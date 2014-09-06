/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pruebas.pruebafiltro3;

import com.pruebas.algoritmo.AnalisisAsistencia;
import com.pruebas.algoritmo.CalculoAsistencia;
import com.pruebas.dao.DAO;
import com.pruebas.entidades.HorarioJornada;
import com.pruebas.entidades.Permiso;
import com.pruebas.entidades.Vista;
import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author RyuujiMD
 */
public class Main {

    public static void main(String[] args) {
        AnalisisAsistencia aa = new AnalisisAsistencia();
        DAO<Vista> vistaDAO = new DAO<>(Vista.class);
        String dni = "18033904";

        String jpql = "SELECT r FROM Vista r WHERE r.dni = :dni";
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("dni", Integer.parseInt(dni));

        List<Vista> marcaciones = vistaDAO.buscar(jpql, parametros);

        HorarioJornada hj = aa.turnosXEmpleado(dni, 8, 2014).get(0);

        LOG.info(hj.toString());

        Vista marcacion = aa.filtrarMarcacion(hj.getFecha(), hj.getJornada().getEntrada(), 30, 0, marcaciones);

        LOG.log(Level.INFO, "EL MENOR PARA EL DNI: {0} ES: {1}", new String[]{dni, marcacion.getHora().toString()});
    }
    private static final Logger LOG = Logger.getLogger(Main.class.getName());
}
