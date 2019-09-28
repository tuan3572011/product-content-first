package com.product.content.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ConfigurationWrapper {
    List<Configuration> configurations;
}
