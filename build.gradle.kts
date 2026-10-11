import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.gradle.api.tasks.bundling.AbstractArchiveTask

plugins {
    id("net.darkhax.mmc") version "26.3.0.2"
}

val forkVersion = file("fork-version.txt").readText(Charsets.UTF_8).trim()
require(Regex("26\\.3\\.0\\.[0-9]+\\.[1-9][0-9]*").matches(forkVersion)) {
    "fork-version.txt 中的版本号必须包含上游版本号和 Fork 修订号。"
}

val verifyForkVersion = tasks.register("verifyForkVersion") {
    group = "verification"
    description = "检查构建版本号是否与 Fork 版本配置一致。"
    doLast {
        check(project.version.toString() == forkVersion) {
            "Fork 版本号应为 $forkVersion，当前为 ${project.version}。请通过 build-fabric.ps1 构建，或设置正确的 BUILD_NUMBER。"
        }
    }
}

allprojects {
    tasks.withType<AbstractArchiveTask>().configureEach {
        dependsOn(verifyForkVersion)
    }
}

// 上游插件会在配置阶段重写 README，在此恢复 Fork 说明和本地版本徽章。
val badgeFile = file("docs/badges/version.svg")
badgeFile.parentFile.mkdirs()
badgeFile.writeText(
    """<svg xmlns="http://www.w3.org/2000/svg" width="166" height="20" role="img" aria-label="版本：$forkVersion">
  <title>版本：$forkVersion</title>
  <linearGradient id="gradient" x2="0" y2="100%">
    <stop offset="0" stop-color="#bbb" stop-opacity=".1"/>
    <stop offset="1" stop-opacity=".1"/>
  </linearGradient>
  <clipPath id="clip"><rect width="166" height="20" rx="3"/></clipPath>
  <g clip-path="url(#clip)">
    <path fill="#555" d="M0 0h54v20H0z"/>
    <path fill="#007ec6" d="M54 0h112v20H54z"/>
    <path fill="url(#gradient)" d="M0 0h166v20H0z"/>
  </g>
  <g fill="#fff" text-anchor="middle" font-family="Verdana,DejaVu Sans,Microsoft YaHei,sans-serif" font-size="11">
    <text x="27" y="15" fill="#010101" fill-opacity=".3">版本</text>
    <text x="27" y="14">版本</text>
    <text x="110" y="15" fill="#010101" fill-opacity=".3">$forkVersion</text>
    <text x="110" y="14">$forkVersion</text>
  </g>
</svg>
""",
    Charsets.UTF_8
)

fun replaceReadmeSection(input: String, name: String, content: String): String {
    val start = "<!-- $name-start -->"
    val end = "<!-- $name-end -->"
    val section = Regex("${Regex.escape(start)}.*?${Regex.escape(end)}", RegexOption.DOT_MATCHES_ALL)
    check(section.containsMatchIn(input)) { "README.md 缺少 $name 区域标记。" }
    return section.replace(input) { "$start\n$content\n$end" }
}

val readmeFile = file("README.md")
var readme = readmeFile.readText(Charsets.UTF_8)
readme = replaceReadmeSection(readme, "name", """
# EnchantmentDescriptions

[![MINEZ version](docs/badges/version.svg)](https://github.com/MINEZ/EnchantmentDescriptions/releases)
""".trimIndent())
readme = replaceReadmeSection(readme, "description", """
This is the MINEZ fork of [Enchantment Descriptions](https://github.com/Darkhax-Minecraft/Enchantment-Descriptions) for Minecraft 26.3. It fixes crashes when an activation hint is added to an immutable tooltip list and preserves hidden tooltips. The original mod ID, `enchdesc`, is retained.

Download this fork from [GitHub Releases](https://github.com/MINEZ/EnchantmentDescriptions/releases) and report fork-specific issues [here](https://github.com/MINEZ/EnchantmentDescriptions/issues). See [简体中文说明](README_zh-CN.md) for build and installation instructions.
""".trimIndent())
readme = replaceReadmeSection(readme, "maven", """
## Build this fork

Use JDK 25 and the `26.3` branch. Open PowerShell in the repository root and run:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\build-fabric.ps1
```

The build script reads `fork-version.txt` and supplies the build number expected by the upstream conventions plugin. The Fabric artifact is `fabric/build/libs/EnchantmentDescriptions-fabric-MC26.3-$forkVersion.jar`.

Replace the existing EnchantmentDescriptions jar with this artifact and keep the required Prickle dependency installed. The MINEZ fork is distributed through GitHub Releases; the upstream CurseForge, Modrinth and Maven pages do not distribute this fork.
""".trimIndent())
readmeFile.writeText(readme, Charsets.UTF_8)

// 元数据由上游插件生成，再将下载和更新入口指向本 Fork。
val forkRepository = "https://github.com/MINEZ/EnchantmentDescriptions"
val fabricMetadataFile = file("fabric/src/main/resources/fabric.mod.json")
@Suppress("UNCHECKED_CAST")
val fabricMetadata = JsonSlurper().parse(fabricMetadataFile, "UTF-8") as MutableMap<String, Any?>
fabricMetadata["authors"] = listOf("Darkhax", "BredyAK")
@Suppress("UNCHECKED_CAST")
val fabricContact = fabricMetadata["contact"] as MutableMap<String, Any?>
fabricContact["homepage"] = "$forkRepository/releases"
@Suppress("UNCHECKED_CAST")
val fabricCustom = fabricMetadata["custom"] as MutableMap<String, Any?>
@Suppress("UNCHECKED_CAST")
val fabricModMenu = fabricCustom["modmenu"] as MutableMap<String, Any?>
fabricModMenu["links"] = mapOf("GitHub" to "$forkRepository/releases")
fabricMetadataFile.writeText(JsonOutput.prettyPrint(JsonOutput.toJson(fabricMetadata), true) + "\n", Charsets.UTF_8)

val neoforgeMetadataFile = file("neoforge/src/main/resources/META-INF/neoforge.mods.toml")
val neoforgeMetadata = neoforgeMetadataFile.readText(Charsets.UTF_8)
    .replace(Regex("^updateJSONURL = .*\\r?\\n", RegexOption.MULTILINE), "")
    .replace(Regex("^displayURL = .*", RegexOption.MULTILINE), "displayURL = \"$forkRepository/releases\"")
neoforgeMetadataFile.writeText(neoforgeMetadata, Charsets.UTF_8)
