package com.skynet.javafx.repository;

import com.skynet.javafx.model.Devis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DevisRepository extends JpaRepository<Devis, Long> {
    
    @Query("SELECT d FROM Devis d WHERE d.numeroDevis LIKE :yearPrefix% ORDER BY d.numeroDevis DESC")
    Devis findLastDevisForYear(@Param("yearPrefix") String yearPrefix);
}
