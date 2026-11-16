package com.farmaciaoasis.controller;

import com.farmaciaoasis.model.DetalleVenta;
import com.farmaciaoasis.model.Lote;
import com.farmaciaoasis.model.Venta;
import com.farmaciaoasis.service.VentaService;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    private static final String CARRITO = "carrito";
    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    /** Carrito de la venta en curso, guardado en la sesión del usuario. */
    @SuppressWarnings("unchecked")
    private List<DetalleVenta> carrito(HttpSession session) {
        List<DetalleVenta> carrito = (List<DetalleVenta>) session.getAttribute(CARRITO);
        if (carrito == null) {
            carrito = new ArrayList<>();
            session.setAttribute(CARRITO, carrito);
        }
        return carrito;
    }

    /** Unidades del lote que ya están en el carrito. */
    private int yaEnCarrito(List<DetalleVenta> carrito, Integer loteId) {
        return carrito.stream()
                .filter(i -> i.getLote() != null && loteId.equals(i.getLote().getIdLote()))
                .mapToInt(DetalleVenta::getCantidad)
                .sum();
    }

    @GetMapping
    public String listar(Model model, HttpSession session) {
        List<DetalleVenta> carrito = carrito(session);
        BigDecimal total = carrito.stream()
                .map(DetalleVenta::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("ventas", ventaService.listar());
        model.addAttribute("lotes", ventaService.lotesDisponibles());
        model.addAttribute("carrito", carrito);
        model.addAttribute("totalVenta", total);
        return "ventas/lista";
    }

    @PostMapping("/agregar")
    public String agregar(@RequestParam Integer loteId,
                          @RequestParam int cantidad,
                          HttpSession session,
                          RedirectAttributes flash) {
        List<DetalleVenta> carrito = carrito(session);
        Lote lote = ventaService.buscarLote(loteId);
        if (lote == null) {
            flash.addFlashAttribute("mensajeError", "El lote seleccionado no existe.");
        } else if (Lote.VENCIDO.equals(lote.getEstado())) {
            flash.addFlashAttribute("mensajeError", "No se puede vender un lote vencido.");
        } else if (cantidad < 1) {
            flash.addFlashAttribute("mensajeError", "La cantidad debe ser al menos 1.");
        } else if (cantidad + yaEnCarrito(carrito, loteId) > lote.getCantidad()) {
            // Se comprueba contra el stock total del lote, sumando lo que ya está en la venta.
            flash.addFlashAttribute("mensajeError",
                    "No hay suficiente stock. Disponible: " + lote.getCantidad()
                    + ", ya en la venta: " + yaEnCarrito(carrito, loteId) + ".");
        } else {
            carrito.add(ventaService.nuevoItem(lote, cantidad));
            flash.addFlashAttribute("mensajeExito", "Medicamento agregado a la venta.");
        }
        return "redirect:/ventas";
    }

    @PostMapping("/confirmar")
    public String confirmar(HttpSession session, Authentication auth, RedirectAttributes flash) {
        List<DetalleVenta> carrito = carrito(session);
        if (carrito.isEmpty()) {
            flash.addFlashAttribute("mensajeError", "No hay medicamentos en la venta.");
            return "redirect:/ventas";
        }
        try {
            Venta venta = ventaService.registrar(carrito, auth.getName());
            session.removeAttribute(CARRITO);
            flash.addFlashAttribute("mensajeExito", "Venta registrada correctamente.");
            // Al registrar la venta se muestra la factura generada.
            return "redirect:/ventas/factura/" + venta.getIdVenta();
        } catch (Exception e) {
            flash.addFlashAttribute("mensajeError", "No se pudo registrar la venta. Revise el stock de los lotes.");
            return "redirect:/ventas";
        }
    }

    /** Muestra (e imprime) la factura de una venta ya registrada. */
    @GetMapping("/factura/{id}")
    public String factura(@PathVariable Integer id, Model model, RedirectAttributes flash) {
        Venta venta = ventaService.buscarFactura(id);
        if (venta == null) {
            flash.addFlashAttribute("mensajeError", "No se encontró la factura solicitada.");
            return "redirect:/ventas";
        }
        model.addAttribute("venta", venta);
        return "ventas/factura";
    }

    @PostMapping("/limpiar")
    public String limpiar(HttpSession session, RedirectAttributes flash) {
        session.removeAttribute(CARRITO);
        flash.addFlashAttribute("mensajeExito", "Venta actual cancelada.");
        return "redirect:/ventas";
    }
}
