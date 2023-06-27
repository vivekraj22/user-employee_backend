package com.finolitech.model;

import io.vertx.core.json.JsonObject;

public class User {
  private static int counter = 0;
  private Integer id;
  private String name;
  private String email;
  private Gender gender;
  private Status status;
  private String timestamp;

  public User() {
    this.id = ++counter;
  }

  public User(String name, String email, Gender gender, Status status, String timestamp) {
    this.id = ++counter;
    this.name = name;
    this.email = email;
    this.gender = gender;
    this.status = status;
    this.timestamp = timestamp;
  }

  public User(Integer id, String name, String email, Gender gender, Status status, String timestamp) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.gender = gender;
    this.status = status;
    this.timestamp = timestamp;
  }

  public Integer getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public Gender getGender() {
    return gender;
  }

  public Status getStatus() {
    return status;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setGender(Gender gender) {
    this.gender = gender;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public static User fromJson(JsonObject json) {
    Integer id = json.getInteger("id");
    String name = json.getString("name");
    String email = json.getString("email");
    Gender gender = Gender.valueOf(json.getString("gender").toUpperCase());
    Status status = Status.valueOf(json.getString("status").toUpperCase());
    String timestamp = json.getString("timestamp");

    return new User(id, name, email, gender, status, timestamp);
  }

}
