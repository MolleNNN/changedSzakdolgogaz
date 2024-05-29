package com.changedprogram.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.changedprogram.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    // Update method signature to use Date
 /*   Optional<User> findByNameAndBirthdate(String name, Date birthdate);

    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN FETCH u.position " +
            "JOIN FETCH u.receiver " +
            "JOIN FETCH u.company " +
            "LEFT JOIN FETCH u.results r " +
            "LEFT JOIN FETCH r.ppt")
    List<User> findAllWithDetails();*/



    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN FETCH u.position p " +
            "JOIN FETCH u.receiver r " +
            "JOIN FETCH u.company c " +
            "LEFT JOIN FETCH u.results res " +
            "LEFT JOIN FETCH res.ppt ppt " +
            "ORDER BY " +
            "CASE WHEN :sortBy = 'name' THEN u.name END, " +
            "CASE WHEN :sortBy = 'birthdate' THEN u.birthdate END, " +
            "CASE WHEN :sortBy = 'position.name' THEN p.name END, " +
            "CASE WHEN :sortBy = 'company.name' THEN c.name END, " +
            "CASE WHEN :sortBy = 'receiver.name' THEN r.name END, " +
            "CASE WHEN :sortBy = 'active' THEN u.active END, " +
            "CASE WHEN :sortBy = 'results.filled' THEN res.filled END, " +
            "CASE WHEN :sortBy = 'results.ppt.filename' THEN ppt.filename END, " +
            "u.name ASC")
     List<User> findAllWithDetails(@Param("sortBy") String sortBy);
    int countByActive(boolean active);
    Optional<User> findByTaxNumber(String taxNumber);

/*
    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.position " +
            "LEFT JOIN FETCH u.receiver " +
            "LEFT JOIN FETCH u.company")
     List<User> findAllWithDetails1();
     */
    @Query("SELECT DISTINCT u FROM User u " +
    	       "LEFT JOIN FETCH u.position " +
    	       "LEFT JOIN FETCH u.receiver " +
    	       "LEFT JOIN FETCH u.company " +
    	       "LEFT JOIN FETCH u.results r " +
    	       "LEFT JOIN FETCH r.ppt " +
    	       "ORDER BY u.name ASC")
    	List<User> findAllWithDetails1();

}