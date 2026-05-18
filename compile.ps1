$root = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $root
New-Item -ItemType Directory -Force -Path out | Out-Null
javac -encoding UTF-8 -d out -sourcepath src src\Main.java
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
New-Item -ItemType Directory -Force -Path out\i18n | Out-Null
Copy-Item -Force src\i18n\*.properties out\i18n\
Write-Host "Compilacion OK. Ejecutar: java -cp out Main"
