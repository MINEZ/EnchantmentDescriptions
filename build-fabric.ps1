[CmdletBinding()]
param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]] $GradleArguments
)

$ErrorActionPreference = 'Stop'
$forkVersion = (Get-Content -LiteralPath (Join-Path $PSScriptRoot 'fork-version.txt') -Raw -Encoding UTF8).Trim()
if ($forkVersion -notmatch '^26\.3\.0\.(\d+\.[1-9]\d*)$') {
    throw 'fork-version.txt 中的版本号格式无效，必须包含上游版本号和 Fork 修订号。'
}
$forkBuildNumber = $Matches[1]
if ($null -eq $GradleArguments -or $GradleArguments.Count -eq 0) {
    $GradleArguments = @(':fabric:build')
}

$previousBuildNumber = [Environment]::GetEnvironmentVariable('BUILD_NUMBER', 'Process')
$previousLocation = Get-Location
try {
    Set-Location -LiteralPath $PSScriptRoot
    [Environment]::SetEnvironmentVariable('BUILD_NUMBER', $forkBuildNumber, 'Process')
    Write-Host ('正在构建 EnchantmentDescriptions {0}。' -f $forkVersion)
    & (Join-Path $PSScriptRoot 'gradlew.bat') @GradleArguments
    if ($LASTEXITCODE -ne 0) {
        throw ('Gradle 构建失败，退出码为 {0}。' -f $LASTEXITCODE)
    }
}
finally {
    [Environment]::SetEnvironmentVariable('BUILD_NUMBER', $previousBuildNumber, 'Process')
    Set-Location -LiteralPath $previousLocation.Path
}
