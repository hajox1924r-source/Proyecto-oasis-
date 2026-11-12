package com.farmaciaoasis.service;

import com.farmaciaoasis.model.Venta;
import com.farmaciaoasis.repository.VentaRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class VentaService {

    private final VentaRepository ventas;

    public VentaService(VentaRepository ventas) {
        this.ventas = ventas;
    }

    public List<Venta> listar() {
        return ventas.findAllByOrderByFechaDesc();
    }
}
