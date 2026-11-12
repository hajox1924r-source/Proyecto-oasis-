package com.farmaciaoasis.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Usuario cajero: registra las ventas. */
@Entity
@Table(name = "cajero")
public class Cajero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cajero")
    private Integer idCajero;

    private String nombre;

    private String correo;

    @Column(name = "contrasena_hash")
    private String contrasenaHash;

    private boolean activo = true;

    public Integer getIdCajero() { return idCajero; }
    public void setIdCajero(Integer idCajero) { this.idCajero = idCajero; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContrasenaHash() { return contrasenaHash; }
    public void setContrasenaHash(String contrasenaHash) { this.contrasenaHash = contrasenaHash; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
