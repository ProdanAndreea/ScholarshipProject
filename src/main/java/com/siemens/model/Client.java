package com.siemens.model;

import lombok.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "client")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"name","email", "occupiedPosition"})
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
        return "Client [name=" + name + ", email=" + email + "]";
    }
}
