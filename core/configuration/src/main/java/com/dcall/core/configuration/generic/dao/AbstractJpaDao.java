package com.dcall.core.configuration.generic.dao;

import com.dcall.core.configuration.generic.entity.Entity;
import com.dcall.core.configuration.app.exception.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Abstract Data Access Object
 *
 * @param <T>  Interface contract
 * @param <ID> Primary key type
 * @param <B>  bean implementation of T interface
 */
public abstract class AbstractJpaDao<B extends T, T extends Entity<ID>, ID extends Serializable> implements GenericDao<T, ID> {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractJpaDao.class);

    private final Class<T> type;
    private final String mPersistentClassName;

    @PersistenceContext
    EntityManager entityManager;

    public AbstractJpaDao() {
        this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.mPersistentClassName = this.type.getName().substring(this.type.getName().lastIndexOf('.') + 1);
    }

    public Class<T> getPersistentClass() {
        return type;
    }

    public T findById(ID id) throws TechnicalException {
        try {
            return entityManager.find(getPersistentClass(), id);
        } catch (final Exception e) {
            throw new TechnicalException("[DAO] Erreur - findById()", e);
        }
    }

    @Override
    public List<T> findAll() throws TechnicalException {
        try {
            return entityManager.createQuery("from " + type.getName()).getResultList();
        }
        catch (Exception e) {
            throw new TechnicalException("[DAO] - findAll()", e);
        }
    }

    @Override
    public long countAll() throws TechnicalException {
        try {
            return (Long) entityManager.createQuery("select count(*) from " + mPersistentClassName).getSingleResult();
        } catch (final Exception e) {
            throw new TechnicalException("[DAO] - countAll()", e);
        }
    }

    @Override
    public T merge(T bo) throws TechnicalException {
        try {
            return entityManager.merge((B) bo);
        } catch (final Exception e) {
            throw new TechnicalException("[DAO] - merge()", e);
        }
    }

    @Override
    public T save(T bo) throws TechnicalException {
        try {
            entityManager.persist((B) bo);
            return bo;
        } catch (final Exception e) {
            throw new TechnicalException("[DAO] - save()", e);
        }
    }

    @Override
    public void delete(final T bo) throws TechnicalException {
        try {
            entityManager.remove((B) bo);
            entityManager.flush();
        } catch (final Exception e) {
            throw new TechnicalException("[DAO] - delete()", e);
        }
    }

    @Override
    public void purgeTable() {
        entityManager.createQuery("DELETE FROM " + type.getName()).executeUpdate();
    }

    /**
     * Flush de la session hibernate
     *
     * @throws TechnicalException Exception DAO
     */
    public void flush() throws TechnicalException {
        try {
            entityManager.flush();
        } catch (final Exception e) {
            throw new TechnicalException("[DAO] - flush()", e);
        }
    }

    /**
     * Clear de la session
     *
     * @throws TechnicalException Exception DAO
     */
    public void clear() throws TechnicalException {
        try {
            entityManager.clear();
        } catch (final Exception e) {
            throw new TechnicalException("[DAO] - clear()", e);
        }
    }

    @Override
    public void detach(final T bo) throws TechnicalException {
        try {
            entityManager.detach(bo);
        } catch (final Exception e) {
            throw new TechnicalException("[DAO] - detach()", e);
        }
    }

    @Override
    public boolean isDetached(final T bo) throws TechnicalException {
        try {
            return !entityManager.contains(bo);
        } catch (final Exception e) {
            throw new TechnicalException("[DAO] - isDetache()", e);
        }
    }
}
