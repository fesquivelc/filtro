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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author RyuujiMD
 */
@Entity
@Table(name = "horario_jornada")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "HorarioJornada.findAll", query = "SELECT h FROM HorarioJornada h"),
    @NamedQuery(name = "HorarioJornada.findById", query = "SELECT h FROM HorarioJornada h WHERE h.id = :id"),
    @NamedQuery(name = "HorarioJornada.findByFecha", query = "SELECT h FROM HorarioJornada h WHERE h.fecha = :fecha")})
public class HorarioJornada implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @JoinColumn(name = "horario", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Horario horario;
    @JoinColumn(name = "jornada", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Jornada jornada;
    @OneToMany(mappedBy = "turno")
    private List<Registro> registroList;
    @OneToMany(mappedBy = "turnoId")
    private List<Tardanza> tardanzaList;
    @OneToMany(mappedBy = "turnoId")
    private List<Falta> faltaList;
    @OneToMany(mappedBy = "horarioJornada1Id")
    private List<CambioTurno> cambioTurnoList;
    @OneToMany(mappedBy = "horarioJornada2Id")
    private List<CambioTurno> cambioTurnoList1;

    public HorarioJornada() {
    }

    public HorarioJornada(Integer id) {
        this.id = id;
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

    public Horario getHorario() {
        return horario;
    }

    public void setHorario(Horario horario) {
        this.horario = horario;
    }

    public Jornada getJornada() {
        return jornada;
    }

    public void setJornada(Jornada jornada) {
        this.jornada = jornada;
    }

    @XmlTransient
    public List<Registro> getRegistroList() {
        return registroList;
    }

    public void setRegistroList(List<Registro> registroList) {
        this.registroList = registroList;
    }

    @XmlTransient
    public List<Tardanza> getTardanzaList() {
        return tardanzaList;
    }

    public void setTardanzaList(List<Tardanza> tardanzaList) {
        this.tardanzaList = tardanzaList;
    }

    @XmlTransient
    public List<Falta> getFaltaList() {
        return faltaList;
    }

    public void setFaltaList(List<Falta> faltaList) {
        this.faltaList = faltaList;
    }

    @XmlTransient
    public List<CambioTurno> getCambioTurnoList() {
        return cambioTurnoList;
    }

    public void setCambioTurnoList(List<CambioTurno> cambioTurnoList) {
        this.cambioTurnoList = cambioTurnoList;
    }

    @XmlTransient
    public List<CambioTurno> getCambioTurnoList1() {
        return cambioTurnoList1;
    }

    public void setCambioTurnoList1(List<CambioTurno> cambioTurnoList1) {
        this.cambioTurnoList1 = cambioTurnoList1;
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
        if (!(object instanceof HorarioJornada)) {
            return false;
        }
        HorarioJornada other = (HorarioJornada) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pruebas.entidades.HorarioJornada[ id=" + id + " ]";
    }
    
}
