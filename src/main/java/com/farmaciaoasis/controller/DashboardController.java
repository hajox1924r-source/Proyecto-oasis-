package com.farmaciaoasis.controller;

import com.farmaciaoasis.model.Lote;
import com.farmaciaoasis.service.LoteService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Página de inicio: resumen de las alertas de vencimiento. */
@Controller
public class DashboardController {

    private final LoteService loteService;

    public DashboardController(LoteService loteService) {
        this.loteService = loteService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        List<Lote> alertas = loteService.listarVencidosYPorVencer();
        long vencidos = alertas.stream()
                .filter(lote -> Lote.VENCIDO.equals(lote.getEstado()))
                .count();
        model.addAttribute("vencidos", vencidos);
        model.addAttribute("proximos", alertas.size() - vencidos);
        return "dashboard";
    }
}
