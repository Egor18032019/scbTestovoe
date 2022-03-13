package com.scb.contour.services;

import com.scb.contour.exception.NotFoundException;
import com.scb.contour.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

import static com.scb.contour.utils.Const.MAXHEIGHT;
import static com.scb.contour.utils.Const.MAXWIDTH;


@Service
public class ChartService {
    FileService fileStorageService;

    public ChartService(@Autowired FileService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }


    public String createNewChart(int width, int height) throws ValidationException {
        boolean isRightSize = validateChartSize(width, height, MAXWIDTH, MAXHEIGHT);
        if (isRightSize) {
            String identifier = UUID.randomUUID().toString();
            fileStorageService.createBmp(identifier, width, height);
//        В теле ответа возвращается {id} — уникальный идентификатор изображения в строковом представлении.
            return identifier;
        } else {
            throw new ValidationException("Не правильно заданные размеры ");
        }
    }

    public void addFragment(String id, int x, int y,
                            int width, int height,
                            byte[] image) throws IOException, ValidationException, NotFoundException {

        Path isExists = fileStorageService.fileExists(id);
        boolean isHaveThisIdForFileStorage = isExists != null;
        System.out.println("isHaveThisIdForFileStorage " + isHaveThisIdForFileStorage);
        if (isHaveThisIdForFileStorage) {
            BufferedImage bmpImage = ImageIO.read(isExists.toFile());

            boolean isRightCoordinate = validateCoordinateChats(x, y, width, height, bmpImage.getWidth(), bmpImage.getHeight());
            System.out.println("isRightCoordinate " + isRightCoordinate);
            if (isRightCoordinate) {
                fileStorageService.insertPartBmp(isExists, bmpImage, x, y, width, height, image);
            } else {
                System.out.println("ValidationException");
                throw new ValidationException("Не правильные координаты или размеры ");
            }
        } else {
            System.out.println("NotFoundException");
            throw new NotFoundException("Нет файла с таким названием " + id);
        }
    }


    //    Тело ответа: изображение в формате BMP (цвет в RGB, 24 бита на 1 пиксель).
    public byte[] getFragment(String id, int x, int y, int width, int height) throws IOException, ValidationException, NotFoundException {
        Path isExists = fileStorageService.fileExists(id);
        boolean isHaveThisIdForFileStorage = fileStorageService.fileExists(id) != null;
        if (isHaveThisIdForFileStorage) {
            BufferedImage chart = ImageIO.read(isExists.toFile());
            boolean isRightCoordinate = validateCoordinateChats(x, y, width, height, chart.getWidth(), chart.getHeight());
            if (isRightCoordinate) {
                // передаём файл и путь что бы второй раз к этому не обращаться
                return fileStorageService.getFilePart(chart, x, y, width, height);
            } else {
                throw new ValidationException("Не правильные координаты или размеры ");
            }
        } else {
            throw new NotFoundException("Charts с id = " + id + " не найден.");
        }
    }

    public void delete(String id) throws NotFoundException {
        fileStorageService.deleteFile(id);
    }

    //положительные целые числа, не превосходящие 20 000 и 50 000, соответственно
    private boolean validateChartSize(int width, int height, int maxWidth, int maxHeight) {
        boolean isRightSizeChart = width >= 0 && height >= 0 && width <= maxWidth && height <= maxHeight;
        return isRightSizeChart;
    }

    private boolean validateCoordinateChats(int x, int y, int fragmentFileWidth, int fragmentFileHeight, int fileWidth, int fileHeight) {
        System.out.println("fileWidth " + fileWidth + " fragmentFileWidth " + fragmentFileWidth);
        System.out.println("fileHeight " + fileHeight + " fragmentFileHeight " + fragmentFileHeight);
        boolean isRightCoordinatefragmentChart =
                x >= 0 && y >= 0 &&
                        x <= fileWidth && y <= fileHeight &&
                        fileWidth >= fragmentFileWidth && fileHeight >= fragmentFileHeight &&
                        fileWidth > 0 && fileHeight > 0;
        return isRightCoordinatefragmentChart;
    }


}
