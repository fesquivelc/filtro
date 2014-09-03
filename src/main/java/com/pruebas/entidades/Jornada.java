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
    @NamedQuery(name = "Jornada.findAll", query = "SELECT j FROM Jornada j"),
    @NamedQuery(name = "Jornada.findById", query = "SELECT j FROM Jornada j WHERE j.id = :id"),
    @NamedQuery(name = "Jornada.findByEntrada", query = "SELECT j FROM Jornada j WHERE j.entrada = :entrada"),
    @NamedQuery(name = "Jornada.findBySalida", query = "SELECT j FROM Jornada j WHERE j.salida = :salida"),
    @NamedQuery(name = "Jornada.findByTerminaDiaSiguiente", query = "SELECT j FROM Jornada j WHERE j.terminaDiaSiguiente = :terminaDiaSiguiente"),
    @NamedQuery(name = "Jornada.findByNombre", query = "SELECT j FROM Jornada j WHERE j.nombre = :nombre")})
public class Jornada implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Basic(optional = false)
    @Temporal(TemporalType.TIME)
    private Date entrada;
    @Basic(optional = false)
    @Temporal(TemporalType.TIME)
    private Date salida;
    @Column(name = "termina_dia_siguiente")
    private Boolean terminaDiaSiguiente;
    @Basic(optional = false)
    private String nombre;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "jornada")
    private List<HorarioJornada> horarioJornadaList;

    public Jornada() {
    }

    public Jornada(Integer id) {
        this.id = id;
    }

    public Jornada(Integer id, Date entrada, Date salida, String nombre) {
        this.id = id;
        this.entrada = entrada;
        this.salida = salida;
        this.nombre = nombre;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getEntrada() {
        return entrada;
    }

    public void setEntrada(Date entrada) {
        this.entrada = entrada;
    }

    public Date getSalida() {
        return salida;
    }

    public void setSalida(Date salida) {
        this.salida = salida;
    }

    public Boolean getTerminaDiaSiguiente() {
        return terminaDiaSiguiente;
    }

    public void setTerminaDiaSiguiente(Boolean terminaDiaSiguiente) {
        this.terminaDiaSiguiente = terminaDiaSiguiente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @XmlTransient
    public List<HorarioJornada> getHorarioJornadaList() {
        return horarioJornadaList;
    }

    public void setHorarioJornadaList(List<HorarioJornada> horarioJornadaList) {
        this.horarioJornadaList = horarioJornadaList;
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
        if (!(object instanceof Jornada)) {
            return false;
        }
        Jornada other = (Jornada) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pruebas.entidades.Jornada[ id=" + id + " ]";
    }
    
}
