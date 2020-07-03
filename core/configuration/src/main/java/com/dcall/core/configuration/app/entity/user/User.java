package com.dcall.core.configuration.app.entity.user;

import com.dcall.core.configuration.app.entity.Entity;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public interface User<ID> extends Entity<ID> {
    // getters
    @JsonGetter("name")String getName();
    @JsonGetter("surname") String getSurname();
    @JsonGetter("email") String getEmail();
    @JsonGetter("login") String getLogin();
    @JsonGetter("password") String getPassword();
    @JsonGetter("path") String getWorkspace();

    // setters
    @JsonSetter("name") User<ID> setName(final String name);
    @JsonSetter("surname") User<ID> setSurname(final String surname);
    @JsonSetter("email") User<ID> setEmail(final String email);
    @JsonSetter("login") User<ID> setLogin(final String login);
    @JsonSetter("password") User<ID> setPassword(final String password);
    @JsonSetter("path") User<String> setWorkspace(String path);

    // util
    User<ID> reset();
}
