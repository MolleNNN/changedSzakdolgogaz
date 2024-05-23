package com.changedprogram.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.changedprogram.dto.ReceiverDTO;
import com.changedprogram.entity.Receiver;

public interface ReceiverRepository extends JpaRepository<Receiver, Long> {
    boolean existsByName(String name);
    Optional<Receiver> findByNameIgnoreCase(String name);

    @Query("SELECT new com.changedprogram.dto.ReceiverDTO(r.id, r.name) FROM Receiver r")
    List<ReceiverDTO> findAllReceiverDTOs();

    @Query("SELECT new com.changedprogram.dto.ReceiverDTO(r.id, r.name) FROM Receiver r ORDER BY r.name")
    List<ReceiverDTO> findAllReceiverDTOsSortedByName();
}
