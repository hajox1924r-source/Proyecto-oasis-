package com.farmaciaoasis.controller;

import com.farmaciaoasis.service.LoteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
        return "lotes/lista";
    }
}
