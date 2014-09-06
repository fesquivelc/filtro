/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pruebas.algoritmo;

import com.pruebas.dao.DAO;
import com.pruebas.entidades.CambioTurno;
import com.pruebas.entidades.Empleado;
import com.pruebas.entidades.EmpleadoHorario;
import com.pruebas.entidades.Falta;
import com.pruebas.entidades.Feriado;
import com.pruebas.entidades.HorarioJornada;
import com.pruebas.entidades.Permiso;
import com.pruebas.entidades.PermisoEmpleado;
import com.pruebas.entidades.Registro;
import com.pruebas.entidades.TCImportacion;
import com.pruebas.entidades.Tardanza;
import com.pruebas.entidades.Vista;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * @author fesquivelc
 */
public class AnalisisAsistencia {

    private Time horaInicio;
    private Date fechaInicio;
    private Time horaFin;
    private Date fechaFin;
    private final int MINUTOS_MAX_REGULAR = 5;
    private final int MINUTOS_MAX_TARDANZA = 15;
    private final int MINUTOS_ANTES_MARCACION_ENTRADA = 30;
    private final int MINUTOS_MAX_MARCACION_SALIDA = 0;

    DAO<Empleado> empleadoDAO = new DAO<>(Empleado.class);
    DAO<TCImportacion> tcDAO = new DAO<>(TCImportacion.class);
    DAO<Vista> vistaDAO = new DAO<>(Vista.class);
    DAO<EmpleadoHorario> empleadoHorarioDAO = new DAO<>(EmpleadoHorario.class);
    DAO<Permiso> permisoDAO = new DAO<>(Permiso.class);
    DAO<Feriado> feriadoDAO = new DAO<>(Feriado.class);
    DAO<PermisoEmpleado> permisoEmpleadoDAO = new DAO<>(PermisoEmpleado.class);
    DAO<CambioTurno> cambioTurnoDAO = new DAO<>(CambioTurno.class);
    DAO<Registro> registroDAO = new DAO<>(Registro.class);

    private List<Empleado> empleados;
    private List<Falta> faltasXMes;
    private List<Tardanza> tardanzasXMes;
    private List<Registro> registroXMes;

    List<Feriado> feriadosXAnio;

    public List<Empleado> getEmpleados() {
        return empleados;
    }

    public void setEmpleados(List<Empleado> empleados) {
        this.empleados = empleados;
    }

    public List<CambioTurno> cambiosTurnoXEmpleado(String dni, int mes, int anio) {
        int primero = 1;
        int ultimo = this.ultimoDiaMes(mes, anio);

        String fechaI = "{d '" + anio + "-" + mes + "-" + primero + "'}";
        String fechaF = "{d '" + anio + "-" + mes + "-" + ultimo + "'}";

        String jpql = "SELECT ct FROM CambioTurno ct WHERE "
                + "(ct.empleado1Id.dni = :dni AND ct.horarioJornada2Id.fecha BETWEEN " + fechaI + " AND " + fechaF + " ) "
                + "OR "
                + "(ct.empleado2Id.dni = :dni AND ct.horarioJornada1Id.fecha BETWEEN " + fechaI + " AND " + fechaF + " ) "
                + "ORDER BY ct.horarioJornada1Id.fecha, ct.horarioJornada2Id.fecha ASC ";

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("dni", dni);

        return this.cambioTurnoDAO.buscar(jpql, parametros);

    }

    private List<Permiso> permisosXEmpleado(String dni, int mes, int anio) {
        int primero = 1;
        int ultimo = this.ultimoDiaMes(mes, anio);

        String fechaI = "{d '" + anio + "-" + mes + "-" + primero + "'}";
        String fechaF = "{d '" + anio + "-" + mes + "-" + ultimo + "'}";

        String jpql = "SELECT pe FROM PermisoEmpleado pe"
                + " WHERE pe.empleado.dni = :dni AND"
                + " (pe.permiso.porFecha = false AND pe.permiso.fecha BETWEEN " + fechaI + " AND " + fechaF + ") "
                + " OR "
                + " (pe.permiso.porFecha = true AND "
                + "    (pe.permiso.fEntrada BETWEEN " + fechaI + " AND " + fechaF + " OR pe.permiso.fSalida BETWEEN " + fechaI + " AND " + fechaF + "))";
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("dni", dni);

        List<PermisoEmpleado> lista = permisoEmpleadoDAO.buscar(jpql, parametros);
        List<Permiso> permisos = new ArrayList<>();
        for (PermisoEmpleado pe : lista) {
            permisos.add(pe.getPermiso());
        }

        return permisos;
    }

