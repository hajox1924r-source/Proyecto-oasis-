package com.farmaciaoasis.controller;

import com.farmaciaoasis.service.LoteService;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
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
@RequestMapping("/lotes")
public class LoteController {

    private final LoteService loteService;

    public LoteController(LoteService loteService) {
        this.loteService = loteService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("lotes", loteService.listar());
        model.addAttribute("productos", loteService.listarProductos());
        model.addAttribute("proveedores", loteService.listarProveedores());
        return "lotes/lista";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam(required = false) Integer producto,
                          @RequestParam(required = false) Integer proveedor,
                          @RequestParam(required = false) String numeroLote,
                          @RequestParam(defaultValue = "0") int cantidad,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaVencimiento,
                          Authentication auth,
                          RedirectAttributes flash) {
        // Todos los campos son obligatorios para dar de alta un lote.
        if (producto == null || proveedor == null
                || numeroLote == null || numeroLote.isBlank()
                || fechaVencimiento == null) {
            flash.addFlashAttribute("mensajeError", "Debe completar todos los campos del lote.");
            return "redirect:/lotes";
        }
        if (!fechaVencimiento.isAfter(LocalDate.now())) {
            flash.addFlashAttribute("mensajeError", "La fecha de vencimiento debe ser posterior a hoy.");
            return "redirect:/lotes";
        }
        if (cantidad < 1) {
            flash.addFlashAttribute("mensajeError", "La cantidad recibida debe ser al menos 1.");
            return "redirect:/lotes";
        }
        try {
            loteService.guardar(producto, proveedor, numeroLote.trim(), cantidad, fechaVencimiento, auth.getName());
            flash.addFlashAttribute("mensajeExito", "Lote registrado correctamente.");
        } catch (Exception e) {
            flash.addFlashAttribute("mensajeError",
                    "No se pudo registrar el lote. Verifique que el número de lote no esté repetido para ese producto.");
        }
        return "redirect:/lotes";
    }

    /** Retira un lote vencido del inventario (deja sus unidades en cero). Solo administrador. */
    @PostMapping("/retirar/{id}")
    public String retirar(@PathVariable Integer id, RedirectAttributes flash) {
        if (loteService.retirar(id)) {
            flash.addFlashAttribute("mensajeExito", "Lote retirado del inventario.");
        } else {
            flash.addFlashAttribute("mensajeError", "No se encontró el lote que se quería retirar.");
        }
        return "redirect:/vencimientos";
    }
}
