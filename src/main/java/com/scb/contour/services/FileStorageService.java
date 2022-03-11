package com.scb.contour.services;


import com.scb.contour.exception.FolderException;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service("fileStorageService")
public class FileStorageService implements FileService {
    private final Path path = Paths.get(System.getProperty("user.dir") + "/fileStorage");
    private final String extension = ".bmp";

    private void init() {
        try {
            Files.createDirectory(path);
            System.out.println("Инициализация каталога " + path);
        } catch (IOException e) {
            System.out.println("Невозможно инициализировать каталог " + path);
            throw new FolderException("Cold not initialize folder for upload.");
        }
    }

    @Override
    public void createBmp(String identifier, int width, int height) {
        if (!Files.exists(this.path)) {
            this.init();
        }

        System.out.println(identifier);
        String newFileName = identifier + extension;
        System.out.println(newFileName);
        String newPath = String.valueOf(this.path);

        File file = null;
        try {
            file = Files.createFile(Paths.get(newPath, newFileName)).toFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ImageIO.write(
                    new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB),
                    "bmp",
                    file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean deleteFile(String id) {
        String filename = id + extension;
        System.out.println(filename);
        boolean isDeletingFile = false;
        try {
            isDeletingFile = Files.deleteIfExists(Paths.get(String.valueOf(path), filename));
            System.out.println(isDeletingFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isDeletingFile;
    }

    @Override
    public void insertPartBmp(String id,
                              int x, int y,
                              int width, int height, byte[] imageBytes) throws IOException {
        System.out.println("1");
        Path isExists = fileExists(id);
        System.out.println("Path" + isExists.toString());

        BufferedImage bmpImage = ImageIO.read(isExists.toFile());
        System.out.println("bmpImage " + bmpImage.getHeight());

        //перевели старый файл в поток
        ImageOutputStream output = ImageIO.createImageOutputStream(isExists.toFile());
        // прочитали новый
        BufferedImage imagePart = ImageIO.read(new ByteArrayInputStream(imageBytes));

        bmpImage.getGraphics().drawImage(imagePart, x, y, width, height, null);
        ImageIO.write(bmpImage, "bmp", output);
        output.close();
    }

    @Override
    public byte[] getFilePart(BufferedImage file, int x, int y, int width, int height) throws IOException {
        int partWidth = Math.min(file.getWidth() - x, width);
        int partHeight = Math.min(file.getHeight() - y, height);

        BufferedImage imagePart = file.getSubimage(x, y, partWidth, partHeight);
        if (partWidth != width || partHeight != height) {
            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            resizedImage.getGraphics().drawImage(imagePart, 0, 0, null);
            imagePart = resizedImage;
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(imagePart, "bmp", stream);
        return stream.toByteArray();
    }

    public Path fileExists(String id) {
        String filename = id + extension;
        Path pathCharts = Paths.get(String.valueOf(path), filename);
        if (Files.exists(path)) {
            return pathCharts;
        } else {
            return null;
        }
    }
}
