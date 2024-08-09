package org.tetris.common.manager;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotManager {
    private static final String DIRECTORY = System.getProperty("user.dir") + "/screenshot/";
    private static final String FORMAT = "png";

    public static void screenshot(Scene view) {
        Platform.runLater(() -> {
            try {
                // Створення BufferedImage
                WritableImage image = new WritableImage(
                        (int) view.getWidth(),
                        (int) view.getHeight()
                );
                view.snapshot(image);

                // Створення імені для файлу
                String fullFileName = createFileName();

                // Збереження зображення в файл
                Path path = saveImage(image, fullFileName);

                System.out.println("Зображення успішно збережено в " + path.toAbsolutePath());
            } catch (IOException e) {
                System.err.println("Помилка при збереженні зображення: " + e.getMessage());
            }
        });
    }

    private static String createFileName() {
        LocalDateTime dateTime = LocalDateTime.now();
        String date = DateTimeFormatter
                .ofPattern("yyyy-MM-dd")
                .format(dateTime);
        String time = DateTimeFormatter
                .ofPattern("HH-mm-ss")
                .format(dateTime);
        String fileName = String.format(
                "Screenshot_D%s_T%s_Tetris",
                date,
                time
        );

        // Робить унікальну назву для файлу
        return createUniqueFileName(fileName);
    }

    private static String createUniqueFileName(String fileName) {
        String fullFileName = DIRECTORY + fileName + "." + FORMAT;

        // Перевірка наявності файлу та додавання нумерації у випадку збігів
        int fileNumber = 1;
        String newFullFileName;
        while (Files.exists(Paths.get(fullFileName))) {
            newFullFileName = String.format(
                    "%s_(%d)",
                    fileName,
                    fileNumber
            );
            fullFileName = DIRECTORY + newFullFileName + "." + FORMAT;
            ++fileNumber;
        }
        return fullFileName;
    }

    /**
     * Створює скріншот екрану і зберігає його в папку "screenshot"
     * @param image
     * @param fullFileName
     * @return
     * @throws IOException
     */
    private static Path saveImage(Image image, String fullFileName) throws IOException {
        Path path = Paths.get(fullFileName);
        Files.createDirectories(path.getParent());
        ImageIO.write(
                SwingFXUtils.fromFXImage(image, null),
                FORMAT,
                path.toFile()
        );
        return path;
    }
}