    public void iniciarAnalisis(Date fechaInicio, Time horaInicio, Date fechaFin, Time horaFin) {
        Calendar cal = Calendar.getInstance();

        if (fechaFin == null) {
            fechaFin = new Date(cal.getTimeInMillis());
        }

        if (horaFin == null) {
            horaFin = new Time(cal.getTimeInMillis());
        }

        this.fechaInicio = fechaInicio;
        this.horaInicio = horaInicio;

        this.fechaFin = fechaFin;
        this.horaFin = horaFin;

        List<HorarioJornada> turnosXMes;
//        List<Feriado> feriadosXAnio;
        List<Permiso> permisosXMes;
        List<CambioTurno> cambiosTurnoXMes;
        List<Vista> marcacionesXMes;

        int anio = cal.get(Calendar.YEAR);
        cal.setTime(fechaInicio);
        feriadosXAnio = this.cargarFeriados(anio);

        while (this.fechaInicio.compareTo(fechaFin) <= 0) {

            int mes = cal.get(Calendar.MONTH) + 1;

            if (anio != cal.get(Calendar.YEAR)) {
                anio = cal.get(Calendar.YEAR);
                feriadosXAnio = this.cargarFeriados(anio);
            }

            //SEA REALIZA UN ANALISIS POR MES Y POR EMPLEADO :D 
            for (Empleado empleado : this.empleados) {
                turnosXMes = this.turnosXEmpleado(empleado.getDni(), mes, anio);
                permisosXMes = this.permisosXEmpleado(empleado.getDni(), mes, anio);
                cambiosTurnoXMes = this.cambiosTurnoXEmpleado(empleado.getDni(), mes, anio);
                marcacionesXMes = this.vistaXEmpleado(empleado.getDni(), mes, anio);

                for (HorarioJornada turno : turnosXMes) {
                    if (turno.getHorario().getPorFecha()) {
                        analizarRegistroAsistencial(empleado, turno, marcacionesXMes, permisosXMes, cambiosTurnoXMes);
                    } else {
                        analizarRegistroAdministrativo(empleado, turno, marcacionesXMes, permisosXMes);
                    }
                }
            }

            cal.setTime(this.fechaInicio);
            cal.add(Calendar.MONTH, 1);
            cal.set(Calendar.DAY_OF_MONTH, 1);
        }

    }

    public List<HorarioJornada> turnosXEmpleado(String dni, int mes, int anio) {

        int primero = 1;
        int ultimo = this.ultimoDiaMes(mes, anio);

        String fechaI = "{d '" + anio + "-" + mes + "-" + primero + "'}";
        String fechaF = "{d '" + anio + "-" + mes + "-" + ultimo + "'}";

        String sql = "SELECT eh FROM EmpleadoHorario eh"
                + " WHERE"
                + " eh.empleado.dni = :dni"
                + " AND (eh.horario.porFecha = false"
                + " OR (eh.horario.porFecha = true AND eh.horario.fechaRegistro BETWEEN " + fechaI + " AND " + fechaF + "))";

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("dni", dni);

        List<EmpleadoHorario> empleadoHorarios = empleadoHorarioDAO.buscar(sql, parametros);
        List<HorarioJornada> lista = new ArrayList<>();
        for (EmpleadoHorario empleadoHorario : empleadoHorarios) {
            List<HorarioJornada> horarioJornadas = empleadoHorario.getHorario().getHorarioJornadaList();
            lista.addAll(horarioJornadas);
        }
        return lista;
    }

    private int ultimoDiaMes(int mes, int anio) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(anio, mes - 1, 1);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public List<Feriado> cargarFeriados(int anio) {
        String jpql = "SELECT f FROM Feriado f WHERE f.anio = :anio";
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("anio", anio + "");

        return feriadoDAO.buscar(jpql, parametros);
    }

