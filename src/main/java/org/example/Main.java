/*
 * MIT License
 *
 * Copyright (c) 2025 YouTube Video/Sound Downloader
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    private TextField urlField;
    private ComboBox<String> formatChoice;
    private ComboBox<String> qualityChoice;
    private ComboBox<String> audioFormatChoice;
    private Label statusLabel;
    private Label downloadSpeedLabel;
    private String saveDirectory;
    private ProgressBar progressBar;
    private ProgressIndicator progressIndicator;
    private Label progressLabel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("YouTube İndirici");

        // Varsayılan indirme konumu ayarla
        String userHome = System.getProperty("user.home");
        saveDirectory = userHome + File.separator + "Downloads";

        // İndirme klasörü yoksa oluştur
        File downloadDir = new File(saveDirectory);
        if (!downloadDir.exists() && !downloadDir.mkdirs()) {
            System.err.println("İndirme klasörü oluşturulamadı!");
        }

        // Icon ekle
        try {
            // Önce jar içindeki resource'tan icon yükle
            Image icon = new Image(getClass().getResourceAsStream("/icon.png"));
            if (icon != null && !icon.isError()) {
                primaryStage.getIcons().add(icon);
            } else {
                // Alternatif: ico dosyasını dene
                Image altIcon = new Image("file:src/ico/images.ico");
                if (altIcon != null && !altIcon.isError()) {
                    primaryStage.getIcons().add(altIcon);
                }
            }
        } catch (Exception e) {
            // Son çare: dosya sisteminden yükle
            try {
                File iconFile = new File("src/ico/images.ico");
                if (iconFile.exists()) {
                    primaryStage.getIcons().add(new Image(iconFile.toURI().toString()));
                }
            } catch (Exception ex) {
                System.err.println("Hiçbir icon yüklenemedi: " + ex.getMessage());
            }
        }

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #1E1E1E;"); // Koyu arka plan

        // URL girişi
        Label urlLabel = new Label("YouTube URL:");
        urlLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #E0E0E0;");
        urlField = new TextField();
        urlField.setPromptText("YouTube video URL'sini yapıştırın");
        urlField.setStyle("-fx-padding: 10; -fx-font-size: 13px; -fx-background-color: #2D2D2D; " +
                         "-fx-text-fill: #E0E0E0; -fx-prompt-text-fill: #808080; -fx-border-color: #3D3D3D; " +
                         "-fx-border-radius: 5; -fx-background-radius: 5;");

        // ComboBox'lar için genel stil tanımı
        String comboBoxStyle = "-fx-padding: 5; " +
                             "-fx-font-size: 13px; " +
                             "-fx-background-color: #2D2D2D; " +
                             "-fx-text-fill: white; " +
                             "-fx-border-color: #3D3D3D; " +
                             "-fx-border-radius: 5; " +
                             "-fx-background-radius: 5; " +
                             "-fx-mark-color: white;"; // Ok işaretini beyaz yap

        // Format seçimi
        Label formatLabel = new Label("Format:");
        formatLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #E0E0E0;");
        formatChoice = new ComboBox<>();
        formatChoice.getItems().addAll("Video", "Ses");
        formatChoice.setValue("Video");
        formatChoice.setStyle(comboBoxStyle);

        // ComboBox popup stilini ayarla
        formatChoice.setButtonCell(createStyledCell());
        formatChoice.setCellFactory(listView -> createStyledCell());

        // Kalite seçimi
        Label qualityLabel = new Label("Kalite:");
        qualityLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #E0E0E0;");
        qualityChoice = new ComboBox<>();
        qualityChoice.setStyle(comboBoxStyle);

        // Başlangıçta video kalitelerini ayarla
        qualityChoice.getItems().addAll("1080p", "720p", "480p", "360p");
        qualityChoice.setValue("1080p");

        // Kalite ComboBox popup stilini ayarla
        qualityChoice.setButtonCell(createStyledCell());
        qualityChoice.setCellFactory(listView -> createStyledCell());

        // Ses formatı seçimi
        Label audioFormatLabel = new Label("Ses Formatı:");
        audioFormatLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #E0E0E0;");
        audioFormatChoice = new ComboBox<>();
        audioFormatChoice.getItems().addAll("MP3", "WAV");
        audioFormatChoice.setValue("MP3");
        audioFormatChoice.setDisable(true);
        audioFormatChoice.setStyle(comboBoxStyle);

        // Ses formatı ComboBox popup stilini ayarla
        audioFormatChoice.setButtonCell(createStyledCell());
        audioFormatChoice.setCellFactory(listView -> createStyledCell());

        // Format değiştiğinde yapılacak işlemler
        formatChoice.setOnAction(e -> {
            String selectedFormat = formatChoice.getValue();
            qualityChoice.getItems().clear();
            if (selectedFormat.equals("Video")) {
                qualityChoice.getItems().addAll("1080p", "720p", "480p", "360p");
                qualityChoice.setValue("1080p");
                audioFormatChoice.setDisable(true);
            } else {
                qualityChoice.getItems().addAll("En İyi Kalite", "Orta Kalite", "Düşük Kalite");
                qualityChoice.setValue("En İyi Kalite");
                audioFormatChoice.setDisable(false);
            }
        });

        // Tüm ComboBox'lar için popup stil ayarları
        String popupStyle = "-fx-background-color: #2D2D2D;";
        formatChoice.getStyleClass().add("combo-box-popup");
        qualityChoice.getStyleClass().add("combo-box-popup");
        audioFormatChoice.getStyleClass().add("combo-box-popup");

        // İndirme butonu oluştur
        Button downloadButton = createDownloadButton();

        // Durum etiketi ve indirme konumu
        Label locationLabel = new Label("İndirme Konumu: " + saveDirectory);
        locationLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #808080;");
        locationLabel.setWrapText(true);

        // İndirme durumu için container
        VBox downloadStatusContainer = new VBox(5);
        downloadStatusContainer.setAlignment(Pos.CENTER);

        // Progress bar ve yüzde
        HBox progressContainer = new HBox(10);
        progressContainer.setAlignment(Pos.CENTER_LEFT);

        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(350);
        progressBar.setStyle("-fx-accent: #007ACC; -fx-control-inner-background: #2D2D2D;");
        progressBar.setVisible(false);

        progressLabel = new Label("0%");
        progressLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #E0E0E0;");
        progressLabel.setVisible(false);

        progressIndicator = new ProgressIndicator(0);
        progressIndicator.setStyle("-fx-progress-color: #007ACC;");
        progressIndicator.setPrefSize(25, 25);
        progressIndicator.setVisible(false);

        progressContainer.getChildren().addAll(progressBar, progressLabel, progressIndicator);

        // İndirme hızı
        downloadSpeedLabel = new Label("");
        downloadSpeedLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #808080;");
        downloadSpeedLabel.setVisible(false);

        // Durum etiketi
        statusLabel = new Label("");
        statusLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #E0E0E0;");
        statusLabel.setWrapText(true);

        downloadStatusContainer.getChildren().addAll(progressContainer, downloadSpeedLabel, statusLabel);

        // Format seçimi için HBox
        HBox formatBox = new HBox(10);
        formatBox.setAlignment(Pos.CENTER_LEFT);
        formatBox.getChildren().addAll(formatLabel, formatChoice);

        // Kalite seçimi için HBox
        HBox qualityBox = new HBox(10);
        qualityBox.setAlignment(Pos.CENTER_LEFT);
        qualityBox.getChildren().addAll(qualityLabel, qualityChoice);

        // Ses formatı seçimi için HBox
        HBox audioFormatBox = new HBox(10);
        audioFormatBox.setAlignment(Pos.CENTER_LEFT);
        audioFormatBox.getChildren().addAll(audioFormatLabel, audioFormatChoice);

        root.getChildren().addAll(
                urlLabel, urlField,
                formatBox,
                qualityBox,
                audioFormatBox,
                locationLabel,
                downloadButton,
                downloadStatusContainer
        );

        Scene scene = new Scene(root, 500, 450);
        scene.getStylesheets().add("data:text/css," +
            ".combo-box-popup .list-view {" +
            "    -fx-background-color: #2D2D2D;" +
            "    -fx-border-color: #3D3D3D;" +
            "}" +
            ".combo-box-popup .list-cell {" +
            "    -fx-background-color: #2D2D2D;" +
            "    -fx-text-fill: white;" +
            "    -fx-padding: 5;" +
            "}" +
            ".combo-box-popup .list-cell:hover {" +
            "    -fx-background-color: #3D3D3D;" +
            "}" +
            ".combo-box .list-cell {" +
            "    -fx-text-fill: white;" +
            "    -fx-background-color: #2D2D2D;" +
            "}" +
            ".combo-box-base {" +
            "    -fx-background-color: #2D2D2D;" +
            "    -fx-border-color: #3D3D3D;" +
            "}" +
            ".combo-box-base:hover {" +
            "    -fx-background-color: #3D3D3D;" +
            "}" +
            ".combo-box .arrow {" +
            "    -fx-background-color: white;" +
            "}"
        );
        scene.setFill(Color.web("#1E1E1E"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createDownloadButton() {
        Button downloadButton = new Button("İndir");
        String buttonStyle = "-fx-background-color: #007ACC; -fx-text-fill: white; " +
                           "-fx-font-weight: bold; -fx-padding: 10 30; -fx-font-size: 14px; " +
                           "-fx-background-radius: 5; -fx-cursor: hand;";

        downloadButton.setStyle(buttonStyle);

        downloadButton.setOnMouseEntered(e ->
            downloadButton.setStyle(buttonStyle.replace("#007ACC", "#1C8CDC")));

        downloadButton.setOnMouseExited(e ->
            downloadButton.setStyle(buttonStyle));

        downloadButton.setOnAction(e -> startDownload());

        return downloadButton;
    }

    private void updateProgress(String line) {
        if (line.contains("[download]")) {
            try {
                // İndirme yüzdesini parse et
                if (line.contains("%")) {
                    String percentStr = line.substring(line.indexOf(" ") + 1, line.indexOf("%")).trim();
                    double percent = Double.parseDouble(percentStr);
                    double progress = percent / 100.0;

                    Platform.runLater(() -> {
                        progressBar.setProgress(progress);
                        progressIndicator.setProgress(progress);
                        progressLabel.setText(String.format("%.1f%%", percent));
                        progressBar.setVisible(true);
                        progressLabel.setVisible(true);
                        progressIndicator.setVisible(true);
                    });
                }

                // İndirme hızını parse et
                if (line.contains("at") && line.contains("/s")) {
                    String speedStr = line.substring(line.lastIndexOf("at ") + 3, line.lastIndexOf("/s") + 2);
                    Platform.runLater(() -> {
                        downloadSpeedLabel.setText("İndirme Hızı: " + speedStr);
                        downloadSpeedLabel.setVisible(true);
                    });
                }
            } catch (Exception e) {
                // Parse hatalarını görmezden gel
            }
        }
    }

    private String getFormatString(String format, String quality) {
        if (format.equals("Video")) {
            return switch (quality) {
                case "1080p" -> "bestvideo[height<=1080][ext=mp4]+bestaudio[ext=m4a]/best[height<=1080][ext=mp4]";
                case "720p" -> "bestvideo[height<=720][ext=mp4]+bestaudio[ext=m4a]/best[height<=720][ext=mp4]";
                case "480p" -> "bestvideo[height<=480][ext=mp4]+bestaudio[ext=m4a]/best[height<=480][ext=mp4]";
                case "360p" -> "bestvideo[height<=360][ext=mp4]+bestaudio[ext=m4a]/best[height<=360][ext=mp4]";
                default -> "best[ext=mp4]";
            };
        } else {
            return switch (quality) {
                case "En İyi Kalite" -> "bestaudio[ext=m4a]";
                case "Orta Kalite" -> "bestaudio[abr<=128][ext=m4a]";
                case "Düşük Kalite" -> "worstaudio[ext=m4a]";
                default -> "bestaudio[ext=m4a]";
            };
        }
    }

    private void startDownload() {
        if (saveDirectory == null) {
            showAlert("Hata", "Lütfen önce kayıt klasörü seçin.");
            return;
        }

        String videoUrl = urlField.getText().trim();
        if (videoUrl.isEmpty()) {
            showAlert("Hata", "Lütfen bir YouTube URL'si girin.");
            return;
        }

        updateStatus("İndirme başlatılıyor...");

        // İndirme başlamadan önce progress bar'ı sıfırla
        Platform.runLater(() -> {
            progressBar.setProgress(0);
            progressIndicator.setProgress(0);
            progressLabel.setText("0%");
            progressBar.setVisible(true);
            progressLabel.setVisible(true);
            progressIndicator.setVisible(true);
            downloadSpeedLabel.setText("");
            downloadSpeedLabel.setVisible(true);
        });

        new Thread(() -> {
            try {
                // FFmpeg'in yüklü olup olmadığını kontrol et
                ProcessBuilder ffmpegCheck = new ProcessBuilder("ffmpeg", "-version");
                try {
                    Process ffmpegProcess = ffmpegCheck.start();
                    ffmpegProcess.waitFor();
                } catch (Exception e) {
                    throw new Exception("FFmpeg bulunamadı. Lütfen FFmpeg'i yükleyin ve PATH'e ekleyin.");
                }

                String format = formatChoice.getValue();
                String quality = qualityChoice.getValue();
                String formatStr = getFormatString(format, quality);

                String outputTemplate = saveDirectory + File.separator + "%(title)s.%(ext)s";

                // Olası yt-dlp yolları
                String[] possiblePaths = {
                    "C:\\youtube-dl\\yt-dlp.exe",  // Özel kurulum yolu
                    System.getenv("LOCALAPPDATA") + "\\Microsoft\\WinGet\\Packages\\yt-dlp_yt-dlp\\yt-dlp.exe",
                    System.getenv("LOCALAPPDATA") + "\\Microsoft\\WinGet\\Packages\\yt-dlp.yt-dlp_yt-dlp\\yt-dlp.exe",
                    "C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Local\\Programs\\yt-dlp\\yt-dlp.exe",
                    "yt-dlp.exe"  // PATH'te olması durumunda
                };

                String ytDlpPath = null;
                for (String path : possiblePaths) {
                    File f = new File(path);
                    if (f.exists()) {
                        ytDlpPath = path;
                        updateLog("yt-dlp bulundu: " + path);
                        break;
                    }
                }

                if (ytDlpPath == null) {
                    throw new Exception("yt-dlp bulunamadı. Lütfen C:\\youtube-dl klasörüne yt-dlp.exe'yi yükleyin.");
                }

                List<String> command = new ArrayList<>();
                command.add(ytDlpPath);
                command.add(videoUrl);
                command.add("-f");
                command.add(formatStr);

                if (format.equals("Video")) {
                    command.add("--merge-output-format");
                    command.add("mp4");
                }

                if (!format.equals("Video")) {
                    command.add("--extract-audio");
                    command.add("--audio-format");
                    command.add(audioFormatChoice.getValue().toLowerCase());
                }

                command.add("-o");
                command.add(outputTemplate);

                ProcessBuilder pb = new ProcessBuilder(command);
                pb.redirectErrorStream(true);
                Process process = pb.start();

                // Çıktıyı oku ve göster
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        final String finalLine = line;
                        updateProgress(finalLine);
                        Platform.runLater(() -> updateStatus(finalLine));
                    }
                }

                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    Platform.runLater(() -> {
                        updateStatus("İndirme başarıyla tamamlandı!");
                        progressBar.setProgress(1.0);
                        progressIndicator.setProgress(1.0);
                        progressLabel.setText("100%");
                        downloadSpeedLabel.setText("");
                    });
                } else {
                    throw new Exception("İndirme işlemi başarısız oldu (Çıkış kodu: " + exitCode + ")");
                }
            } catch (Exception e) {
                final String errorMessage = e.getMessage();
                Platform.runLater(() -> {
                    updateStatus("Hata: " + errorMessage);
                    showAlert("Hata", errorMessage);
                    progressBar.setVisible(false);
                    progressLabel.setVisible(false);
                    progressIndicator.setVisible(false);
                    downloadSpeedLabel.setVisible(false);
                });
            }
        }).start();
    }

    private void updateStatus(String message) {
        if (message.contains("[download]") || message.contains("[ffmpeg]")) {
            statusLabel.setText(message);
        }
    }

    private void updateLog(String message) {
        if (message != null && !message.trim().isEmpty()) {
            Platform.runLater(() -> statusLabel.setText(message));
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title != null ? title : "Hata");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private ListCell<String> createStyledCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: white; -fx-background-color: #2D2D2D;");
                }
            }
        };
    }

    public static void main(String[] args) {
        launch(args);
    }
}
