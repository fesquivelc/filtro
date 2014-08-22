/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pruebas.dao;

import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author RyuujiMD
 * @param <T>
 */
public class DAO<T> {

    private final String PU = "postgresql-PU";
    private EntityManager em;  
    private final Class<T> clase;

    public DAO(Class<T> clase) {
        this.clase = clase;
    }
    
    

    public EntityManager getEntityManager() {
        if (em == null) {
            EntityManagerFactory emf
                    = Persistence.createEntityManagerFactory(this.PU);
            em = emf.createEntityManager();
        }
        return em;
    }    

    public void guardar(T objeto) {
        
        getEntityManager().getTransaction().begin();
        getEntityManager().persist(objeto);
        getEntityManager().getTransaction().commit();
    }  
    
    public void guardarLote(List<T> objetos){
        getEntityManager().getTransaction().begin();
        for(T objeto : objetos){
            getEntityManager().persist(objeto);
        }
        getEntityManager().getTransaction().commit();
    }

    public void modificar(T objeto) {
        getEntityManager().getTransaction().begin();
        getEntityManager().merge(objeto);
        getEntityManager().getTransaction().commit();
    }

    public void eliminar(T objeto) {
        getEntityManager().getTransaction().begin();
        getEntityManager().remove(objeto);
        getEntityManager().getTransaction().commit();
    }
    
    public List<T> buscar(String queryJPQL){
        return this.buscar(queryJPQL,null,-1,-1);
    }
    
    public List<T> buscar(String queryJPQL, Map<String, Object> parametros){
        return this.buscar(queryJPQL, parametros, -1, -1);
    }
    
    public List<T> buscar(String queryJPQL, Map<String, Object> parametros, int inicio, int tamanio){
        Query query = getEntityManager().createQuery(queryJPQL);

        if (parametros != null) {
            for (Map.Entry<String, Object> entry : parametros.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }                
        
        if(inicio != -1){
            query.setFirstResult(inicio);            
        }
                
        
        if(tamanio != -1){
            query.setMaxResults(tamanio);
        }
        
        List<T> lista = query.getResultList();
                
        
        return lista;
    }
    
    public int contar() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        javax.persistence.criteria.Root<T> rt = cq.from(clase);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }
    
    public List<T> buscarTodos() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(clase));
        return getEntityManager().createQuery(cq).getResultList();
    }
}
