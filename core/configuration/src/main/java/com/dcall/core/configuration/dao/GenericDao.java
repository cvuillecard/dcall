package com.dcall.core.configuration.dao;

import java.io.Serializable;
import java.util.List;

/**
 * Abstract Data Access Object methods definition
 *
 * @param <T>  Interface contract
 * @param <ID> Primary key type
 */
public interface GenericDao<T, ID extends Serializable>
{
   /**
    * find an entity by its id
    * 
    * @param id
    * @return entity
    */
   T findById(final ID id) throws Exception;

    /**
     * find all entities of entity's table definition
     *
     * @return iterable entities
     * @throws Exception
     */
   Iterable<T> findAll() throws Exception;

   /**
    * Count the number of entries in table for this entity
    * 
    * @return number of entries in table
    */
   long countAll() throws Exception;

   /**
    * Save a new entity in database
    * 
    * @param bo
    * @return saved entity
    */
   T save(final T bo) throws Exception;
   
   /**
    * Merge the object with id and update the value in database
    * 
    * @param bo
    * @return persisted entity
    */
   T merge(final T bo) throws Exception;
   
   /**
    * delete the entry for the object id in table
    * 
    * @param bo
    */
   void delete(final T bo) throws Exception;

   /**
    * delete all entries in table entity
    *
    * @throws Exception
    */
   void purgeTable() throws Exception;

   /**
    * Clear session
    *
    * @throws Exception
    */
   void clear() throws  Exception;

   /**
    * detach an entity from session cache
    * 
    * @param bo
    */
   void detach(final T bo) throws Exception;

   /**
    * Vérifie si un object est détaché
    *
    * @param bo
    * @return true if object is attached to session cache
    */
   boolean isDetached(final T bo) throws Exception;
}
