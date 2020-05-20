package com.dcall.core.configuration.dao;

import com.dcall.core.configuration.entity.Entity;
import com.dcall.core.configuration.exception.TechnicalException;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

/**
 * Abstract Data Access Object
 *
 * @param <T>  Interface contract
 * @param <ID> Primary key type
 * @param <B>  bean implementation of T interface
 */
public abstract class AbstractNeo4jDao<B extends T, T extends Entity<ID>, ID extends Serializable> implements GenericDao<T, ID> {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractNeo4jDao.class);

	private final int DEPTH_LIST = 0;
	private final int DEPTH_ENTITY = 1;
	private final Class<T> type;
	private final String mPersistentClassName;

	@Autowired
	SessionFactory sessionFactory;

	@Autowired
	protected Session session;

	public AbstractNeo4jDao() {
		this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		this.mPersistentClassName = this.type.getName().substring(this.type.getName().lastIndexOf('.') + 1);
	}

	@Override
	public T findById(final ID id) throws TechnicalException {
		try {
			return session.load(type, id, DEPTH_ENTITY);
		} catch (final Exception e) {
			LOG.error("DAO (findById) :\r\n", e);
			throw new TechnicalException("[DAO] - findById()", e);
		}
	}

	@Override
	public Iterable<T> findAll() throws TechnicalException {
		try {
			return session.loadAll(type, DEPTH_LIST);
		} catch (final Exception e) {
			LOG.error("DAO (findAll) :\r\n", e);
			throw new TechnicalException("[DAO] - findAll()", e);
		}
	}

	@Override
	public T merge(final T bo) throws TechnicalException {
		try {
			return this.save(bo);
		} catch (final Exception e) {
			LOG.error("DAO (merge) :\r\n", e);
			throw new TechnicalException("[DAO] - merge()", e);
		}
	}

	@Override
	public T save(final T bo) throws TechnicalException {
		try {
			session.save(bo);
			return bo;
		} catch (final Exception e) {
			throw new TechnicalException("[DAO] save()", e);
		}
	}
	
	@Override
	public void delete(final T bo) throws TechnicalException {
		try {
			session.delete(bo);
		} catch (final Exception e) {
			throw new TechnicalException("[DAO] - delete()", e);
		}
	}

	@Override
	public void purgeTable() throws TechnicalException {
		try {
			session.deleteAll(type);
		} catch (Exception e) {
			throw new TechnicalException("[DAO] - deleteAll()", e);
		}
	}

	@Override
	public void clear() throws TechnicalException {
		try {
			session.clear();
		} catch (final Exception e) {
			throw new TechnicalException("[DAO] - clear()", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void detach(final T bo) throws TechnicalException {
		try {
			session.detachNodeEntity(Long.getLong(bo.getId().toString()));
		} catch (final Exception e) {
			throw new TechnicalException("[DAO] - detach()", e);
		}
	}

}
