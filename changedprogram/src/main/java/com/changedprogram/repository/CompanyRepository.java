package com.changedprogram.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.changedprogram.dto.CompanyDTO;
import com.changedprogram.entity.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    boolean existsByName(String name);
    Optional<Company> findByNameIgnoreCase(String name);

    @Query("SELECT new com.changedprogram.dto.CompanyDTO(c.id, c.name) FROM Company c")
    List<CompanyDTO> findAllCompanyDTOs();
    
    @Query("SELECT new com.changedprogram.dto.CompanyDTO(c.id, c.name) FROM Company c")
    List<CompanyDTO> findAllProjectedBy(Sort sort);
}
