package com.product.content.service.impl;

import com.product.content.model.Configuration;
import com.product.content.repository.ConfigurationRepository;
import com.product.content.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ConfigurationServiceImpl implements ConfigurationService{
    @Autowired
    private ConfigurationRepository configurationRepository;
    @Override
    public Map<String, String> getByConfig(String... keys) {
        return configurationRepository.findAllById(Arrays.asList(keys)).stream()
                .collect(Collectors.toMap(Configuration::getKey, Configuration::getValue));
    }

    @Override
    public List<Configuration> save(List<Configuration> configs) {
        return this.configurationRepository.saveAll(configs);
    }

    @Override
    public void save(Configuration configuration) {
        this.configurationRepository.save(configuration);
    }

    @Override
    public Map<String, String> getAll() {
        return this.configurationRepository.findAll().stream()
                .collect(Collectors.toMap(Configuration::getKey, Configuration::getValue));
    }
}
