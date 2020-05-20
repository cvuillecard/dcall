package com.dcall.core.configuration.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.io.Serializable;

/** Base interface for all entities used in the application */
public interface Entity<Id> extends Serializable {
	@JsonGetter("id") Id getId();
	@JsonSetter("id") Entity<Id> setId(Id id);
}
