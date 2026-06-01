$root = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $root
$mvnCmd = (Get-Command mvn -ErrorAction SilentlyContinue)
if ($null -eq $mvnCmd) {
  $localMvn = "C:\Users\Matthews\tools\apache-maven-3.9.9\bin\mvn.cmd"
  if (Test-Path $localMvn) {
    $mvnCmd = $localMvn
  } else {
    Write-Error "Maven no esta instalado. Instala Maven 3.9+ y agrega 'mvn' al PATH."
    exit 1
  }
}
& $mvnCmd -q -DskipTests compile
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
Write-Host "Compilacion Maven OK. Ejecutar: & `"$mvnCmd`" -q exec:java"
