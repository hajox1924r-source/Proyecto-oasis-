package com.farmaciaoasis.service;

import com.farmaciaoasis.model.Lote;
import com.farmaciaoasis.repository.LoteRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

/** Lotes y control de vencimientos. */
@Service
public class LoteService {

    private final LoteRepository lotes;

    public LoteService(LoteRepository lotes) {
        this.lotes = lotes;
    }

    public List<Lote> listar() {
        return lotes.findAllByOrderByFechaVencimientoAsc();
    }

    /** Lotes con unidades disponibles que ya vencieron o vencen en los próximos Lote.DIAS_ALERTA días. */
    public List<Lote> listarVencidosYPorVencer() {
        return lotes.buscarQueVencenHasta(LocalDate.now().plusDays(Lote.DIAS_ALERTA));
    }
}
