package com.siemens.model;

import lombok.*;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Builder
public class Client {

    private String name;
    private String email;
    private String occupiedPosition;

    public Client() {}

    public Client(String name, String email, String occupiedPosition) {
        this.name = name;
        this.email = email;
        this.occupiedPosition = occupiedPosition;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "Student [name=" + name + ", email=" + email + "]";
    }
}
