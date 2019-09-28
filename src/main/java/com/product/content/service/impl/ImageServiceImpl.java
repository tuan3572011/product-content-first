package com.product.content.service.impl;

import com.product.content.model.Image;
import com.product.content.repository.ImageRepository;
import com.product.content.service.StorageService;
import com.product.content.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@Service
public class ImageServiceImpl implements ImageService{

    @PostConstruct
    private void init(){
        this.storageService.discovery().forEach(image-> this.imageRepository.save(new Image(image)));
    }

    @Autowired
    private StorageService storageService;
    @Autowired
    private ImageRepository imageRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Image save(MultipartFile file) {
        if(imageRepository.findById(file.getName()).isPresent()){
            throw new IllegalArgumentException("Image already exist");
        }
        storageService.delete(file.getOriginalFilename());
        String root = storageService.store(file);
        Image image = Image.builder().name(file.getOriginalFilename()).path(root).build();
        return imageRepository.save(image);
    }

    @Override
    public List<Image> getAll() {
        return imageRepository.findAll();
    }

    @Override
    public byte[] getFullImage(String fileName) {
        validateImage(fileName);
        try {
            return FileCopyUtils.copyToByteArray(storageService.loadFullImage(fileName).getFile());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private void validateImage(String fileName){
        if(!imageRepository.findById(fileName).isPresent()){
            throw new IllegalArgumentException("File does not exist "+fileName);
        }
    }

    @Override
    public byte[] getThumbnail(String fileName) {
        validateImage(fileName);
        try {
            return FileCopyUtils.copyToByteArray(storageService.loadThumbnail(fileName).getFile());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Image delete(String fileName) {
        this.validateImage(fileName);
        Image image = imageRepository.getOne(fileName);
        imageRepository.delete(image);
        storageService.delete(fileName);
        return image;
    }
}
