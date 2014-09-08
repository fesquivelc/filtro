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
import com.pruebas.entidades.Horario;
import com.pruebas.entidades.HorarioJornada;
import com.pruebas.entidades.Permiso;
import com.pruebas.entidades.PermisoEmpleado;
import com.pruebas.entidades.Registro;
import com.pruebas.entidades.TCImportacion;
import com.pruebas.entidades.Tardanza;
import com.pruebas.entidades.Vista;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author RyuujiMD
 */
public class UtilitarioAsistencia {

    private static final Logger Log = Logger.getLogger(UtilitarioAsistencia.class.getName());

    int toleranciaMAX = 5; //minutos hasta los cuales se puede marcar sin que sea computado como tardanza 
    int tardanzaMAX = 10; //minutos de tolerancia para que sea considerado como falta

    //FECHA Y HORA DE PARTIDA PARA EL ANALISIS
    Date fechaPartida;
    Date horaPartida;

    Date fechaLlegada;
    Date horaLlegada;

    int mesCursor;
    int anioCursor;
    int diaCursor;

//    List<Empleado> empleados;
    List<Feriado> feriados;
    Connection connSQLServer;

    List<Registro> registroMensual;
    List<Falta> faltasMensuales;
    List<Tardanza> tardanzasMensuales;

    String usuario;
    String contrasena;
    String url;
    String driverManager;
    String query;
    String queryPlus;

    Properties properties;

    //TODOS LOS DAO
    DAO<Empleado> empleadoDAO = new DAO<>(Empleado.class);
    DAO<TCImportacion> tcDAO = new DAO<>(TCImportacion.class);
    DAO<Vista> vistaDAO = new DAO<>(Vista.class);
    DAO<EmpleadoHorario> empleadoHorarioDAO = new DAO<>(EmpleadoHorario.class);
    DAO<Permiso> permisoDAO = new DAO<>(Permiso.class);
    DAO<Feriado> feriadoDAO = new DAO<>(Feriado.class);
    DAO<PermisoEmpleado> permisoEmpleadoDAO = new DAO<>(PermisoEmpleado.class);
    DAO<CambioTurno> cambioTurnoDAO = new DAO<>(CambioTurno.class);

    public UtilitarioAsistencia() {
        try {
            File fileProperties = new File("configuracion.properties");
//            fileProperties.createNewFile();
            Log.log(Level.INFO, "ARCHIVO RUTA: {0}", fileProperties.getAbsolutePath());
            FileInputStream fileInputStreamProperties = new FileInputStream(fileProperties);

            properties = new Properties();
            properties.load(fileInputStreamProperties);

            this.usuario = properties.getProperty("usuario");
            this.contrasena = properties.getProperty("contrasena");
            this.url = properties.getProperty("url");
            this.driverManager = properties.getProperty("driverManager");

            this.query = properties.getProperty("query");
            this.queryPlus = properties.getProperty("queryPlus");

        } catch (FileNotFoundException ex) {
            Logger.getLogger(UtilitarioAsistencia.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UtilitarioAsistencia.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void conectarBioStar() {
        try {
            Class.forName(driverManager);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(UtilitarioAsistencia.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            connSQLServer = DriverManager.getConnection(url, usuario, contrasena);
        } catch (SQLException ex) {
            Logger.getLogger(UtilitarioAsistencia.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void crearEspejo() {
        List<TCImportacion> importaciones = tcDAO.buscar("SELECT t FROM TCImportacion t ORDER BY t.id DESC", null, -1, 1);
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
        /*
         SE CREAN LOS PUNTOS EN LOS CUALES TERMINARA EL ANALISIS MES A MES
         */
        fechaLlegada = tc.getFecha();
        horaLlegada = tc.getHora();

        tcDAO.guardar(tc);
    }

    private void cargaMasiva() {
        this.cargaMasiva(null, null);
    }

    public void cargaMasiva(Date fecha, Date hora) {
        this.conectarBioStar();
        List<Vista> vistas = new ArrayList<>();
        PreparedStatement ps;
        Timestamp t = new Timestamp(tardanzaMAX);
        try {
            if (fecha == null || hora == null) {
                ps = connSQLServer.prepareStatement(this.query);
            } else {
                ps = connSQLServer.prepareStatement(this.query + " " + this.queryPlus);

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
            Logger.getLogger(UtilitarioAsistencia.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (this.connSQLServer != null) {
                    this.connSQLServer.close();
                }

            } catch (SQLException ex) {
                Logger.getLogger(UtilitarioAsistencia.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

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
