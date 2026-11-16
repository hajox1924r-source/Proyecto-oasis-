package com.farmaciaoasis.service;

import com.farmaciaoasis.model.Cajero;
import com.farmaciaoasis.model.DetalleVenta;
import com.farmaciaoasis.model.Lote;
import com.farmaciaoasis.model.Venta;
import com.farmaciaoasis.repository.CajeroRepository;
import com.farmaciaoasis.repository.LoteRepository;
import com.farmaciaoasis.repository.VentaRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VentaService {

    private final VentaRepository ventas;
    private final LoteRepository lotes;
    private final CajeroRepository cajeros;

    public VentaService(VentaRepository ventas, LoteRepository lotes, CajeroRepository cajeros) {
        this.ventas = ventas;
        this.lotes = lotes;
        this.cajeros = cajeros;
    }

    public List<Venta> listar() {
        return ventas.findAllByOrderByFechaDesc();
    }

    /** Lotes con stock disponible y que no están vencidos. */
    public List<Lote> lotesDisponibles() {
        return lotes.findAllByOrderByFechaVencimientoAsc().stream()
                .filter(l -> l.getCantidad() > 0 && !Lote.VENCIDO.equals(l.getEstado()))
                .toList();
    }

    public Lote buscarLote(Integer idLote) {
        return lotes.findById(idLote).orElse(null);
    }

    /** Crea un renglón de venta (todavía sin guardar) para el carrito en sesión. */
    public DetalleVenta nuevoItem(Lote lote, int cantidad) {
        BigDecimal precio = (lote.getProducto() != null && lote.getProducto().getPrecioVenta() != null)
                ? lote.getProducto().getPrecioVenta() : BigDecimal.ZERO;
        DetalleVenta item = new DetalleVenta();
        item.setLote(lote);
        item.setCantidad(cantidad);
        item.setPrecioUnitario(precio);
        item.setSubtotal(precio.multiply(BigDecimal.valueOf(cantidad)));
        return item;
    }

    /** Registra la venta: descuenta el stock de cada lote y guarda la venta con sus detalles. */
    @Transactional
    public void registrar(List<DetalleVenta> carrito, String correoCajero) {
        // ponytail: si quien vende es un administrador (no hay cajero con ese correo), usa el primer cajero
        Cajero cajero = cajeros.findByCorreo(correoCajero)
                .orElseGet(() -> cajeros.findAll().stream().findFirst().orElse(null));

        Venta venta = new Venta();
        venta.setCajero(cajero);
        venta.setFecha(LocalDateTime.now());

        BigDecimal total = BigDecimal.ZERO;
        for (DetalleVenta item : carrito) {
            Lote lote = lotes.findById(item.getLote().getIdLote()).orElseThrow();
            if (item.getCantidad() > lote.getCantidad()) {
                throw new IllegalStateException("Stock insuficiente en el lote " + lote.getNumeroLote());
            }
            lote.setCantidad(lote.getCantidad() - item.getCantidad());
            lotes.save(lote);

            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setLote(lote);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecioUnitario());
            detalle.setSubtotal(item.getSubtotal());
            venta.getDetalles().add(detalle);
            total = total.add(item.getSubtotal());
        }
        venta.setTotal(total);
        ventas.save(venta);
    }
}
