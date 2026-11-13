package com.farmaciaoasis.service;

import com.farmaciaoasis.model.Producto;
import com.farmaciaoasis.repository.ProductoRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductoService {

    private final ProductoRepository productos;

    public ProductoService(ProductoRepository productos) {
        this.productos = productos;
    }

    public List<Producto> listar() {
        return productos.findAllByOrderByNombreAsc();
    }
}
