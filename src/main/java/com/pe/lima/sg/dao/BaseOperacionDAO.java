package com.pe.lima.sg.dao;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;


/**
 * Clase abstracta DAO para las operaciones: guardar, eliminar, actualizar
 * consultar por Id y listar todas las entidades
 * 
 * @param <T> Entidad de base de datos
 * @param <ID> ID de la Entidad
 */

@NoRepositoryBean
public interface BaseOperacionDAO<T, ID extends Serializable> extends Repository<T, ID>, JpaSpecificationExecutor<T> {
 
    void delete(T deleted);
 
    List<T> findAll();
     
    T findOne(ID id);
 
    T save(T persisted);    
    
}
