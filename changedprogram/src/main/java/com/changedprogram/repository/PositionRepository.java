package com.changedprogram.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.changedprogram.dto.PositionDTO;
import com.changedprogram.entity.Position;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface PositionRepository extends JpaRepository<Position, Long> {
	boolean existsByName(String name);
    Optional<Position> findByNameIgnoreCase(String name);

    
    @Query("SELECT new com.changedprogram.dto.PositionDTO(p.id, p.name) FROM Position p")
    List<PositionDTO> findAllPositionDTOs();
    
    @Query("SELECT new com.changedprogram.dto.PositionDTO(p.id, p.name) FROM Position p")
    List<PositionDTO> findAllProjectedBy(Sort sort);
}
