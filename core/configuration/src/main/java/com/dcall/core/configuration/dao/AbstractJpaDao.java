package com.dcall.core.configuration.dao;

import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Description de l'interface : Description des fonctions générique Hibernate
 *
 * @param <T>  ClassePersistante
 * @param <ID> Type de la clé primaire
 * @param <B>  ClassBean
 */
public abstract class AbstractJpaDao<B, T, ID extends Serializable> implements GenericDao<T, ID> {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractJpaDao.class);

    private final Class<T> type;
    private final String mPersistentClassName;

    @PersistenceContext
    EntityManager entityManager;

    public void test() {
//        NetServer server = Vertx.vertx().getOrCreateContext().owner().eventBus().
    }

    public AbstractJpaDao() {
        this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.mPersistentClassName = this.type.getName().substring(this.type.getName().lastIndexOf('.') + 1);
    }

    public Class<T> getPersistentClass() {
        return type;
    }


    public T findById(ID id) throws Exception {
        try {
            final T bo = (T) entityManager.find(getPersistentClass(), id);
            return bo;
        } catch (final Exception e) {
            LOG.error("Erreur DAO (findById) :\r\n", e);
            throw new Exception("[DAO] Erreur (findById)", e);
        }
    }

    public List<T> findAll() throws Exception {
        return entityManager.createQuery("from " + type.getName()).getResultList();
    }

    /**
     * Recherche de tous les objets
     *
     * @return le nombre d'objet
     * @throws Exception Exception DAO
     */
    public long countAll() throws Exception {
        try {
            return (Long) entityManager.createQuery("select count(*) from " + mPersistentClassName).getSingleResult();
        } catch (final Exception e) {
            LOG.error("Erreur DAO (countAll) :\r\n", e);
            throw new Exception("[DAO] Erreur (countAll)", e);
        }
    }

    /**
     * Nbr résultat recherche partielle paginée sur un champ
     *
     * @param fieldName nom de la propriété objet de la recherche
     * @param search    objet de la recherche
     * @return Une liste d'objet
     * @throws Exception Exception DAO
     */
    public long countByLike(String fieldName, String search) throws Exception {
        try {
            final String hqlQuery = "select count(*) from " + mPersistentClassName + " where " + fieldName + " like :search";

            final Query query = entityManager.createQuery(hqlQuery);
            query.setParameter("search", search);

            return (Long) query.getSingleResult();
        } catch (final Exception e) {
            LOG.error("Erreur DAO (countByLike) :\r\n", e);
            throw new Exception("[DAO] Erreur (countByLike)", e);
        }
    }

    /**
     * Recherche partielle paginée sur un champ
     *
     * @param firstResult index premier resultat
     * @param maxResult   nombre d'enregistrement max
     * @param fieldName   nom de la propriété objet de la recherche
     * @param search      objet de la recherche
     * @param orderBy     Liste des propriétés pour determiner le tri
     * @return Une liste d'objet
     * @throws Exception Exception DAO
     */
    public List<T> findByLike(int firstResult, int maxResult, String fieldName, String search, String orderBy) throws Exception {
        try {
            String hqlQuery = "from " + mPersistentClassName + " where " + fieldName + " like :search";
            if (orderBy != null && !StringUtils.isEmpty(orderBy)) {
                hqlQuery += " order by " + orderBy;
            }
            final Query query = entityManager.createQuery(hqlQuery);
            query.setParameter("search", search);

            if (firstResult >= 0) {
                query.setFirstResult(firstResult);
            }

            if (maxResult > 0) {
                query.setMaxResults(maxResult);
            }

            return query.getResultList();
        } catch (final Exception e) {
            LOG.error("Erreur DAO (findByLike) :\r\n", e);
            throw new Exception("[DAO] Erreur (findByLike)", e);
        }
    }

    /**
     * Modification d'un objet
     *
     * @param bo l'objet à modifier
     * @return l'objet persisté
     * @throws Exception Exception DAO
     */

    @SuppressWarnings({"unchecked", "cast"})
    public T merge(T bo) throws Exception {
        try {
            return (T) entityManager.merge((B) bo);
        } catch (final Exception e) {
            LOG.error("Erreur DAO (merge) :\r\n", e);
            throw new Exception("[DAO] Erreur (merge)", e);
        }
    }

    /**
     * Creation d'un objet, l'objet passé en argument est re-détaché, celui retourné est persistent
     *
     * @param bo l'objet à créer
     * @return l'objet crée persistent
     * @throws Exception Exception DAO
     */
    public T save(T bo) throws Exception {
        try {
            entityManager.persist((B) bo);
            return bo;
        } catch (final Exception e) {
            LOG.error("Erreur DAO (save) :\r\n", e);
            throw new Exception("[DAO] Erreur (save)", e);
        }
    }

    /**
     * Suppression d'un objet
     *
     * @param bo Objet
     * @throws Exception Exception DAO
     */

    public void delete(T bo) throws Exception {
        try {
            entityManager.remove((B) bo);
            entityManager.flush();
        } catch (final Exception e) {
            LOG.error("Erreur DAO (delete) :\r\n", e);
            throw new Exception("[DAO] Erreur (delete)", e);
        }
    }

    /**
     * Supprime tous les objets de la table 'obj'
     *
     * @throws Exception
     */
    public void purgeTable() throws Exception {
        entityManager.createQuery("DELETE FROM " + type.getName()).executeUpdate();
    }

    /**
     * Flush de la session hibernate
     *
     * @throws Exception Exception DAO
     */
    public void flush() throws Exception {
        try {
            entityManager.flush();
        } catch (final Exception e) {
            LOG.error("Erreur DAO (flush) :\r\n", e);
            throw new Exception("[DAO] Erreur (flush)", e);
        }
    }

    /**
     * Clear de la session
     *
     * @throws Exception Exception DAO
     */
    public void clear() throws Exception {
        try {
            entityManager.clear();
        } catch (final Exception e) {
            LOG.error("Erreur DAO (clear) :\r\n", e);
            throw new Exception("[DAO] Erreur (clear)", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void detach(T bo) throws Exception {
        try {
            entityManager.detach(bo);
        } catch (final Exception e) {
            LOG.error("Erreur DAO (detache) :\r\n", e);
            throw new Exception("[DAO] Erreur (detache)", e);
        }
    }

    public boolean isDetached(T bo) throws Exception {
        try {
            return !entityManager.contains(bo);
        } catch (final Exception e) {
            LOG.error("Erreur DAO (isDetache) :\r\n", e);
            throw new Exception("[DAO] Erreur (isDetache)", e);
        }
    }
}
