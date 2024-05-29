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

	    @Query("SELECT p FROM Ppt p WHERE p.id IN (SELECT q.ppt.id FROM Question q)")
	    List<Ppt> findPptsWithQuestions();
	    
	    boolean existsByFilename(String filename);


	    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END FROM Ppt p WHERE p.filename = :filename AND p.id <> :id")
	    boolean existsByFilenameAndIdNot(@Param("filename") String filename, @Param("id") Long id);

}
