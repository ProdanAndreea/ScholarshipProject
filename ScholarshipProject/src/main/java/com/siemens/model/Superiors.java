package com.siemens.model;

import lombok.AllArgsConstructor;
import lombok.Builder;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "superiors")
@XmlAccessorType(XmlAccessType.FIELD)
@Builder
@AllArgsConstructor
public class Superiors {

    public Superiors() {}

    @XmlElement(name = "superior")
    private List<Superior> superiors = null;

    public List<Superior> getSuperiors() {
        return superiors;
    }

    public void setSuperiors(List<Superior> superiors) {
        this.superiors = superiors;
    }

}
