package com.product.content.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "configuration")
@Entity
public class Configuration {
    @Id
    private String key;
    private String value;
    @Transient
    private String label;

    public Configuration(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
