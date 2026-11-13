package com.farmaciaoasis.repository;

import com.farmaciaoasis.model.Venta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaRepository extends JpaRepository<Venta, Integer> {

    List<Venta> findAllByOrderByFechaDesc();
}
