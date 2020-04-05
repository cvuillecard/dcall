package com.dcall.core.configuration.service;

public interface GenericService<T, ID> {
	T create(final T bo) throws Exception;
	T update(final T bo) throws Exception;
	T findById(final ID id) throws Exception;
	Iterable<T> findAll() throws Exception;
	void delete(final T bo) throws Exception;
}
