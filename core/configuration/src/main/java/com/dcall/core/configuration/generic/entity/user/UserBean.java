package com.dcall.core.configuration.generic.entity.user;

import com.dcall.core.configuration.generic.entity.Entity;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserBean implements User<String> {
    private String id;
    private String name;
    private String surname;
    private String email;
    private String login;
    private String password;
    private String path;

    public UserBean() {}

    public UserBean(@JsonProperty final String id, @JsonProperty final String name, @JsonProperty final String surname,
                    @JsonProperty final String email, @JsonProperty final String login, @JsonProperty final String password,
                    @JsonProperty final String path) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.login = login;
        this.password = password;
        this.path = path;
    }

    // getters
    @Override public String getId() { return this.id; }
    @Override public String getName() { return name; }
    @Override public String getSurname() { return surname; }
    @Override public String getEmail() { return email; }
    @Override public String getLogin() { return login; }
    @Override public String getPassword() { return password; }
    @Override public String getPath() { return path; }

    // setters
    @Override public Entity<String> setId(final String id) { this.id = id; return this; }
    @Override public User<String> setName(final String name) { this.name = name; return this; }
    @Override public User<String> setSurname(final String surname) { this.surname = surname; return this; }
    @Override public User<String> setEmail(final String email) { this.email = email; return this; }
    @Override public User<String> setLogin(final String login) { this.login = login; return this; }
    @Override public User<String> setPassword(final String password) { this.password = password; return this; }
    @Override public User<String> setPath(final String path) { this.path = path; return this; }

}
