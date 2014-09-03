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
    @NamedQuery(name = "Empleado.findAll", query = "SELECT e FROM Empleado e"),
    @NamedQuery(name = "Empleado.findById", query = "SELECT e FROM Empleado e WHERE e.id = :id"),
    @NamedQuery(name = "Empleado.findByNombre", query = "SELECT e FROM Empleado e WHERE e.nombre = :nombre"),
    @NamedQuery(name = "Empleado.findByDni", query = "SELECT e FROM Empleado e WHERE e.dni = :dni"),
    @NamedQuery(name = "Empleado.findByFechaNacimiento", query = "SELECT e FROM Empleado e WHERE e.fechaNacimiento = :fechaNacimiento")})
public class Empleado implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Basic(optional = false)
    private String nombre;
    @Basic(optional = false)
    private String dni;
    @Basic(optional = false)
    @Column(name = "fecha_nacimiento")
    @Temporal(TemporalType.DATE)
    private Date fechaNacimiento;
    @OneToMany(mappedBy = "empleadoId")
    private List<Tardanza> tardanzaList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "empleado")
    private List<PermisoEmpleado> permisoEmpleadoList;
    @OneToMany(mappedBy = "empleadoId")
    private List<Falta> faltaList;
    @OneToMany(mappedBy = "empleado2Id")
    private List<CambioTurno> cambioTurnoList;
    @OneToMany(mappedBy = "empleado1Id")
    private List<CambioTurno> cambioTurnoList1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "empleado")
    private List<EmpleadoHorario> empleadoHorarioList;

    public Empleado() {
    }

    public Empleado(Integer id) {
        this.id = id;
    }

    public Empleado(Integer id, String nombre, String dni, Date fechaNacimiento) {
        this.id = id;
        this.nombre = nombre;
        this.dni = dni;
        this.fechaNacimiento = fechaNacimiento;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    @XmlTransient
    public List<Tardanza> getTardanzaList() {
        return tardanzaList;
    }

    public void setTardanzaList(List<Tardanza> tardanzaList) {
        this.tardanzaList = tardanzaList;
    }

    @XmlTransient
    public List<PermisoEmpleado> getPermisoEmpleadoList() {
        return permisoEmpleadoList;
    }

    public void setPermisoEmpleadoList(List<PermisoEmpleado> permisoEmpleadoList) {
        this.permisoEmpleadoList = permisoEmpleadoList;
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
        if (!(object instanceof Empleado)) {
            return false;
        }
        Empleado other = (Empleado) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pruebas.entidades.Empleado[ id=" + id + " ]";
    }
    
}
