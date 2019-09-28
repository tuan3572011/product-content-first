package com.product.content.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StorageService {

    String store(MultipartFile file);

    Resource loadFullImage(String filename);

    void delete(String fileName);

    Resource loadThumbnail(String fileName);

    List<String> discovery();
}

