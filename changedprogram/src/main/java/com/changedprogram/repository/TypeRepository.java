package com.changedprogram.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.changedprogram.entity.Type;

public interface TypeRepository extends JpaRepository<Type, Long> {
    Optional<Type> findByName(String name);
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
    List<Type> findAllByOrderByNameAsc();

}