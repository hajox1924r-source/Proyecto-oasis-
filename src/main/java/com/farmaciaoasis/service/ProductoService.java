package com.farmaciaoasis.service;

import com.farmaciaoasis.model.Categoria;
import com.farmaciaoasis.model.Producto;
import com.farmaciaoasis.repository.CategoriaRepository;
import com.farmaciaoasis.repository.DetalleVentaRepository;
import com.farmaciaoasis.repository.LoteRepository;
import com.farmaciaoasis.repository.ProductoRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductoService {

    private final ProductoRepository productos;
    private final CategoriaRepository categorias;
    private final LoteRepository lotes;
    private final DetalleVentaRepository detalles;

    public ProductoService(ProductoRepository productos, CategoriaRepository categorias,
                           LoteRepository lotes, DetalleVentaRepository detalles) {
        this.productos = productos;
        this.categorias = categorias;
        this.lotes = lotes;
        this.detalles = detalles;
    }

    public List<Producto> listar() {
        return productos.findAllByOrderByNombreAsc();
    }

    /** Lista filtrada por el texto del buscador; sin texto, devuelve todos. */
    public List<Producto> listar(String buscar) {
        if (buscar == null || buscar.isBlank()) {
            return listar();
        }
        return productos.buscar(buscar.trim());
    }

    public List<Categoria> listarCategorias() {
        return categorias.findAll();
    }

    public Producto buscarPorId(Integer id) {
        return productos.findById(id).orElse(null);
    }

    /** Crea un producto nuevo (id nulo) o actualiza uno existente. */
    public void guardar(Integer idProducto, String codigo, String nombre, String presentacion,
                        Integer idCategoria, BigDecimal precioVenta) {
        Producto p = (idProducto != null)
                ? productos.findById(idProducto).orElse(new Producto())
                : new Producto();
        p.setCodigo(codigo);
        p.setNombre(nombre);
        p.setPresentacion(presentacion);
        p.setPrecioVenta(precioVenta);
        p.setCategoria(categorias.findById(idCategoria).orElse(null));
        productos.save(p);
    }

    /**
     * Elimina un medicamento y sus lotes. Se usa, por ejemplo, cuando se registró un
     * producto por error. Si el medicamento tiene ventas registradas NO se elimina,
     * para no dañar el historial de facturas: en ese caso el administrador puede
     * editarlo o retirar sus lotes, pero no borrarlo.
     */
    @Transactional
    public void eliminar(Integer idProducto) {
        if (detalles.existsByLote_Producto_IdProducto(idProducto)) {
            throw new IllegalStateException(
                    "No se puede eliminar: el medicamento ya tiene ventas registradas.");
        }
        // Sin ventas: se borran primero sus lotes (que no se vendieron) y luego el producto.
        lotes.deleteByProducto_IdProducto(idProducto);
        productos.deleteById(idProducto);
    }
}
