package com.farmaciaoasis.repository;

import com.farmaciaoasis.model.Lote;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoteRepository extends JpaRepository<Lote, Integer> {

    List<Lote> findAllByOrderByFechaVencimientoAsc();

    /** Lotes con unidades disponibles que vencen en la fecha límite o antes (incluye los ya vencidos). */
    @Query("select l from Lote l where l.cantidad > 0 and l.fechaVencimiento <= :limite order by l.fechaVencimiento")
    List<Lote> buscarQueVencenHasta(@Param("limite") LocalDate limite);

    /** Indica si el medicamento tiene algún lote registrado (aunque esté en cero). */
    boolean existsByProducto_IdProducto(Integer idProducto);

    /** Borra todos los lotes de un medicamento (se usa al eliminar un producto sin ventas). */
    long deleteByProducto_IdProducto(Integer idProducto);
}
