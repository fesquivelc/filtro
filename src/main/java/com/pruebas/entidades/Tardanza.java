/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pruebas.entidades;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
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
 * @author RyuujiMD
 */
@Entity
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tardanza.findAll", query = "SELECT t FROM Tardanza t"),
    @NamedQuery(name = "Tardanza.findById", query = "SELECT t FROM Tardanza t WHERE t.id = :id"),
    @NamedQuery(name = "Tardanza.findByFecha", query = "SELECT t FROM Tardanza t WHERE t.fecha = :fecha"),
    @NamedQuery(name = "Tardanza.findByHoraentrada", query = "SELECT t FROM Tardanza t WHERE t.horaentrada = :horaentrada"),
    @NamedQuery(name = "Tardanza.findByHoraregistro", query = "SELECT t FROM Tardanza t WHERE t.horaregistro = :horaregistro"),
    @NamedQuery(name = "Tardanza.findByMinutos", query = "SELECT t FROM Tardanza t WHERE t.minutos = :minutos")})
public class Tardanza implements Serializable {
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
    private Date horaentrada;
    @Basic(optional = false)
    @Temporal(TemporalType.TIME)
    private Date horaregistro;
    @Basic(optional = false)
    private int minutos;
    @JoinColumn(name = "empleado_id", referencedColumnName = "id")
    @ManyToOne
    private Empleado empleadoId;
    @JoinColumn(name = "turno_id", referencedColumnName = "id")
    @ManyToOne
    private HorarioJornada turnoId;

    public Tardanza() {
    }

    public Tardanza(Integer id) {
        this.id = id;
    }

    public Tardanza(Integer id, Date fecha, Date horaentrada, Date horaregistro, int minutos) {
        this.id = id;
        this.fecha = fecha;
        this.horaentrada = horaentrada;
        this.horaregistro = horaregistro;
        this.minutos = minutos;
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

    public Date getHoraentrada() {
        return horaentrada;
    }

    public void setHoraentrada(Date horaentrada) {
        this.horaentrada = horaentrada;
    }

    public Date getHoraregistro() {
        return horaregistro;
    }

    public void setHoraregistro(Date horaregistro) {
        this.horaregistro = horaregistro;
    }

    public int getMinutos() {
        return minutos;
    }

    public void setMinutos(int minutos) {
        this.minutos = minutos;
    }

    public Empleado getEmpleadoId() {
        return empleadoId;
    }

    public void setEmpleadoId(Empleado empleadoId) {
        this.empleadoId = empleadoId;
    }

    public HorarioJornada getTurnoId() {
        return turnoId;
    }

    public void setTurnoId(HorarioJornada turnoId) {
        this.turnoId = turnoId;
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
        if (!(object instanceof Tardanza)) {
            return false;
        }
        Tardanza other = (Tardanza) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pruebas.entidades.Tardanza[ id=" + id + " ]";
    }
    
}
