package com.farmaciaoasis.service;

import com.farmaciaoasis.model.Categoria;
import com.farmaciaoasis.model.Producto;
import com.farmaciaoasis.repository.CategoriaRepository;
import com.farmaciaoasis.repository.ProductoRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductoService {

    private final ProductoRepository productos;
    private final CategoriaRepository categorias;

    public ProductoService(ProductoRepository productos, CategoriaRepository categorias) {
        this.productos = productos;
        this.categorias = categorias;
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
}
