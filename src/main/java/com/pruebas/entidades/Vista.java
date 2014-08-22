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
    @NamedQuery(name = "Vista.findAll", query = "SELECT v FROM Vista v"),
    @NamedQuery(name = "Vista.findById", query = "SELECT v FROM Vista v WHERE v.id = :id"),
    @NamedQuery(name = "Vista.findByFecha", query = "SELECT v FROM Vista v WHERE v.fecha = :fecha"),
    @NamedQuery(name = "Vista.findByHora", query = "SELECT v FROM Vista v WHERE v.hora = :hora"),
    @NamedQuery(name = "Vista.findByEquipoIp", query = "SELECT v FROM Vista v WHERE v.equipoIp = :equipoIp"),
    @NamedQuery(name = "Vista.findByDni", query = "SELECT v FROM Vista v WHERE v.dni = :dni")})
public class Vista implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Basic(optional = false)
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Temporal(TemporalType.TIME)
    private Date hora;
    @Basic(optional = false)
    @Column(name = "equipo_ip")
    private String equipoIp;
    @Basic(optional = false)
    private int dni;

    public Vista() {
    }

    public Vista(Integer id) {
        this.id = id;
    }

    public Vista(Integer id, Date fecha, String equipoIp, int dni) {
        this.id = id;
        this.fecha = fecha;
        this.equipoIp = equipoIp;
        this.dni = dni;
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

    public String getEquipoIp() {
        return equipoIp;
    }

    public void setEquipoIp(String equipoIp) {
        this.equipoIp = equipoIp;
    }

    public int getDni() {
        return dni;
    }

    public void setDni(int dni) {
        this.dni = dni;
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
        if (!(object instanceof Vista)) {
            return false;
        }
        Vista other = (Vista) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pruebas.entidades.Vista[ id=" + id + " ]";
    }
    
}
