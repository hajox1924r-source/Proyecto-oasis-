package com.farmaciaoasis.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

/** Comprueba la regla de vencimientos: vencido, próximo a vencer (30 días o menos) y vigente. */
class LoteTest {

    private static final LocalDate HOY = LocalDate.of(2026, 11, 11);

    private static Lote loteQueVence(LocalDate fechaVencimiento) {
        Lote lote = new Lote();
        lote.setFechaVencimiento(fechaVencimiento);
        return lote;
    }

    @Test
    void elLoteQueVencioAyerEstaVencido() {
        Lote lote = loteQueVence(HOY.minusDays(1));
        assertEquals(Lote.VENCIDO, lote.estado(HOY));
        assertEquals(-1, lote.diasRestantes(HOY));
    }

    @Test
    void elLoteQueVenceHoyEstaProximoAVencer() {
        assertEquals(Lote.PROXIMO_A_VENCER, loteQueVence(HOY).estado(HOY));
    }

    @Test
    void elLoteQueVenceEn30DiasEstaProximoAVencer() {
        assertEquals(Lote.PROXIMO_A_VENCER, loteQueVence(HOY.plusDays(30)).estado(HOY));
    }

    @Test
    void elLoteQueVenceEn31DiasEstaVigente() {
        assertEquals(Lote.VIGENTE, loteQueVence(HOY.plusDays(31)).estado(HOY));
    }
}
