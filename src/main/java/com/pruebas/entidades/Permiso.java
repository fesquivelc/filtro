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
 * @author fesquivelc
 */
@Entity
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Permiso.findAll", query = "SELECT p FROM Permiso p"),
    @NamedQuery(name = "Permiso.findById", query = "SELECT p FROM Permiso p WHERE p.id = :id"),
    @NamedQuery(name = "Permiso.findByMotivo", query = "SELECT p FROM Permiso p WHERE p.motivo = :motivo"),
    @NamedQuery(name = "Permiso.findByFecha", query = "SELECT p FROM Permiso p WHERE p.fecha = :fecha"),
    @NamedQuery(name = "Permiso.findByHEntrada", query = "SELECT p FROM Permiso p WHERE p.hEntrada = :hEntrada"),
    @NamedQuery(name = "Permiso.findByHSalida", query = "SELECT p FROM Permiso p WHERE p.hSalida = :hSalida"),
    @NamedQuery(name = "Permiso.findByPorFecha", query = "SELECT p FROM Permiso p WHERE p.porFecha = :porFecha"),
    @NamedQuery(name = "Permiso.findByFEntrada", query = "SELECT p FROM Permiso p WHERE p.fEntrada = :fEntrada"),
    @NamedQuery(name = "Permiso.findByFSalida", query = "SELECT p FROM Permiso p WHERE p.fSalida = :fSalida")})
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
    @Column(name = "h_entrada")
    @Temporal(TemporalType.TIME)
    private Date hEntrada;
    @Column(name = "h_salida")
    @Temporal(TemporalType.TIME)
    private Date hSalida;
    @Column(name = "por_fecha")
    private Boolean porFecha;
    @Column(name = "f_entrada")
    @Temporal(TemporalType.DATE)
    private Date fEntrada;
    @Column(name = "f_salida")
    @Temporal(TemporalType.DATE)
    private Date fSalida;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "permiso")
    private List<PermisoEmpleado> permisoEmpleadoList;

    public Permiso() {
    }

    public Permiso(Integer id) {
        this.id = id;
    }

    public Permiso(Integer id, String motivo, Date fecha) {
        this.id = id;
        this.motivo = motivo;
        this.fecha = fecha;
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

    public Date getHEntrada() {
        return hEntrada;
    }

    public void setHEntrada(Date hEntrada) {
        this.hEntrada = hEntrada;
    }

    public Date getHSalida() {
        return hSalida;
    }

    public void setHSalida(Date hSalida) {
        this.hSalida = hSalida;
    }

    public Boolean getPorFecha() {
        return porFecha;
    }

    public void setPorFecha(Boolean porFecha) {
        this.porFecha = porFecha;
    }

    public Date getFEntrada() {
        return fEntrada;
    }

    public void setFEntrada(Date fEntrada) {
        this.fEntrada = fEntrada;
    }

    public Date getFSalida() {
        return fSalida;
    }

    public void setFSalida(Date fSalida) {
        this.fSalida = fSalida;
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
