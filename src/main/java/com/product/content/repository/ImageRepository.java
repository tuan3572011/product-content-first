package com.product.content.repository;

import com.product.content.model.Image;
import com.product.content.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface ImageRepository extends JpaRepository<Image, String> {

}
