package com.farmaciaoasis.service;

import com.farmaciaoasis.model.Lote;
import com.farmaciaoasis.model.Producto;
import com.farmaciaoasis.model.Proveedor;
import com.farmaciaoasis.repository.AdministradorRepository;
import com.farmaciaoasis.repository.LoteRepository;
import com.farmaciaoasis.repository.ProductoRepository;
import com.farmaciaoasis.repository.ProveedorRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

/** Lotes y control de vencimientos. */
@Service
public class LoteService {

    private final LoteRepository lotes;
    private final ProductoRepository productos;
    private final ProveedorRepository proveedores;
    private final AdministradorRepository administradores;

    public LoteService(LoteRepository lotes, ProductoRepository productos,
                       ProveedorRepository proveedores, AdministradorRepository administradores) {
        this.lotes = lotes;
        this.productos = productos;
        this.proveedores = proveedores;
        this.administradores = administradores;
    }

    public List<Lote> listar() {
        return lotes.findAllByOrderByFechaVencimientoAsc();
    }

    public List<Producto> listarProductos() {
        return productos.findAllByOrderByNombreAsc();
    }

    public List<Proveedor> listarProveedores() {
        return proveedores.findAll();
    }

    /** Lotes con unidades disponibles que ya vencieron o vencen en los próximos Lote.DIAS_ALERTA días. */
    public List<Lote> listarVencidosYPorVencer() {
        return lotes.buscarQueVencenHasta(LocalDate.now().plusDays(Lote.DIAS_ALERTA));
    }

    /**
     * Lista de alertas filtrada para la vista de vencimientos.
     * filtro: "vencidos" (días restantes <= 0), "por-vencer" (> 0) o "todos".
     * Se usa el mismo corte de días que muestra la insignia de la tabla.
     */
    public List<Lote> listarAlertas(String filtro) {
        List<Lote> base = listarVencidosYPorVencer();
        if ("vencidos".equals(filtro)) {
            return base.stream().filter(l -> l.getDiasRestantes() <= 0).toList();
        }
        if ("por-vencer".equals(filtro)) {
            return base.stream().filter(l -> l.getDiasRestantes() > 0).toList();
        }
        return base;
    }

    /** Retira un lote del inventario dejando sus unidades en cero (para medicamentos vencidos). */
    public boolean retirar(Integer idLote) {
        return lotes.findById(idLote).map(lote -> {
            lote.setCantidad(0);
            lotes.save(lote);
            return true;
        }).orElse(false);
    }

    /** Registra un lote nuevo. La fecha de ingreso es hoy y debe vencer después de hoy. */
    public void guardar(Integer idProducto, Integer idProveedor, String numeroLote,
                        int cantidad, LocalDate fechaVencimiento, String correoAdmin) {
        Lote lote = new Lote();
        lote.setNumeroLote(numeroLote);
        lote.setCantidad(cantidad);
        lote.setFechaIngreso(LocalDate.now());
        lote.setFechaVencimiento(fechaVencimiento);
        // ponytail: el formulario no pide precio de compra; se deja en 0, se agrega si el negocio lo necesita
        lote.setPrecioCompra(BigDecimal.ZERO);
        lote.setProducto(productos.findById(idProducto).orElse(null));
        lote.setProveedor(proveedores.findById(idProveedor).orElse(null));
        lote.setAdministrador(administradores.findByCorreo(correoAdmin)
                .orElseGet(() -> administradores.findAll().stream().findFirst().orElse(null)));
        lotes.save(lote);
    }
}
