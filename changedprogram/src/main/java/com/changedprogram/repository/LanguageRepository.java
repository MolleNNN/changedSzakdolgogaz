package com.changedprogram.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.changedprogram.entity.Language;


public interface LanguageRepository extends JpaRepository<Language, Long> {
    Optional<Language> findByCode(String code);

}