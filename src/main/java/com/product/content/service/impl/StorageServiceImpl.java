package com.product.content.service.impl;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.product.content.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;

@Service
public class StorageServiceImpl implements StorageService {


  Logger log = LoggerFactory.getLogger(this.getClass().getName());
  private final Path rootLocation = Paths.get("upload-dir");
  private final Path thumbnailLocation = Paths.get("upload-dir").resolve("thumbnail");


  @Override
  public String store(MultipartFile file) {
    try {
      String fileName = file.getOriginalFilename();
      String format = fileName.substring(fileName.lastIndexOf(".") + 1);
      Files.copy(file.getInputStream(), this.rootLocation.resolve(fileName));
      this.createThumbnail(this.loadResource(this.rootLocation.resolve(fileName)).getFile(), format, 800, 600);
      return this.rootLocation.toString();
    } catch (Exception e) {
      log.error(e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    }
  }


  private File createThumbnail(File inputImgFile, String format, int thumnail_width, int thumbnail_height){
    File outputFile=null;
    try {
      BufferedImage img = new BufferedImage(thumnail_width, thumbnail_height, BufferedImage.TYPE_INT_RGB);
      img.createGraphics().drawImage(ImageIO.read(inputImgFile).getScaledInstance(thumnail_width, thumbnail_height, Image.SCALE_SMOOTH),0,0,null);
      outputFile=new File(this.thumbnailLocation.resolve(inputImgFile.getName()).toString());
      ImageIO.write(img, format, outputFile);
      return outputFile;
    } catch (IOException e) {
      System.out.println("Exception while generating thumbnail "+e.getMessage());
      return null;
    }
  }


  @Override
  public Resource loadFullImage(String fileName) {
    return loadResource(this.rootLocation.resolve(fileName));
  }

  private Resource loadResource(Path path){
    try {
      Resource resource = new UrlResource(path.toUri());
      if (resource.exists() || resource.isReadable()) {
        return resource;
      } else {
        throw new RuntimeException("FAIL!");
      }
    } catch (MalformedURLException e) {
      throw new RuntimeException("FAIL!");
    }
  }


  @Override
  public void delete(String fileName) {
      this.delete(rootLocation.resolve(fileName));
      this.delete(thumbnailLocation.resolve(fileName));
  }

  private void delete(Path path){
    if(Objects.nonNull(path)){
      FileSystemUtils.deleteRecursively(path.toFile());
    }
  }



  @Override
  public Resource loadThumbnail(String fileName) {
    return loadResource(this.thumbnailLocation.resolve(fileName));
  }

  @Override
  public List<String> discovery() {
    List<String> result = new LinkedList<>();
    try {
      Files.walk(this.rootLocation, 1).forEach(path -> {
        File file = path.toFile();
        if (file.isFile()){
          log.info("Discover image " + file.getName());
          result.add(file.getName());
        }
      });
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }

  public void init() {
    try {
      Files.createDirectory(rootLocation);
    } catch (IOException e) {
      throw new RuntimeException("Could not initialize storage!");
    }
  }
}