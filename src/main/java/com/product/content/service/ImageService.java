package com.product.content.service;

import com.product.content.model.Image;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {

    Image save(MultipartFile file);

    List<Image> getAll();

    byte[] getFullImage(String fileName);

    byte[] getThumbnail(String fileName);

    Image delete(String fileName);
}
