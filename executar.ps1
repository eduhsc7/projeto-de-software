[Console]::OutputEncoding = [System.Text.Encoding]::UTF8; [Console]::InputEncoding = [System.Text.Encoding]::UTF8
New-Item -ItemType Directory -Force -Path "$PSScriptRoot\bin" | Out-Null
$arquivos = (Get-ChildItem -Recurse "$PSScriptRoot\src\*.java").FullName
javac -encoding UTF-8 -d "$PSScriptRoot\bin" $arquivos
if ($LASTEXITCODE -eq 0) {
    java -cp "$PSScriptRoot\bin" Main @args
}
