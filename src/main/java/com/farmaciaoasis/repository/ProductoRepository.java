package com.farmaciaoasis.repository;

import com.farmaciaoasis.model.Producto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    List<Producto> findAllByOrderByNombreAsc();

    /** Busca por nombre, código, presentación o categoría (sin distinguir mayúsculas). */
    @Query("select p from Producto p left join p.categoria c where "
            + "lower(p.nombre) like lower(concat('%', :q, '%')) "
            + "or lower(p.codigo) like lower(concat('%', :q, '%')) "
            + "or lower(coalesce(p.presentacion, '')) like lower(concat('%', :q, '%')) "
            + "or lower(coalesce(c.nombre, '')) like lower(concat('%', :q, '%')) "
            + "order by p.nombre")
    List<Producto> buscar(@Param("q") String q);
}
