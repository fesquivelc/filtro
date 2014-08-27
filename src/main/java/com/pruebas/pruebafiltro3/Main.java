/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pruebas.pruebafiltro3;

import com.pruebas.algoritmo.CalculoAsistencia;
import com.pruebas.dao.DAO;
import com.pruebas.entidades.Permiso;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author RyuujiMD
 */
public class Main {
    public static void main(String[] args) {
        CalculoAsistencia filtro = new CalculoAsistencia();        
//        filtro.realizarAnalisis();
//        filtro.cargarFeriados(2014);
//        
//        if(filtro.isFeriado(4, 8)){
//            LOG.info("ES FERIADO");
//        }else{
//            LOG.info("NO ES FERIADO");
//        }
        Calendar cal = Calendar.getInstance();
        cal.set(2014, 7, 31);
        
        int diaSemana = cal.get(Calendar.DAY_OF_WEEK);
        LOG.log(Level.INFO, "DIA DE LA SEMANA {0}",diaSemana);
    }
    private static final Logger LOG = Logger.getLogger(Main.class.getName());
}
