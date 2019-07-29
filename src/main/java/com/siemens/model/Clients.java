package com.siemens.model;

import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "clients")
@XmlAccessorType(XmlAccessType.FIELD)
@Builder
@AllArgsConstructor
public class Clients {

    public Clients() {}

    @XmlElement(name = "client")
    private List<Client> clients = null;

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }
}