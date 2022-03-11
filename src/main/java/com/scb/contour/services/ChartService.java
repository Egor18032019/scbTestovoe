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


@Service
public class ChartService {
    FileService fileStorageService;

    public ChartService(@Autowired FileService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }


    public String createNewChart(int width, int height) throws ValidationException {
        int MAXWIDTH = 20000;
        int MAXHEIGHT = 50000;
        if (validateChartSize(width, height, MAXWIDTH, MAXHEIGHT)) {
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


        boolean isRightCoordinate = validateCoordinateChats(x, y, width, height);
        if (isRightCoordinate) {
            // передаём файл и путь что бы второй раз к этому не обращаться
            fileStorageService.insertPartBmp(id, x, y, width, height, image);
        } else {
            throw new ValidationException("Не правильные координаты или размеры ");
        }
    }


    //    Тело ответа: изображение в формате BMP (цвет в RGB, 24 бита на 1 пиксель).
    public byte[] getFragment(String id, int x, int y, int width, int height) throws IOException {
        Path isExists = fileStorageService.fileExists(id);
        if (isExists != null) {
            BufferedImage chart = ImageIO.read(isExists.toFile());
            boolean isRightCoordinate = validateCoordinateChats(x, y, width, height);
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

    public void delete(String id) {
        fileStorageService.deleteFile(id);
    }

    //положительные целые числа, не превосходящие 20 000 и 50 000, соответственно
    private boolean validateChartSize(int width, int height, int maxWidth, int maxHeight) {
        boolean isRightSizeChart = width >= 0 && height >= 0 && width <= maxWidth && height <= maxHeight;
        return isRightSizeChart;
    }

    private boolean validateCoordinateChats(int x, int y, int fileWidth, int fileHeight) {
        boolean isRightCoordinatefragmentChart = x <= fileWidth && y <= fileHeight && x >= 0 && y >= 0;
        return isRightCoordinatefragmentChart;
    }


}
