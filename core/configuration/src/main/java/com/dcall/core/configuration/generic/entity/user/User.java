package com.dcall.core.configuration.generic.entity.user;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public interface User {
    // getters
    @JsonGetter("name")String getName();
    @JsonGetter("surname") String getSurname();
    @JsonGetter("email") String getEmail();
    @JsonGetter("login") String getLogin();
    @JsonGetter("password") String getPassword();

    // setters
    @JsonSetter("name") void setName(final String name);
    @JsonSetter("surname") void setSurname(final String surname);
    @JsonSetter("email") void setEmail(final String email);
    @JsonSetter("login") void setLogin(final String login);
    @JsonSetter("password") void setPassword(final String password);
}
