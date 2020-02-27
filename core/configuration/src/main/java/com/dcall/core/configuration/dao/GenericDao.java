package com.dcall.core.configuration.dao;

import java.io.Serializable;
import java.util.List;

public interface GenericDao<T, ID extends Serializable>
{
   /**
    * Recherche par ID
    * 
    * @param id Id de l'objet
    * @return Object
    */
   T findById(ID id) throws Exception;

    /**
     * Recherche toutes les entités d'un même type
     *
     * @return
     * @throws Exception
     */
   List<T> findAll() throws Exception;

   /**
    * Compte le nombre d'objet
    * 
    * @return le nombre d'objet
    */
   long countAll() throws Exception;

   /**
    * Creation d'un objet
    * 
    * @param bo l'objet à créer
    * @return l'objet crée
    */
   T save(T bo) throws Exception;
   
   /**
    * Modification d'un objet
    * 
    * @param bo l'objet à modifier
    * @return l'objet persisté
    */
   T merge(T bo) throws Exception;
   
   /**
    * Supression d'un objet
    * 
    * @param bo objet à supprimer
    */
   void delete(T bo) throws Exception;

    /**
     * Suppression de tous les objets - equivaut a 'delete from objet_table;'
     *
     * @throws Exception
     */
   void purgeTable() throws Exception;

   /**
    * Permet de détacher un objet
    * 
    * @param bo objet à initialiser
    */
   void detach(T bo) throws Exception;

   /**
    * Vérifie si un object est détaché
    *
    * @param bo objet à vérifier
    * @return true si l'objet est détaché
    */
   boolean isDetached(T bo) throws Exception;
}
