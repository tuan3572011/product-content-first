package com.product.content.controller;


import com.product.content.model.Configuration;
import com.product.content.model.ConfigurationWrapper;
import com.product.content.repository.ConfigurationRepository;
import com.product.content.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("admin/configuration")
public class ConfigurationResource {

    @PostConstruct
    private void init(){
        this.configurationService.save(new Configuration("location-image", "location.jpg"));
        this.configurationService.save(new Configuration("email", "ABC@gmail.com"));
        this.configurationService.save(new Configuration("address", "394 University Ave. <br>" +
                "Paolo Alto, CA 957236 "));
        this.configurationService.save(new Configuration("facebook", "ABC@facebook.com"));
        this.configurationService.save(new Configuration("phone", "090900999"));
        this.configurationService.save(new Configuration("twitter", "ABC@twitter.com"));
    }

    @Autowired
    private ConfigurationService configurationService;

    @PostMapping
    public ResponseEntity<List<Configuration>> save(@RequestBody ConfigurationWrapper wrapper){
        return new ResponseEntity<>(configurationService.save(wrapper.getConfigurations()), HttpStatus.OK);
    }


    @GetMapping("/contact-us")
    public ResponseEntity<Map<String, String>> getContactUsConfiguration(){
        return new ResponseEntity<>(configurationService.getByConfig("location-image", "address", "email", "facebook", "phone","twitter"), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, String>> getAll(){
        return new ResponseEntity<>(configurationService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/about-us")
    public ResponseEntity<Map<String, String>> getAboutUsConfiguration(){
        return new ResponseEntity<>(configurationService.getByConfig("location-image", "address", "email", "facebook", "phone","twitter"), HttpStatus.OK);
    }
}
