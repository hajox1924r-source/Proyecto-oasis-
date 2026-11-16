package com.farmaciaoasis.repository;

import com.farmaciaoasis.model.Venta;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VentaRepository extends JpaRepository<Venta, Integer> {

    List<Venta> findAllByOrderByFechaDesc();

    /** Carga la venta con sus renglones, lotes y productos (para mostrar/imprimir la factura). */
    @Query("select distinct v from Venta v "
            + "left join fetch v.detalles d "
            + "left join fetch d.lote l "
            + "left join fetch l.producto "
            + "where v.idVenta = :id")
    Optional<Venta> buscarConDetalles(@Param("id") Integer id);
}
