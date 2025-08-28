# YouTube Downloader - Digital Signing Script
# Bu script, uygulamanızı dijital olarak imzalamak için kullanılır

param(
    [Parameter(Mandatory=$true)]
    [string]$CertificatePath,

    [Parameter(Mandatory=$true)]
    [string]$CertificatePassword,

    [Parameter(Mandatory=$false)]
    [string]$TimestampServer = "http://timestamp.digicert.com"
)

Write-Host "YouTube Downloader - Digital Signing Process" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green

# EXE dosyasının varlığını kontrol et
$exePath = "target\YouTubeDownloader.exe"
if (-not (Test-Path $exePath)) {
    Write-Host "Error: $exePath bulunamadı. Önce 'mvn clean package' komutunu çalıştırın." -ForegroundColor Red
    exit 1
}

try {
    # Sertifikayı yükle
    Write-Host "Sertifika yükleniyor: $CertificatePath" -ForegroundColor Yellow
    $cert = Get-PfxCertificate -FilePath $CertificatePath

    # Dosyayı imzala
    Write-Host "EXE dosyası imzalanıyor..." -ForegroundColor Yellow
    Set-AuthenticodeSignature -FilePath $exePath -Certificate $cert -TimestampServer $TimestampServer

    # İmza doğrulaması
    $signature = Get-AuthenticodeSignature -FilePath $exePath
    if ($signature.Status -eq "Valid") {
        Write-Host "✓ Dosya başarıyla imzalandı!" -ForegroundColor Green
        Write-Host "İmza Durumu: $($signature.Status)" -ForegroundColor Green
        Write-Host "İmzalayan: $($signature.SignerCertificate.Subject)" -ForegroundColor Green
    } else {
        Write-Host "✗ İmzalama başarısız: $($signature.Status)" -ForegroundColor Red
        Write-Host "Hata: $($signature.StatusMessage)" -ForegroundColor Red
    }

} catch {
    Write-Host "✗ İmzalama sırasında hata oluştu: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host "`nNot: İmzalanan dosya Windows SmartScreen tarafından tanınması için zaman gerekebilir." -ForegroundColor Cyan
Write-Host "False positive durumunda Microsoft'a şu adresten bildirim yapabilirsiniz:" -ForegroundColor Cyan
Write-Host "https://www.microsoft.com/wdsi/submission" -ForegroundColor Blue
