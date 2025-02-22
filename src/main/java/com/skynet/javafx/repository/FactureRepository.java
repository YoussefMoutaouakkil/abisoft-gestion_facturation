package com.skynet.javafx.repository;

import com.skynet.javafx.model.Facture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Long> {
    @Query("SELECT f FROM Facture f WHERE f.numeroFacture LIKE :yearPrefix% ORDER BY f.numeroFacture DESC")
    Facture findLastFactureForYear(@Param("yearPrefix") String yearPrefix);
}
