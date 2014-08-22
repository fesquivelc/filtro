/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pruebas.pruebafiltro3;

import com.pruebas.algoritmo.CalculoAsistencia;
import com.pruebas.dao.DAO;
import com.pruebas.entidades.Permiso;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author RyuujiMD
 */
public class Main {
    public static void main(String[] args) {
//        DAO<Permiso> permisos = new DAO<>(Permiso.class);
//        String jpql = "SELECT p FROM Permiso p WHERE p.fecha BETWEEN {d '2014-08-01'} AND {d '2014-08-31'}";
//        Map<String, Object> mapa = new HashMap<>();
//        mapa.put("finicio", "2014-08-01");
////        permisos.buscar(jpql, mapa);
//        System.out.println("TAMANO: "+permisos.buscar(jpql).size());
        CalculoAsistencia filtro = new CalculoAsistencia();        
        filtro.realizarAnalisis();
    }
}
