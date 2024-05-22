package com.changedprogram.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.changedprogram.entity.Type;

public interface TypeRepository extends JpaRepository<Type, Long> {
    Optional<Type> findByName(String name);
}