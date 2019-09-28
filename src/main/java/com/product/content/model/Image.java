package com.product.content.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "image")
@Entity
public class Image {
    private String path;
    @Id
    private String name;

    public Image(String name) {
        this.name = name;
    }
}
