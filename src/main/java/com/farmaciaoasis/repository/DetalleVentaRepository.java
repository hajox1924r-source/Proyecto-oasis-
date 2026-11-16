package com.farmaciaoasis.repository;

import com.farmaciaoasis.model.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Integer> {

    /** ¿Existe alguna venta de un lote de este medicamento? Protege el historial de facturas. */
    boolean existsByLote_Producto_IdProducto(Integer idProducto);
}
