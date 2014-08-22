/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pruebas.algoritmo;

import com.pruebas.dao.DAO;
import com.pruebas.entidades.Empleado;
import com.pruebas.entidades.EmpleadoHorario;
import com.pruebas.entidades.Feriado;
import com.pruebas.entidades.HorarioJornada;
import com.pruebas.entidades.Jornada;
import com.pruebas.entidades.Permiso;
import com.pruebas.entidades.PermisoEmpleado;
import com.pruebas.entidades.TCImportacion;
import com.pruebas.entidades.Vista;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author RyuujiMD
 */
public class Filtro {

    private static final Logger Log = Logger.getLogger(Filtro.class.getName());

    int toleranciaMAX = 5; //minutos hasta los cuales se puede marcar sin que sea computado como tardanza 
    int tardanzaMAX = 10; //minutos de tolerancia para que sea considerado como falta
    
    //FECHA Y HORA DE PARTIDA PARA EL ANALISIS
    Date fechaPartida;
    Date horaPartida;
    
    
    List<Empleado> empleados;
    List<Feriado> feriados;
    Connection connSQLServer;
    
    //TODOS LOS DAO
    DAO<Empleado> empleadoDAO = new DAO<>(Empleado.class);
    DAO<TCImportacion> tcDAO = new DAO<>(TCImportacion.class);
    DAO<Vista> vistaDAO = new DAO<>(Vista.class);
    DAO<EmpleadoHorario> empleadoHorarioDAO = new DAO<>(EmpleadoHorario.class);
    DAO<Permiso> permisoDAO = new DAO<>(Permiso.class);
    DAO<Feriado> feriadoDAO = new DAO<>(Feriado.class);
    DAO<PermisoEmpleado> permisoEmpleadoDAO = new DAO<>(PermisoEmpleado.class);

