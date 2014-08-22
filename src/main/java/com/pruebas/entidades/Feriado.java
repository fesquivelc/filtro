/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pruebas.entidades;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author RyuujiMD
 */
@Entity
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Feriado.findAll", query = "SELECT f FROM Feriado f"),
    @NamedQuery(name = "Feriado.findById", query = "SELECT f FROM Feriado f WHERE f.id = :id"),
    @NamedQuery(name = "Feriado.findByFInicio", query = "SELECT f FROM Feriado f WHERE f.fInicio = :fInicio"),
    @NamedQuery(name = "Feriado.findByFFin", query = "SELECT f FROM Feriado f WHERE f.fFin = :fFin"),
    @NamedQuery(name = "Feriado.findByAnio", query = "SELECT f FROM Feriado f WHERE f.anio = :anio")})
public class Feriado implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "f_inicio")
    @Temporal(TemporalType.DATE)
    private Date fInicio;
    @Basic(optional = false)
    @Column(name = "f_fin")
    @Temporal(TemporalType.DATE)
    private Date fFin;
    @Basic(optional = false)
    private String anio;

    public Feriado() {
    }

    public Feriado(Integer id) {
        this.id = id;
    }

    public Feriado(Integer id, Date fInicio, Date fFin, String anio) {
        this.id = id;
        this.fInicio = fInicio;
        this.fFin = fFin;
        this.anio = anio;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFInicio() {
        return fInicio;
    }

    public void setFInicio(Date fInicio) {
        this.fInicio = fInicio;
    }

    public Date getFFin() {
        return fFin;
    }

    public void setFFin(Date fFin) {
        this.fFin = fFin;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
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
        if (!(object instanceof Feriado)) {
            return false;
        }
        Feriado other = (Feriado) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pruebas.entidades.Feriado[ id=" + id + " ]";
    }
    
}
