package com.farmaciaoasis.controller;

import com.farmaciaoasis.model.Producto;
import com.farmaciaoasis.service.ProductoService;
import java.math.BigDecimal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String buscar, Model model) {
        model.addAttribute("productos", productoService.listar(buscar));
        model.addAttribute("categorias", productoService.listarCategorias());
        model.addAttribute("buscar", buscar);
        return "productos/lista";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model, RedirectAttributes flash) {
        Producto producto = productoService.buscarPorId(id);
        if (producto == null) {
            flash.addFlashAttribute("mensajeError", "No se encontró el producto solicitado.");
            return "redirect:/productos";
        }
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", productoService.listarCategorias());
        return "productos/form";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam(required = false) Integer idProducto,
                          @RequestParam(required = false) String codigo,
                          @RequestParam(required = false) String nombre,
                          @RequestParam(required = false) String presentacion,
                          @RequestParam(required = false) Integer categoria,
                          @RequestParam(required = false) BigDecimal precioVenta,
                          RedirectAttributes flash) {
        // Validación: los campos clave del producto son obligatorios.
        if (codigo == null || codigo.isBlank()
                || nombre == null || nombre.isBlank()
                || categoria == null) {
            flash.addFlashAttribute("mensajeError", "Debe completar código, nombre y categoría del producto.");
            return "redirect:/productos";
        }
        if (precioVenta == null || precioVenta.signum() <= 0) {
            flash.addFlashAttribute("mensajeError", "El precio de venta debe ser mayor que cero.");
            return "redirect:/productos";
        }
        try {
            productoService.guardar(idProducto, codigo.trim(), nombre.trim(), presentacion, categoria, precioVenta);
            flash.addFlashAttribute("mensajeExito", "Producto guardado correctamente.");
        } catch (Exception e) {
            flash.addFlashAttribute("mensajeError",
                    "No se pudo guardar el producto. Verifique que el código no esté repetido.");
        }
        return "redirect:/productos";
    }
}