    private void conectarBioStar() {
        //PARAMETROS QUE DEBEN IR EN UN ARCHIVO PROPERTIES PARA EVITAR COMPILAR CODIGO
        String url = "jdbc:postgresql://127.0.0.1:5432/sqlserverbd";
        String user = "postgres";
        String password = "root";
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Filtro.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            connSQLServer = DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            Logger.getLogger(Filtro.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void realizarAnalisis() {
        this.crearEspejo();
        /*
        CUANDO SE IMPORTAN LOS DATOS DE BIOSTAR SE GENERAN LOS PUNTOS DE PARTIDA
        SIN EMBARGO ESTO ESTA SUPEDITADO A QUE HAYA EXISTIDO ANTES UN REGISTRO ANTERIOR
        PARA CASOS EN LOS CUALES NO EXISTA UN ANALISIS ANTERIOR O (MEJOR DICHO) DESDE CERO
        SE PONEN LOS PUNTOS DE PARTIDA DESDE LA FECHA Y HORA MAS ANTIGUA DE LA VISTA 
        */
        if(fechaPartida == null || horaPartida == null){
            this.crearPuntosDePartida();
        }
        this.turnosXEmpleado("18033904", 8, 2014);
        this.permisosXEmpleado("46557081", 9, 2014);
    }

    private void crearEspejo() {
        List<TCImportacion> importaciones = tcDAO.buscar("SELECT t FROM TCImportacion t ORDER BY t.id DESC", null, 0, 1);
        if (importaciones.isEmpty()) {
            this.cargaMasiva();
        } else {
            fechaPartida = importaciones.get(0).getFecha();
            horaPartida = importaciones.get(0).getHora();
            this.cargaMasiva(fechaPartida, horaPartida);
        }

        //se guardan los nuevos valores en la tabla TCImportacion
        TCImportacion tc = new TCImportacion();
        tc.setFecha(new Date());
        tc.setHora(new Date());
        tcDAO.guardar(tc);
    }

    private List<HorarioJornada> turnosXEmpleado(String dni, int mes, int anio) {
        
        int primero = 1;
        int ultimo = this.ultimoDiaMes(mes,anio);
        
        String fechaI = "{d '"+anio+"-"+mes+"-"+primero+"'}";
        String fechaF = "{d '"+anio+"-"+mes+"-"+ultimo+"'}";
        
        String sql = "SELECT eh FROM EmpleadoHorario eh"
                + " WHERE"
                + " eh.empleado.dni = :dni"
                + " AND (eh.horario.porFecha = false"
                + " OR (eh.horario.porFecha = true AND eh.horario.fechaRegistro BETWEEN "+fechaI+" AND "+fechaF+"))";
        
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

    private void cargaMasiva() {
        this.cargaMasiva(null, null);
    }

    private void analizarXMes(String dni, String mes) {

    }

    private void cargaMasiva(Date fecha, Date hora) {
        this.conectarBioStar();
        List<Vista> vistas = new ArrayList<>();
        PreparedStatement ps;
        Timestamp t = new Timestamp(tardanzaMAX);
        try {
            if (fecha == null || hora == null) {
                ps = connSQLServer.prepareStatement("select dni,equipo_ip,fecha,hora from vista");
            } else {
                ps = connSQLServer.prepareStatement(
                        "select dni,equipo_ip,fecha,hora from vista"
                        + " where fecha > ? or (fecha = ? and hora >= ?)");

                java.sql.Date pFecha = new java.sql.Date(fecha.getTime());
                Time pHora = new Time(hora.getTime());

                ps.setDate(1, pFecha);
                ps.setDate(2, pFecha);
                ps.setTime(3, pHora);
            }
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Vista vista = new Vista();
                vista.setDni(rs.getInt("dni"));
                vista.setEquipoIp(rs.getString("equipo_ip"));
                vista.setFecha(rs.getDate("fecha"));
                vista.setHora(rs.getTime("hora"));
                vistas.add(vista);
            }

            vistaDAO.guardarLote(vistas);

        } catch (SQLException ex) {
            Logger.getLogger(Filtro.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (this.connSQLServer != null) {
                    this.connSQLServer.close();
                }

            } catch (SQLException ex) {
                Logger.getLogger(Filtro.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private void recorrerVista(String dni) {
        DateFormat dtMes = new SimpleDateFormat("MM");
        int mes = Integer.parseInt(dtMes.format(fechaPartida));

        List<Permiso> permisos = this.permisosXEmpleado(dni, mes,2014);
        
    }

    private List<Permiso> permisosXEmpleado(String dni, int mes, int anio) {        
        int primero = 1;
        int ultimo = this.ultimoDiaMes(mes,anio);
        
        String fechaI = "{d '"+anio+"-"+mes+"-"+primero+"'}";
        String fechaF = "{d '"+anio+"-"+mes+"-"+ultimo+"'}";
        
        String jpql = "SELECT pe FROM PermisoEmpleado pe"
                + " WHERE pe.empleado.dni = :dni AND"
                + " (pe.permiso.porFecha = false AND pe.permiso.fecha BETWEEN "+fechaI+" AND "+fechaF+") "
                + " OR "
                + " (pe.permiso.porFecha = true AND "
                + "    (pe.permiso.fEntrada BETWEEN "+fechaI+" AND "+fechaF+" OR pe.permiso.fSalida BETWEEN "+fechaI+" AND "+fechaF+"))";
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("dni", dni);
        
        List<PermisoEmpleado> lista = permisoEmpleadoDAO.buscar(jpql, parametros);
        List<Permiso> permisos = new ArrayList<>();
        DateFormat dt = new SimpleDateFormat("dd/MM/yyyy");
        for(PermisoEmpleado pe : lista){
            Log.log(Level.INFO, "PERMISO: {0}",dt.format(pe.getPermiso().getFecha()));
            permisos.add(pe.getPermiso());
        }
        
        return permisos;
    }
    
    //LOS FERIADOS SOLO CAMBIAN CUANDO EL ANALISIS HA CAMBIADO DE AÑO
    private List<Feriado> feriadosXAnio(int anio){
        String jpql = "SELECT f FROM Feriado f WHERE f.anio = :anio";
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("anio", anio+"");
        
        return feriadoDAO.buscar(jpql, parametros);
    }

    private int ultimoDiaMes(int mes, int anio) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(anio, mes - 1 , 1);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    private void crearPuntosDePartida() {
        String jpql = "SELECT v FROM Vista v ORDER BY v.id ASC";
        
        /*
        NO HAY QUE PEDIR MAS DE UN ELEMENTO A LA CONSULTA 
        EN CASO CONTRARIO SE HACE DEMASIADO LENTO
        */
        Vista v = vistaDAO.buscar(jpql, null, -1, 1).get(0);
        
        fechaPartida = v.getFecha();
        horaPartida = v.getHora();
    }
}
