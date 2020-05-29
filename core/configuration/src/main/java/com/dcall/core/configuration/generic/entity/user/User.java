package com.dcall.core.configuration.generic.entity.user;

import com.dcall.core.configuration.generic.entity.Entity;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public interface User<ID> extends Entity<ID> {
    // getters
    @JsonGetter("name")String getName();
    @JsonGetter("surname") String getSurname();
    @JsonGetter("email") String getEmail();
    @JsonGetter("login") String getLogin();
    @JsonGetter("password") String getPassword();
    @JsonGetter("path") String getPath();

    // setters
    @JsonSetter("name") User<ID> setName(final String name);
    @JsonSetter("surname") User<ID> setSurname(final String surname);
    @JsonSetter("email") User<ID> setEmail(final String email);
    @JsonSetter("login") User<ID> setLogin(final String login);
    @JsonSetter("password") User<ID> setPassword(final String password);
    @JsonSetter("path") User<String> setPath(String path);
}
