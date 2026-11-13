package com.farmaciaoasis.repository;

import com.farmaciaoasis.model.Administrador;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministradorRepository extends JpaRepository<Administrador, Integer> {

    Optional<Administrador> findByCorreo(String correo);
}
