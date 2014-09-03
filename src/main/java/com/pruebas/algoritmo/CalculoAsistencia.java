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
public class CalculoAsistencia {

    private static final Logger Log = Logger.getLogger(CalculoAsistencia.class.getName());

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

    public CalculoAsistencia() {
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
            Logger.getLogger(CalculoAsistencia.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CalculoAsistencia.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void conectarBioStar() {
        try {
            Class.forName(driverManager);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CalculoAsistencia.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            connSQLServer = DriverManager.getConnection(url, usuario, contrasena);
        } catch (SQLException ex) {
            Logger.getLogger(CalculoAsistencia.class.getName()).log(Level.SEVERE, null, ex);
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
        if (fechaPartida == null || horaPartida == null) {
            this.crearPuntosDePartida();
        }

        List<Empleado> empleados = empleadoDAO.buscarTodos();

        for (Empleado e : empleados) {
            this.analisisEmpleado(e);
        }

//        this.turnosXEmpleado("18033904", 8, 2014);
//        this.permisosXEmpleado("46557081", 9, 2014);
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

    private List<HorarioJornada> turnosXEmpleado(String dni, int mes, int anio) {

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
            Logger.getLogger(CalculoAsistencia.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (this.connSQLServer != null) {
                    this.connSQLServer.close();
                }

            } catch (SQLException ex) {
                Logger.getLogger(CalculoAsistencia.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private void recorrerVista(String dni) {
        DateFormat dtMes = new SimpleDateFormat("MM");
        int mes = Integer.parseInt(dtMes.format(fechaPartida));

        List<Permiso> permisos = this.permisosXEmpleado(dni, mes, 2014);

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
        DateFormat dt = new SimpleDateFormat("dd/MM/yyyy");
        for (PermisoEmpleado pe : lista) {
            Log.log(Level.INFO, "PERMISO: {0}", dt.format(pe.getPermiso().getFecha()));
            permisos.add(pe.getPermiso());
        }

        return permisos;
    }

    //LOS FERIADOS SOLO CAMBIAN CUANDO EL ANALISIS HA CAMBIADO DE AÑO
    public void cargarFeriados(int anio) {
        String jpql = "SELECT f FROM Feriado f WHERE f.anio = :anio";
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("anio", anio + "");

        this.feriados = feriadoDAO.buscar(jpql, parametros);
    }

    private int ultimoDiaMes(int mes, int anio) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(anio, mes - 1, 1);
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

    private void analisisEmpleado(Empleado empleado) {
        Calendar cal = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        String dni = empleado.getDni();

        List<Vista> registros = null;
        List<Permiso> permisos = null;
        List<HorarioJornada> turnos = null;
        List<CambioTurno> cambios = null;
//        while(fechaPartida.getTime() != fechaLlegada.getTime() )
        cal.setTime(fechaPartida);

        int anioPartida = cal.get(Calendar.YEAR); //SI CAMBIA EL AÑO SE CARGAN LOS FERIADOS DE DICHO AÑO
        int mesPartida = cal.get(Calendar.MONTH) + 1; //SI CAMBIA EL MES DE PARTIDA SE CAMBIAN LOS PERMISOS Y REGISTROS

        if (anioPartida != cal.get(Calendar.YEAR)) {
            anioPartida = cal.get(Calendar.YEAR);
            mesPartida = cal.get(Calendar.MONTH) + 1;

            this.cargarFeriados(anioPartida);
            registros = this.vistaXEmpleado(dni, mesPartida, anioPartida);
            permisos = this.permisosXEmpleado(dni, mesPartida, anioPartida);
            turnos = this.turnosXEmpleado(dni, mesPartida, anioPartida);
            cambios = this.cambiosTurnoXEmpleado(dni, mesPartida, anioPartida);
        } else if (mesPartida != cal.get(Calendar.MONTH) + 1) {
            mesPartida = cal.get(Calendar.MONTH) + 1;

            registros = this.vistaXEmpleado(dni, mesPartida, anioPartida);
            permisos = this.permisosXEmpleado(dni, mesPartida, anioPartida);
            turnos = this.turnosXEmpleado(dni, mesPartida, anioPartida);
            cambios = this.cambiosTurnoXEmpleado(dni, mesPartida, anioPartida);
        }

        this.analizarXMes(empleado, turnos, registros, permisos, cambios, mesPartida, anioPartida);

    }

    private void analizarXMes(Empleado empleado, List<HorarioJornada> turnos, List<Vista> registros, List<Permiso> permisos, List<CambioTurno> cambiosTurno, int mes, int anio) {
        int primerDia = 1;
        int ultimoDia = this.ultimoDiaMes(mes, anio);

        if (registroMensual == null) {
            this.registroMensual = new ArrayList<>();
        } else {
            this.registroMensual.clear();
        }

        if (this.faltasMensuales == null) {
            this.faltasMensuales = new ArrayList<>();
        } else {
            this.faltasMensuales.clear();
        }

        for (int dia = primerDia; dia <= ultimoDia; dia++) {
            if (isFeriado(dia, mes)) {
                //SE DEBE ANALIZAR SI TIENE AUTORIZACION PARA ASISTIR EN FERIADO
                //EN CASO DE NO SER ASI NO SE TOMAN EN CUENTA SUS ASISTENCIAS
                //¿LOS ASISTENCIALES ASISTEN EN FERIADOS?
            } else if (isOnomastico(empleado, dia, mes)) {
                //SE ANALIZA SI TIENE UN PERMISO PARA FALTAR POR ONOMASTICO
                //EN CASO DE NO SER ASI SE VERIFICA SI HA ASISTIDO
                //EN CASO DE HABER ASISTIDO SE MARCA COMO ASISTENCIA EN ONOMASTICO
                //EN CASO DE NO HABER ASISTIDO SE GUARDA UN PERMISO
                //EN CASO DE QUE EL ONOMASTICO SEA EN UNA FECHA NO LABORABLE SE BUSCA LA FALTA EN CUALQUIERA DE LOS DIAS PROXIMOS
                //SE DEBE ANALIZAR SI TIENE AUTORIZACION PARA ASISTIR EN CUMPLEAÑOS                
                //PARA EMPLEADOS CON MAS DE UN AÑO
                if (isDiaLaborable(turnos, dia, mes, anio)) {
                    //GENERAR UN PERMISO AUTOMATICAMENTE
                } else {
                    //BUSCAR EL SIGUIENTE DIA LABORAL PARA DARLE EL PERMISO

                }
            } else if (isEstaEnPermisoXFecha(permisos, dia, mes)) {
                //EN ESTE CASO NO SE HACE NADA SIMPLEMENTE SE DEJA PASAR
                //ESTO ES PARA EVITAR TOMAR COMO FALTAS LOS CASOS EN LOS QUE EL ALGORITMO                
            } else if (isDiaLaborable(turnos, dia, mes, anio)) {
                //AQUI SE ENCUENTRA EL QUID DEL ASUNTO
                List<Vista> registroDiario;
                if(isDiaPartida(dia,mes,anio)){
                    registroDiario = registrosXDia(dia,mes,anio,registros,horaPartida);
                }else{
                    registroDiario = registrosXDia(dia,mes,anio,registros);
                }
            }
        }
    }

    public boolean isFeriado(int dia, int mes) {
        Calendar calI = Calendar.getInstance();
        Calendar calF = Calendar.getInstance();
        for (Feriado feriado : this.feriados) {
            calI.setTime(feriado.getFInicio());
            calF.setTime(feriado.getFFin());

            int mesI = calI.get(Calendar.MONTH) + 1;
            int mesF = calF.get(Calendar.MONTH) + 1;

            int diaI = calI.get(Calendar.DAY_OF_MONTH);
            int diaF = calF.get(Calendar.DAY_OF_MONTH);

            if ((dia <= diaF && dia >= diaI) && (mes == mesI || mes == mesF)) {
                return true;
            }
        }
        return false;
    }

    public boolean isFaltaxCambioTurno(int dia, int mes, List<CambioTurno> listaCambioTurno) {
        Calendar cal = Calendar.getInstance();
        for (CambioTurno ct : listaCambioTurno) {
            cal.setTime(ct.getHorarioJornada1Id().getFecha());
            int diaCT1 = cal.get(Calendar.DAY_OF_MONTH);
            if (dia == diaCT1) {
                return true;
            }
        }
        return false;
    }

    public List<CambioTurno> cambiosTurnoXEmpleado(String dni, int mes, int anio) {
        int primero = 1;
        int ultimo = this.ultimoDiaMes(mes, anio);

        String fechaI = "{d '" + anio + "-" + mes + "-" + primero + "'}";
        String fechaF = "{d '" + anio + "-" + mes + "-" + ultimo + "'}";

        String jpql = "SELECT ct FROM CambioTurno ct WHERE (ct.empleado1Id.dni = :dni OR ct.empleado2Id.dni = :dni) "
                + " AND ((ct.horarioJornada1Id.fecha BETWEEN " + fechaI + " AND " + fechaF + " ) "
                + " OR (ct.horarioJornada2Id.fecha BETWEEN " + fechaI + " AND " + fechaF + " )) "
                + " ORDER BY ct.horarioJornada1Id.fecha, ct.horarioJornada2Id.fecha ASC ";

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("dni", dni);

        return this.cambioTurnoDAO.buscar(jpql, parametros);

    }

    private boolean isOnomastico(Empleado empleado, int dia, int mes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(empleado.getFechaNacimiento());

        int diaOnomastico = cal.get(Calendar.DAY_OF_MONTH);
        int mesOnomastico = cal.get(Calendar.MONTH) + 1;

        if (dia == diaOnomastico && mes == mesOnomastico) {
            return true;
        }
        return false;
    }

    private boolean isEstaEnPermisoXFecha(List<Permiso> permisos, int dia, int mes) {
        Calendar calEntrada = Calendar.getInstance();
        Calendar calSalida = Calendar.getInstance();
        for (Permiso permiso : permisos) {
            if (permiso.getPorFecha()) {
                calEntrada.setTime(permiso.getFEntrada());
                calSalida.setTime(permiso.getFSalida());
                int diaEntrada = calEntrada.get(Calendar.DAY_OF_MONTH);
                int diaSalida = calSalida.get(Calendar.DAY_OF_MONTH);

                int mesEntrada = calEntrada.get(Calendar.MONTH) + 1;
                int mesSalida = calSalida.get(Calendar.MONTH) + 1;

                if ((dia >= diaSalida && dia <= diaEntrada) && (mes >= mesSalida && mes <= mesEntrada)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isDiaLaborable(List<HorarioJornada> turnos, int dia, int mes, int anio) {
        //ANALIZA TANTO PARA ADMIN COMO PARA
        Calendar cal = Calendar.getInstance();
        cal.set(anio, mes - 1, dia);
        int diaSemana = cal.get(Calendar.DAY_OF_WEEK);

        for (HorarioJornada turno : turnos) {
            int diaLaboral;

            if (turno.getHorario().getPorFecha()) {
                cal.setTime(turno.getFecha());
                diaLaboral = cal.get(Calendar.DAY_OF_WEEK);
            } else {
                diaLaboral = getDiaLaboralAdministrativo(turno.getHorario());
            }

            if (diaSemana == diaLaboral) {
                return true;
            }
        }
        return false;
    }

    private int getDiaLaboralAdministrativo(Horario horario) {
        if (horario.getDomingo()) {
            return 1;
        } else if (horario.getLunes()) {
            return 2;
        } else if (horario.getMartes()) {
            return 3;
        } else if (horario.getMiercoles()) {
            return 4;
        } else if (horario.getJueves()) {
            return 5;
        } else if (horario.getViernes()) {
            return 6;
        } else {
            return 7;
        }
    }

    private boolean isDiaPartida(int dia, int mes, int anio) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaPartida);
        
        int diaPartida = cal.get(Calendar.DAY_OF_MONTH);
        int mesPartida = cal.get(Calendar.MONTH);
        int anioPartida = cal.get(Calendar.YEAR);
        
        if(dia == diaPartida && mes == mesPartida && anio == anioPartida){
            return true;
        }
        return false;
    }

    private List<Vista> registrosXDia(int dia, int mes, int anio, List<Vista> registrosXMes) {
        return this.registrosXDia(dia, mes, anio, registrosXMes, null, null);
    }

    private List<Vista> registrosXDia(int dia, int mes, int anio, List<Vista> registrosXMes, Date horaPartida) {
        return this.registrosXDia(dia, mes, anio, registrosXMes, horaPartida, null);
    }
    
    private List<Vista> registrosXDia(int dia, int mes, int anio, List<Vista> registrosXMes, Date horaPartida, Date horaFin){
        Calendar cal = Calendar.getInstance();
        if(horaPartida == null){
            cal.set(anio, mes, dia, 0, 0);
            horaPartida = cal.getTime();
        }
        if(horaFin == null){
            cal.set(anio, mes, dia, 11, 59, 59);
            horaFin = cal.getTime();
        }
        cal.set(anio, mes, dia);
        Date fechaActual = cal.getTime();
        
        for(Vista registro : registrosXMes){
            if(registro.equals(fechaActual) && registro.getHora().after(horaPartida) && registro.getHora().before(horaFin)){
                
            }
        }        
        
        return null;
    }

}
