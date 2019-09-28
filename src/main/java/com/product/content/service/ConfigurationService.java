package com.product.content.service;

import com.product.content.model.Configuration;

import java.util.List;
import java.util.Map;

public interface ConfigurationService {

    Map<String, String> getByConfig(String ...keys);

    List<Configuration> save(List<Configuration> configs);

    void save(Configuration configuration);

    Map<String, String> getAll();
}
