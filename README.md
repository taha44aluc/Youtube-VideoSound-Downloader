# YouTube Video/Sound Downloader

Modern ve kullanıcı dostu bir YouTube video ve ses indirme uygulaması.

## Özellikler

- 🎥 **Video İndirme**: 1080p, 720p, 480p, 360p kalitelerinde MP4 formatında
- 🎵 **Ses İndirme**: MP3 ve WAV formatlarında yüksek kalitede ses
- 🌙 **Modern Koyu Tema**: Göz yorucu olmayan siyah arayüz
- 📊 **İndirme İzleme**: Gerçek zamanlı progress bar ve hız göstergesi
- 📁 **Otomatik Klasör**: Sistemin Downloads klasörüne otomatik indirme
- ⚡ **Hızlı ve Güvenilir**: yt-dlp altyapısı ile stabil indirme

## Gereksinimler

### Zorunlu Bileşenler
1. **Java 17+** - JavaFX uygulaması çalıştırmak için
2. **yt-dlp.exe** - YouTube video indirme motoru
3. **FFmpeg** - Video/ses işleme için

### Kurulum Adımları

#### 1. yt-dlp Kurulumu
```bash
# Option 1: Manuel indirme
# https://github.com/yt-dlp/yt-dlp/releases/latest adresinden yt-dlp.exe indirin
# C:\youtube-dl\ klasörüne yerleştirin

# Option 2: Chocolatey ile
choco install yt-dlp

# Option 3: Winget ile
winget install yt-dlp
```

#### 2. FFmpeg Kurulumu
```bash
# Option 1: Chocolatey ile
choco install ffmpeg

# Option 2: Winget ile
winget install ffmpeg

# Option 3: Manuel indirme
# https://ffmpeg.org/download.html adresinden indirip PATH'e ekleyin
```

## Kullanım

1. Uygulamayı başlatın
2. YouTube video URL'sini yapıştırın
3. Format seçin (Video veya Ses)
4. Kalite ayarlayın
5. Ses formatını seçin (sadece ses indirme için)
6. "İndir" butonuna tıklayın

## Antivirüs False Positive Sorunu

### Neden Oluşur?
- Java uygulamaları executable olarak paketlendiğinde
- Sistem komutları (yt-dlp, ffmpeg) çalıştırdığı için
- Henüz yaygın olarak kullanılmadığı için

### Çözümler

#### Kullanıcılar İçin:
1. **Windows Defender Exception**: 
   - Windows Security > Virus & threat protection > Exclusions
   - Uygulama klasörünü ekleyin

2. **Antivirus Exception**:
   - Antivirüs yazılımınızda false positive bildirin
   - Uygulama dosyasını güvenli listesine ekleyin

#### Geliştiriciler İçin:

##### 1. Kod İmzalama Sertifikası (Code Signing Certificate)
```powershell
# Microsoft Store Partner Center üzerinden
# veya SSL sertifika sağlayıcılarından (DigiCert, Sectigo, vs.) 
# Code Signing Certificate satın alın

# PowerShell ile imzalama:
Set-AuthenticodeSignature -FilePath "app.exe" -Certificate $cert -TimestampServer "http://timestamp.digicert.com"
```

##### 3. Microsoft SmartScreen Reputation
- Uygulamanızı https://www.microsoft.com/wdsi/submission adresine gönderin
- False positive bildirimi yapın
- Zaman içinde reputation kazanır

##### 4. Paketleme Optimizasyonları
```xml
<!-- pom.xml'e eklenecek -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>3.4.1</version>
    <configuration>
        <filters>
            <filter>
                <artifact>*:*</artifact>
                <excludes>
                    <exclude>META-INF/*.SF</exclude>
                    <exclude>META-INF/*.DSA</exclude>
                    <exclude>META-INF/*.RSA</exclude>
                </excludes>
            </filter>
        </filters>
    </configuration>
</plugin>
```

## Lisans

MIT License - Detaylar için [LICENSE](LICENSE) dosyasına bakın.

## Sorun Bildirimi

Sorunları [GitHub Issues](https://github.com/taha44aluc/Youtube-VideoSound-Downloader/issues) üzerinden bildirebilirsiniz.

## Yasal Uyarı

Bu uygulama yalnızca kişisel kullanım içindir. Telif hakkı korumalı içerikleri indirmek yasal sorunlara yol açabilir. Kullanıcılar sorumluluğu kendileri üstlenir.
