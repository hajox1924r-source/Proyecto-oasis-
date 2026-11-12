package com.farmaciaoasis.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Lote de un medicamento. Aquí va la fecha de vencimiento: un mismo medicamento
 * puede tener varios lotes que vencen en fechas distintas.
 */
@Entity
@Table(name = "lote")
public class Lote {

    /** Un lote está "próximo a vencer" cuando le quedan estos días o menos. */
    public static final int DIAS_ALERTA = 30;

    // Mismos estados que la vista vista_vencimientos de la base de datos
    public static final String VENCIDO = "VENCIDO";
    public static final String PROXIMO_A_VENCER = "PROXIMO A VENCER";
    public static final String VIGENTE = "VIGENTE";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lote")
    private Integer idLote;

    @Column(name = "numero_lote")
    private String numeroLote;

    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "id_proveedor")
    private Proveedor proveedor;

    /** Administrador que registró el ingreso del lote. */
    @ManyToOne
    @JoinColumn(name = "id_administrador")
    private Administrador administrador;

    @Column(name = "fecha_ingreso")
    private LocalDate fechaIngreso;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    /** Unidades disponibles del lote. */
    private int cantidad;

    @Column(name = "precio_compra")
    private BigDecimal precioCompra;

    // ----- Control de vencimientos (no se guarda en la base de datos, se calcula) -----

    /** Días que faltan para el vencimiento; negativo si ya venció. */
    public long diasRestantes(LocalDate hoy) {
        return ChronoUnit.DAYS.between(hoy, fechaVencimiento);
    }

    public String estado(LocalDate hoy) {
        long dias = diasRestantes(hoy);
        if (dias < 0) {
            return VENCIDO;
        }
        if (dias <= DIAS_ALERTA) {
            return PROXIMO_A_VENCER;
        }
        return VIGENTE;
    }

    /** Para las vistas: ${lote.diasRestantes} */
    public long getDiasRestantes() {
        return diasRestantes(LocalDate.now());
    }

    /** Para las vistas: ${lote.estado} */
    public String getEstado() {
        return estado(LocalDate.now());
    }

    // ----- Getters y setters -----

    public Integer getIdLote() { return idLote; }
    public void setIdLote(Integer idLote) { this.idLote = idLote; }

    public String getNumeroLote() { return numeroLote; }
    public void setNumeroLote(String numeroLote) { this.numeroLote = numeroLote; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Proveedor getProveedor() { return proveedor; }
    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }

    public Administrador getAdministrador() { return administrador; }
    public void setAdministrador(Administrador administrador) { this.administrador = administrador; }

    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(BigDecimal precioCompra) { this.precioCompra = precioCompra; }
}
