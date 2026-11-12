package com.farmaciaoasis.controller;

import com.farmaciaoasis.service.LoteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/** Panel de medicamentos vencidos y por vencer. */
@Controller
@RequestMapping("/vencimientos")
public class VencimientoController {

    private final LoteService loteService;

    public VencimientoController(LoteService loteService) {
        this.loteService = loteService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("lotes", loteService.listarVencidosYPorVencer());
        return "vencimientos/lista";
    }
}
