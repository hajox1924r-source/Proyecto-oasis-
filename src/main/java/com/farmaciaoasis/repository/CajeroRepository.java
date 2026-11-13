package com.farmaciaoasis.repository;

import com.farmaciaoasis.model.Cajero;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CajeroRepository extends JpaRepository<Cajero, Integer> {

    Optional<Cajero> findByCorreo(String correo);
}
