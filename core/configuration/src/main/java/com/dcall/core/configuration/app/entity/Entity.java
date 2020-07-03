package com.dcall.core.configuration.app.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.io.Serializable;

/** Base interface for all entities used in the application */
public interface Entity<ID> extends Serializable {
	@JsonGetter("id") ID getId();
	@JsonSetter("id") Entity<ID> setId(final ID id);
}
