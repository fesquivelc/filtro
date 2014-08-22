/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pruebas.entidades;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author RyuujiMD
 */
@Entity
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Permiso.findAll", query = "SELECT p FROM Permiso p"),
    @NamedQuery(name = "Permiso.findById", query = "SELECT p FROM Permiso p WHERE p.id = :id"),
    @NamedQuery(name = "Permiso.findByMotivo", query = "SELECT p FROM Permiso p WHERE p.motivo = :motivo"),
    @NamedQuery(name = "Permiso.findByFecha", query = "SELECT p FROM Permiso p WHERE p.fecha = :fecha")})
public class Permiso implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Basic(optional = false)
    private String motivo;
    @Basic(optional = false)
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Column(name = "por_fecha")
    private boolean porFecha;
    @Basic(optional = false)
    @Temporal(TemporalType.TIME)
    @Column(name = "h_entrada")
    private Date hEntrada;
    
    @Basic(optional = false)
    @Temporal(TemporalType.TIME)
    @Column(name = "h_salida")
    private Date hSalida;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "f_entrada")
    private Date fEntrada;
    @Temporal(TemporalType.DATE)
    @Column(name = "f_salida")
    private Date fSalida;

    public boolean isPorFecha() {
        return porFecha;
    }

    public void setPorFecha(boolean porFecha) {
        this.porFecha = porFecha;
    }

    public Date getfEntrada() {
        return fEntrada;
    }

    public void setfEntrada(Date fEntrada) {
        this.fEntrada = fEntrada;
    }

    public Date getfSalida() {
        return fSalida;
    }

    public void setfSalida(Date fSalida) {
        this.fSalida = fSalida;
    }
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "permiso")
    private List<PermisoEmpleado> permisoEmpleadoList;

    public Permiso() {
    }

    public Permiso(Integer id) {
        this.id = id;
    }

    public Permiso(Integer id, String motivo, Date fecha, Date entrada, Date salida) {
        this.id = id;
        this.motivo = motivo;
        this.fecha = fecha;
        this.hEntrada = entrada;
        this.hSalida = salida;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Date gethEntrada() {
        return hEntrada;
    }

    public void sethEntrada(Date hEntrada) {
        this.hEntrada = hEntrada;
    }

    public Date gethSalida() {
        return hSalida;
    }

    public void sethSalida(Date hSalida) {
        this.hSalida = hSalida;
    }

    @XmlTransient
    public List<PermisoEmpleado> getPermisoEmpleadoList() {
        return permisoEmpleadoList;
    }

    public void setPermisoEmpleadoList(List<PermisoEmpleado> permisoEmpleadoList) {
        this.permisoEmpleadoList = permisoEmpleadoList;
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
        if (!(object instanceof Permiso)) {
            return false;
        }
        Permiso other = (Permiso) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pruebas.entidades.Permiso[ id=" + id + " ]";
    }
    
}
