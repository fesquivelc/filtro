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
    @NamedQuery(name = "Horario.findAll", query = "SELECT h FROM Horario h"),
    @NamedQuery(name = "Horario.findById", query = "SELECT h FROM Horario h WHERE h.id = :id"),
    @NamedQuery(name = "Horario.findByFechaRegistro", query = "SELECT h FROM Horario h WHERE h.fechaRegistro = :fechaRegistro"),
    @NamedQuery(name = "Horario.findByMes", query = "SELECT h FROM Horario h WHERE h.mes = :mes"),
    @NamedQuery(name = "Horario.findByPorFecha", query = "SELECT h FROM Horario h WHERE h.porFecha = :porFecha"),
    @NamedQuery(name = "Horario.findByLunes", query = "SELECT h FROM Horario h WHERE h.lunes = :lunes"),
    @NamedQuery(name = "Horario.findByMartes", query = "SELECT h FROM Horario h WHERE h.martes = :martes"),
    @NamedQuery(name = "Horario.findByMiercoles", query = "SELECT h FROM Horario h WHERE h.miercoles = :miercoles"),
    @NamedQuery(name = "Horario.findByJueves", query = "SELECT h FROM Horario h WHERE h.jueves = :jueves"),
    @NamedQuery(name = "Horario.findByViernes", query = "SELECT h FROM Horario h WHERE h.viernes = :viernes"),
    @NamedQuery(name = "Horario.findBySabado", query = "SELECT h FROM Horario h WHERE h.sabado = :sabado"),
    @NamedQuery(name = "Horario.findByDomingo", query = "SELECT h FROM Horario h WHERE h.domingo = :domingo")})
public class Horario implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Column(name = "fecha_registro")
    @Temporal(TemporalType.DATE)
    private Date fechaRegistro;
    private Integer mes;
    @Basic(optional = false)
    @Column(name = "por_fecha")
    private boolean porFecha;
    @Basic(optional = false)
    private boolean lunes;
    @Basic(optional = false)
    private boolean martes;
    @Basic(optional = false)
    private boolean miercoles;
    @Basic(optional = false)
    private boolean jueves;
    @Basic(optional = false)
    private boolean viernes;
    @Basic(optional = false)
    private boolean sabado;
    @Basic(optional = false)
    private boolean domingo;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "horario")
    private List<HorarioJornada> horarioJornadaList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "horario")
    private List<EmpleadoHorario> empleadoHorarioList;

    public Horario() {
    }

    public Horario(Integer id) {
        this.id = id;
    }

    public Horario(Integer id, boolean porFecha, boolean lunes, boolean martes, boolean miercoles, boolean jueves, boolean viernes, boolean sabado, boolean domingo) {
        this.id = id;
        this.porFecha = porFecha;
        this.lunes = lunes;
        this.martes = martes;
        this.miercoles = miercoles;
        this.jueves = jueves;
        this.viernes = viernes;
        this.sabado = sabado;
        this.domingo = domingo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public boolean getPorFecha() {
        return porFecha;
    }

    public void setPorFecha(boolean porFecha) {
        this.porFecha = porFecha;
    }

    public boolean getLunes() {
        return lunes;
    }

    public void setLunes(boolean lunes) {
        this.lunes = lunes;
    }

    public boolean getMartes() {
        return martes;
    }

    public void setMartes(boolean martes) {
        this.martes = martes;
    }

    public boolean getMiercoles() {
        return miercoles;
    }

    public void setMiercoles(boolean miercoles) {
        this.miercoles = miercoles;
    }

    public boolean getJueves() {
        return jueves;
    }

    public void setJueves(boolean jueves) {
        this.jueves = jueves;
    }

    public boolean getViernes() {
        return viernes;
    }

    public void setViernes(boolean viernes) {
        this.viernes = viernes;
    }

    public boolean getSabado() {
        return sabado;
    }

    public void setSabado(boolean sabado) {
        this.sabado = sabado;
    }

    public boolean getDomingo() {
        return domingo;
    }

    public void setDomingo(boolean domingo) {
        this.domingo = domingo;
    }

    @XmlTransient
    public List<HorarioJornada> getHorarioJornadaList() {
        return horarioJornadaList;
    }

    public void setHorarioJornadaList(List<HorarioJornada> horarioJornadaList) {
        this.horarioJornadaList = horarioJornadaList;
    }

    @XmlTransient
    public List<EmpleadoHorario> getEmpleadoHorarioList() {
        return empleadoHorarioList;
    }

    public void setEmpleadoHorarioList(List<EmpleadoHorario> empleadoHorarioList) {
        this.empleadoHorarioList = empleadoHorarioList;
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
        if (!(object instanceof Horario)) {
            return false;
        }
        Horario other = (Horario) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pruebas.entidades.Horario[ id=" + id + " ]";
    }
    
}
