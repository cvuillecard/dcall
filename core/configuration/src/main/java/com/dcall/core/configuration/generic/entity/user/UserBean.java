package com.dcall.core.configuration.generic.entity.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserBean implements User {
    private String name;
    private String surname;
    private String email;
    private String login;
    private String password;

    public UserBean() {}

    public UserBean(@JsonProperty final String name, @JsonProperty final String surname, @JsonProperty final String email, @JsonProperty final String login, @JsonProperty final String password) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.login = login;
        this.password = password;
    }

    // getters
    @Override public String getName() { return name; }
    @Override public String getSurname() { return surname; }
    @Override public String getEmail() { return email; }
    @Override public String getLogin() { return login; }
    @Override public String getPassword() { return password; }

    // setters
    @Override public void setName(final String name) { this.name = name; }
    @Override public void setSurname(final String surname) { this.surname = surname; }
    @Override public void setEmail(final String email) { this.email = email; }
    @Override public void setLogin(final String login) { this.login = login; }
    @Override public void setPassword(final String password) { this.password = password; }
}
