package com.farmaciaoasis.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Usuario administrador: registra los lotes que ingresan. */
@Entity
@Table(name = "administrador")
public class Administrador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_administrador")
    private Integer idAdministrador;

    private String nombre;

    private String correo;

    @Column(name = "contrasena_hash")
    private String contrasenaHash;

    private boolean activo = true;

    public Integer getIdAdministrador() { return idAdministrador; }
    public void setIdAdministrador(Integer idAdministrador) { this.idAdministrador = idAdministrador; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContrasenaHash() { return contrasenaHash; }
    public void setContrasenaHash(String contrasenaHash) { this.contrasenaHash = contrasenaHash; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
