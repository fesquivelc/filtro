/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pruebas.entidades;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author fesquivelc
 */
@Entity
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Registro.findAll", query = "SELECT r FROM Registro r"),
    @NamedQuery(name = "Registro.findById", query = "SELECT r FROM Registro r WHERE r.id = :id"),
    @NamedQuery(name = "Registro.findByFecha", query = "SELECT r FROM Registro r WHERE r.fecha = :fecha"),
    @NamedQuery(name = "Registro.findByHora", query = "SELECT r FROM Registro r WHERE r.hora = :hora"),
    @NamedQuery(name = "Registro.findByBiometricoId", query = "SELECT r FROM Registro r WHERE r.biometricoId = :biometricoId"),
    @NamedQuery(name = "Registro.findByEmpleadoId", query = "SELECT r FROM Registro r WHERE r.empleadoId = :empleadoId"),
    @NamedQuery(name = "Registro.findByPermiso", query = "SELECT r FROM Registro r WHERE r.permiso = :permiso"),
    @NamedQuery(name = "Registro.findByEOS", query = "SELECT r FROM Registro r WHERE r.eOS = :eOS"),
    @NamedQuery(name = "Registro.findByTipo", query = "SELECT r FROM Registro r WHERE r.tipo = :tipo"),
    @NamedQuery(name = "Registro.findByMinutosTardanza", query = "SELECT r FROM Registro r WHERE r.minutosTardanza = :minutosTardanza")})
public class Registro implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Basic(optional = false)
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Basic(optional = false)
    @Temporal(TemporalType.TIME)
    private Date hora;
    @Basic(optional = false)
    @Column(name = "biometrico_id")
    private String biometricoId;
    @Basic(optional = false)
    @Column(name = "empleado_id")
    private int empleadoId;
    @Basic(optional = false)
    private boolean permiso;
    @Column(name = "e_o_s")
    private Boolean eOS;
    private String tipo;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "minutos_tardanza")
    private BigDecimal minutosTardanza;
    @JoinColumn(name = "turno", referencedColumnName = "id")
    @ManyToOne
    private HorarioJornada turno;
    @JoinColumn(name = "cambio_turno_id", referencedColumnName = "id")
    @ManyToOne
    private CambioTurno cambioTurnoId;

    public Registro() {
    }

    public Registro(Integer id) {
        this.id = id;
    }

    public Registro(Integer id, Date fecha, Date hora, String biometricoId, int empleadoId, boolean permiso) {
        this.id = id;
        this.fecha = fecha;
        this.hora = hora;
        this.biometricoId = biometricoId;
        this.empleadoId = empleadoId;
        this.permiso = permiso;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Date getHora() {
        return hora;
    }

    public void setHora(Date hora) {
        this.hora = hora;
    }

    public String getBiometricoId() {
        return biometricoId;
    }

    public void setBiometricoId(String biometricoId) {
        this.biometricoId = biometricoId;
    }

    public int getEmpleadoId() {
        return empleadoId;
    }

    public void setEmpleadoId(int empleadoId) {
        this.empleadoId = empleadoId;
    }

    public boolean getPermiso() {
        return permiso;
    }

    public void setPermiso(boolean permiso) {
        this.permiso = permiso;
    }

    public Boolean getEOS() {
        return eOS;
    }

    public void setEOS(Boolean eOS) {
        this.eOS = eOS;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getMinutosTardanza() {
        return minutosTardanza;
    }

    public void setMinutosTardanza(BigDecimal minutosTardanza) {
        this.minutosTardanza = minutosTardanza;
    }

    public HorarioJornada getTurno() {
        return turno;
    }

    public void setTurno(HorarioJornada turno) {
        this.turno = turno;
    }

    public CambioTurno getCambioTurnoId() {
        return cambioTurnoId;
    }

    public void setCambioTurnoId(CambioTurno cambioTurnoId) {
        this.cambioTurnoId = cambioTurnoId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Registro)) {
            return false;
        }
        Registro other = (Registro) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pruebas.entidades.Registro[ id=" + id + " ]";
    }
    
}