    private List<Vista> vistaXEmpleado(String dni, int mes, int anio) {
        int primero = 1;
        int ultimo = this.ultimoDiaMes(mes, anio);

        String fechaI = "{d '" + anio + "-" + mes + "-" + primero + "'}";
        String fechaF = "{d '" + anio + "-" + mes + "-" + ultimo + "'}";

        String jpql = "SELECT v FROM Vista v WHERE v.dni = :dni AND v.fecha BETWEEN " + fechaI + " AND " + fechaF;

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("dni", dni);

        return this.vistaDAO.buscar(jpql, parametros);
    }

    private void analizarRegistroAsistencial(Empleado empleado, HorarioJornada turno, List<Vista> marcacionesXMes, List<Permiso> permisosXMes, List<CambioTurno> cambiosTurnoXMes) {
        CambioTurno cambioTurno = getCambioTurno(turno, cambiosTurnoXMes);
        Registro registro = new Registro();
        Calendar cal = Calendar.getInstance();

        cal.setTime(turno.getJornada().getEntrada());
        cal.add(Calendar.MINUTE, MINUTOS_MAX_TARDANZA);
        //HORA CON LA QUE SE VA A COMPARAR LA HORA DE INICIO
        java.util.Date horaEntrada = cal.getTime();

        cal.setTime(turno.getJornada().getSalida());
        cal.add(Calendar.MINUTE, MINUTOS_MAX_MARCACION_SALIDA);
        //HORA CON LA QUE SE VA A COMPARAR LA HORA DE SALIDA
        java.util.Date horaSalida = cal.getTime();

        if (cambioTurno != null) {
            //getCambioTurno nos indica cual es el cambio de turno correspondiente
            //al turno que se esta analizando en este mes
            //se debe tener en cuenta tres situaciones: ASISTENCIA, TARDANZA y FALTA

        } else if (!isEstaEnPermisoXFecha(turno, permisosXMes)) {
            //Analizamos los registros correspondientes al turno
            //Análisis de si existe un registro previo
            if (turno.getFecha().compareTo(fechaInicio) == 0) {
                if (horaSalida.compareTo(horaInicio) < 0 && !turno.getJornada().getTerminaDiaSiguiente()) {
                    //AQUI NO SUCEDE ABSOLUTAMENTE NADA YA QUE EL TURNO HA SIDO ANALIZADO 
                } else if (horaEntrada.compareTo(horaInicio) < 0) {
                    //SE ANALIZA LA HORA DE SALIDA NADA MAS BUSCANDO LA HORA DE ENTRADA DEL TURNO EN EL MES

                }
            }
            if (turno.getFecha().compareTo(fechaFin) == 0) {
                if (horaSalida.compareTo(horaFin) > 0 || turno.getJornada().getTerminaDiaSiguiente()) {
                    /*
                     SOLO BUSCAMOS EL REGISTRO QUE CORRESPONDA A LA HORA DE ENTRADA 
                     ANALIZAMOS Y SI ES FALTA, TARDANZA O ASISTENCIA REGULAR                    
                     */

                    Vista vista = buscarVista(turno.getFecha(), turno.getJornada().getEntrada(), MINUTOS_ANTES_MARCACION_ENTRADA, MINUTOS_MAX_REGULAR, marcacionesXMes);
                    if (vista == null) {
                        vista = buscarVista(turno.getFecha(), turno.getJornada().getEntrada(), MINUTOS_ANTES_MARCACION_ENTRADA, MINUTOS_MAX_TARDANZA, marcacionesXMes);
                        if (vista == null) {
                            //SE TRATA DE UNA FALTA, ES INDIFERENTE A SI HAY HORA DE SALIDA
                            registro.setTipo("FT");
                            registro.setTurno(turno);
                            registro.setEmpleadoId(empleado);
                        } else {
                            //TARDANZA DE UN TURNO NO TERMINADO ES SUSCEPTIBLE A CAMBIOS : TN
                            registro.setTipo("TN");
                            registro.setTurno(turno);
                            registro.setEmpleadoId(empleado);
                            registro.setBiometricoId(vista.getEquipoIp());
                            registro.setFecha(vista.getFecha());
                            registro.setHora(vista.getHora());
                            registro.setEOS(true);
                        }
                    } else {
                        //ASISTENCIA REGULAR PERO SIN TERMINAR: AN
                        registro.setTipo("AN");
                        registro.setTurno(turno);
                        registro.setEmpleadoId(empleado);
                        registro.setBiometricoId(vista.getEquipoIp());
                        registro.setFecha(vista.getFecha());
                        registro.setHora(vista.getHora());
                        registro.setEOS(true);
                    }
                }
            } else {
                Vista vistaEntrada = buscarVista(turno.getFecha(), turno.getJornada().getEntrada(), MINUTOS_ANTES_MARCACION_ENTRADA, MINUTOS_MAX_REGULAR, marcacionesXMes);

                java.util.Date fechaFinal;
                if (turno.getJornada().getTerminaDiaSiguiente()) {
                    cal.setTime(turno.getFecha());
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    fechaFinal = cal.getTime();
                } else {
                    fechaFinal = turno.getFecha();
                }

                Vista vistaSalida = buscarVista(fechaFinal, turno.getJornada().getSalida(), 0, MINUTOS_MAX_MARCACION_SALIDA, marcacionesXMes);

                if (vistaEntrada == null) {
                    vistaEntrada = buscarVista(turno.getFecha(), turno.getJornada().getEntrada(), MINUTOS_ANTES_MARCACION_ENTRADA, MINUTOS_MAX_TARDANZA, marcacionesXMes);
                    if (vistaEntrada == null) {
                        //FALTA
                        registro.setTipo("FT");
                        registro.setTurno(turno);
                        registro.setEmpleadoId(empleado);
                        registro.setFecha(turno.getFecha());
                    } else {
                        //TARDANZA
                        registro.setTipo("TT");
                        registro.setTurno(turno);
                        registro.setEmpleadoId(empleado);
                        registro.setFecha(vistaEntrada.getHora());
                        registro.setHora(vistaEntrada.getHora());

                    }
                } else {
                    //ASISTENCIA REGULAR
                }
            }
        }

    }

