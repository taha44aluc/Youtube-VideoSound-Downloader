# YouTube Downloader - Build and Release Script
# Bu script uygulamayı derler, paketler ve yayına hazırlar

Write-Host "YouTube Downloader - Build Process" -ForegroundColor Green
Write-Host "==================================" -ForegroundColor Green

# 1. Temizleme ve derleme
Write-Host "1. Proje temizleniyor ve derleniyor..." -ForegroundColor Yellow
mvn clean compile

if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ Derleme başarısız!" -ForegroundColor Red
    exit 1
}

# 2. JAR ve EXE oluşturma
Write-Host "2. JAR ve EXE dosyaları oluşturuluyor..." -ForegroundColor Yellow
mvn package

if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ Paketleme başarısız!" -ForegroundColor Red
    exit 1
}

# 3. Dosya kontrolü
$exePath = "target\YouTubeDownloader.exe"
$jarPath = "target\youtube-video-sound-downloader-1.0-SNAPSHOT-shaded.jar"

if (Test-Path $exePath) {
    Write-Host "✓ EXE dosyası oluşturuldu: $exePath" -ForegroundColor Green
    $exeSize = (Get-Item $exePath).Length / 1MB
    Write-Host "  Dosya boyutu: $([math]::Round($exeSize, 2)) MB" -ForegroundColor Cyan
} else {
    Write-Host "✗ EXE dosyası oluşturulamadı!" -ForegroundColor Red
}

if (Test-Path $jarPath) {
    Write-Host "✓ JAR dosyası oluşturuldu: $jarPath" -ForegroundColor Green
    $jarSize = (Get-Item $jarPath).Length / 1MB
    Write-Host "  Dosya boyutu: $([math]::Round($jarSize, 2)) MB" -ForegroundColor Cyan
} else {
    Write-Host "✗ JAR dosyası oluşturulamadı!" -ForegroundColor Red
}

Write-Host "`n4. Antivirüs False Positive Azaltma İpuçları:" -ForegroundColor Yellow
Write-Host "- Dosyayı VirusTotal'a yükleyerek tarayın" -ForegroundColor Cyan
Write-Host "- Code Signing Certificate ile imzalayın" -ForegroundColor Cyan
Write-Host "- Microsoft SmartScreen'e false positive bildirin" -ForegroundColor Cyan
Write-Host "- Uygulamayı GitHub Releases üzerinden yayınlayın" -ForegroundColor Cyan

Write-Host "`n5. Dağıtım için hazır dosyalar:" -ForegroundColor Green
Write-Host "- EXE: $exePath" -ForegroundColor White
Write-Host "- JAR: $jarPath" -ForegroundColor White
Write-Host "- README: README.md" -ForegroundColor White
Write-Host "- LICENSE: LICENSE" -ForegroundColor White
