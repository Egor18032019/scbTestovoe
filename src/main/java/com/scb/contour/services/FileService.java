package com.scb.contour.services;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

public interface FileService {


    void createBmp(String identifier, int width, int weight);

    boolean deleteFile(String id);

    byte[] getFilePart(BufferedImage file, int x, int y, int width, int height) throws IOException;

    Path fileExists(String id);

    void insertPartBmp(Path isExists, BufferedImage bmpImage, int x, int y, int width, int height, byte[] image) throws IOException;
}
