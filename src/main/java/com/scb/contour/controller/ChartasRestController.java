package com.scb.contour.controller;


import com.scb.contour.exception.NotFoundException;
import com.scb.contour.exception.ValidationException;
import com.scb.contour.services.ChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping(path = ChartasRestController.REST_URL)
public class ChartasRestController {

    public static final String REST_URL = "/chartas";

    @Autowired
    private ChartService chartService;

    //Создать новое изображение папируса заданного размера
    @PostMapping()
    public ResponseEntity<String> createEmptyChart(
            @RequestParam("width") int width, @RequestParam("height") int height) {
        System.out.println("createEmptyChart " + width);
        try {
            return new ResponseEntity<String>(chartService.createNewChart(width, height), HttpStatus.CREATED);
        } catch (ValidationException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //    Сохранить восстановленный фрагмент изображения размера
    @PostMapping(path = "{id}")
    public ResponseEntity<?> addFragment(@PathVariable String id,
                                         @RequestParam int x, @RequestParam int y,
                                         @RequestParam int width, @RequestParam int height,
                                         @RequestBody byte[] image) {
        try {
            chartService.addFragment(id, x, y, width, height, image);
//            Тело ответа пустое.
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IOException | NotFoundException | ValidationException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "{id}", produces = "image/bmp")
    public ResponseEntity<byte[]> getFragment(@PathVariable String id,
                                              @RequestParam int x, @RequestParam int y,
                                              @RequestParam int width, @RequestParam int height) {
        System.out.println("getFragment " + height);
        try {
            return new ResponseEntity<>(chartService.getFragment(id, x, y, width, height), HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity<?> deleteChart(@PathVariable String id) {
        chartService.delete(id);
        System.out.println("deleteChart " + id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
