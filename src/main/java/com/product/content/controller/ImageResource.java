package com.product.content.controller;

import com.product.content.model.Image;
import com.product.content.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("admin/image")
public class ImageResource {
    @Autowired
    ImageService imageService;

    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<Image> handleFileUpload(@RequestParam("file") MultipartFile file) {
        String message = "";
        try {
            Image image = imageService.save(file);
            return ResponseEntity.status(HttpStatus.OK).body(image);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/metadata/all")
    public ResponseEntity<List<Image>> getAll(){
        return ResponseEntity.status(HttpStatus.OK).body(imageService.getAll());
    }

    @GetMapping("fullsize")
    public ResponseEntity<byte[]> getByName(@RequestParam("fileName") String fileName){
        return ResponseEntity.status(HttpStatus.OK).contentType(getImageType(fileName)).body(imageService.getFullImage(fileName));
    }

    @GetMapping("thumbnail")
    public ResponseEntity<byte[]> getByNameThumbnail(@RequestParam("fileName") String fileName){
        return ResponseEntity.status(HttpStatus.OK).contentType(getImageType(fileName)).body(imageService.getThumbnail(fileName));
    }

    @DeleteMapping
    @CrossOrigin
    public ResponseEntity<Image> delete(@RequestParam("fileName") String fileName){
        return ResponseEntity.status(HttpStatus.OK).body(imageService.delete(fileName));
    }

    private MediaType getImageType(String fileName){
        String fileNameLowerCase = fileName.toLowerCase();
        boolean isJpg = fileNameLowerCase.contains(".jpg") || fileNameLowerCase.contains("jpeg");
        return isJpg ? MediaType.IMAGE_JPEG : MediaType.IMAGE_PNG;
    }

}