    private void analizarRegistroAdministrativo(Empleado empleado, HorarioJornada turno, List<Vista> marcacionesXMes, List<Permiso> permisosXMes) {
        Calendar calendar = Calendar.getInstance();

    }

    private CambioTurno getCambioTurno(HorarioJornada turno, List<CambioTurno> cambiosTurnoXMes) {
        for (CambioTurno cambioTurno : cambiosTurnoXMes) {
            Calendar calendario = Calendar.getInstance();
            if (cambioTurno.getHorarioJornada1Id().equals(turno)) {
                return cambioTurno;
            } else if (cambioTurno.getHorarioJornada2Id().equals(turno)) {
                return cambioTurno;
            }
        }
        return null;
    }

    private boolean isEstaEnPermisoXFecha(HorarioJornada turno, List<Permiso> permisosXMes) {
        for (Permiso permiso : permisosXMes) {
            if (permiso.getPorFecha() && permiso.getFSalida().compareTo(turno.getFecha()) >= 0 && permiso.getFEntrada().compareTo(turno.getFecha()) <= 0) {
                return true;
            }
        }
        return false;
    }

    private Registro buscarRegistroXTurno(HorarioJornada turno) {
        String jpql = "SELECT r FROM Registro r WHERE r.turno = :turno";
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("turno", turno);

        return this.registroDAO.buscar(jpql, parametros, -1, 1).get(0);
    }

    public Vista buscarVista(java.util.Date fecha, java.util.Date hora, int minimo, int maximo, List<Vista> marcacionesXMes) {
        Vista menor = null;
        long resta = 0;
        long comp;
        boolean bandera = true;
        Calendar cal = Calendar.getInstance();
        cal.setTime(hora);
        cal.add(Calendar.MINUTE, maximo);

        java.util.Date horaMaxima = cal.getTime();

        cal.add(Calendar.MINUTE, -maximo - minimo);
        java.util.Date horaMinima = cal.getTime();
        LOG.info(horaMinima.toString());
        for (Vista marcacion : marcacionesXMes) {
            if (marcacion.getFecha().compareTo(fecha) == 0) {
                if (marcacion.getHora().compareTo(horaMaxima) <= 0 && marcacion.getHora().compareTo(horaMinima) >= 0) {
                    comp = Math.abs(marcacion.getHora().getTime() - hora.getTime());
                    if (bandera) {
                        resta = comp;
                        menor = marcacion;
                        bandera = false;
                    } else if (resta > comp) {
                        resta = comp;
                        menor = marcacion;
                    }
                }
            }
        }
        return menor;
    }
    private static final Logger LOG = Logger.getLogger(AnalisisAsistencia.class.getName());

}
