package com.changedprogram.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.changedprogram.entity.Ppt;

public interface PptRepository extends JpaRepository<Ppt, Long> {
	 Optional<Ppt> findFirstByOrderByIdAsc();
	/* Optional<Ppt> findByIsActiveTrue();
	 List<Ppt> findAllByOrderByIdAsc();*/
	    List<Ppt> findByIsActiveTrue();
	    List<Ppt> findAllByOrderByIdAsc();

	    @Query("SELECT p FROM Ppt p JOIN p.language l WHERE p.isActive = true AND l.code = :code")
	    List<Ppt> findActivePresentationsByLanguageCode(@Param("code") String code);


}
