package com.changedprogram.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.changedprogram.entity.Image;

public interface ImageRepository extends JpaRepository<Image, Long>{

	List<Image> findByPptId(Long pptId);

}
