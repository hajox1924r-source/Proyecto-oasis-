package com.farmaciaoasis.controller;

import com.farmaciaoasis.service.LoteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Panel de medicamentos vencidos y por vencer. */
@Controller
@RequestMapping("/vencimientos")
public class VencimientoController {

    private final LoteService loteService;

    public VencimientoController(LoteService loteService) {
        this.loteService = loteService;
    }

    /** filtro: todos | por-vencer | vencidos (por defecto, todos). */
    @GetMapping
    public String listar(@RequestParam(required = false, defaultValue = "todos") String filtro,
                         Model model) {
        model.addAttribute("lotes", loteService.listarAlertas(filtro));
        model.addAttribute("filtro", filtro);
        return "vencimientos/lista";
    }
}
