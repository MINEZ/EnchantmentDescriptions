# EnchantmentDescriptions（MINEZ Fork）

[![MINEZ 版本](docs/badges/version.svg)](https://github.com/MINEZ/EnchantmentDescriptions/releases)

这是 [Enchantment Descriptions](https://github.com/Darkhax-Minecraft/Enchantment-Descriptions) 的 MINEZ Fork，面向 Minecraft 26.3 的 Fabric 客户端。

模组为物品提示添加附魔说明。本 Fork 修复了追加按键提示时修改不可变列表导致客户端崩溃的问题，并保留隐藏物品提示的原有行为。模组 ID 仍为 `enchdesc`。

## 安装

1. 关闭客户端。
2. 从 [GitHub Releases](https://github.com/MINEZ/EnchantmentDescriptions/releases) 获取 Fabric 产物，或按下文自行构建。
3. 将产物放入当前游戏实例的 `mods` 目录，替换已有的 EnchantmentDescriptions 文件。
4. 保留与你的 Minecraft 版本匹配的 Fabric API 和 Prickle 依赖，然后启动客户端。

修复后可以正常使用 `require_keybind` 和 `display_activate_text`，无需关闭按键提示来绕过本次崩溃。

## 构建

使用 JDK 25，在 `26.3` 分支构建。Windows PowerShell 示例：

```powershell
Set-Location 'C:\Users\wichi\Desktop\MinezEnchantmentDescriptions'
powershell -NoProfile -ExecutionPolicy Bypass -File .\build-fabric.ps1
```

Fabric 产物位于 `fabric/build/libs`，文件名遵循 `EnchantmentDescriptions-fabric-MC26.3-<版本号>.jar`。

`build-fabric.ps1` 默认执行 `:fabric:build`。如需清理旧产物后构建，可以传入 Gradle 参数：

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\build-fabric.ps1 clean :fabric:build
```

脚本读取 `fork-version.txt`，临时设置上游构建插件使用的 `BUILD_NUMBER`，并在结束时恢复原来的环境变量和工作目录。直接调用 Gradle 时，需要自行设置相应的构建号。

构建会自动生成本地版本徽章，并恢复 Fork 的 README 说明。徽章显示的是本 Fork 的版本，不依赖上游 Maven 版本接口。

模组元数据中的项目和下载入口指向本 Fork。NeoForge 产物不再检查上游更新地址，本 Fork 的更新以 GitHub Releases 为准。

## 版本规则

版本号采用“上游版本号 + Fork 修订号”的形式。当前版本为 `26.3.0.2.1`，基于上游 `26.3.0.2`，Fork 修订号为 `1`。

提升版本时修改 `fork-version.txt`，同步更新本节中的版本信息，并准备 GitHub Releases 更新日志。构建时会校验实际产物版本号与该文件是否一致。

## 修复原理与验证

原有 `afterEnchantmentTooltips` 回调会直接向 `getTooltipLines` 的返回列表追加按键提示。该列表可能不可修改，直接调用 `add` 会触发 `UnsupportedOperationException`。

修复后的回调保留空列表；需要追加文字时，复制原列表，再通过允许取消的 Mixin 回调替换返回值。原有提示内容和顺序保持不变，也不会修改调用方的原列表。

安装修复版后，建议验证以下场景：

- 未按下 Shift 时，附魔物品的按键提示正常显示。
- 按下 Shift 时，附魔说明正常显示。
- 隐藏物品提示的物品不会额外显示按键提示。
- 关闭 `display_activate_text` 或模组功能后，不会出现按键提示。

## 项目信息

- [英文说明](README.md)。
- [问题反馈](https://github.com/MINEZ/EnchantmentDescriptions/issues)。
- [当前版本更新日志](docs/releases/26.3.0.2.1.md)。

原作者为 Darkhax，Fork 维护者为 BredyAK。项目继续使用 LGPL 2.1 许可证，详见 [LICENSE](LICENSE)。
