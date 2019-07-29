package com.siemens.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;


@XmlEnum()
public enum PositionEnum {

    @XmlEnumValue("Sef Direct")
    DIRECT("Sef Direct"),
    @XmlEnumValue("Sef Departament")
    DEPARTAMENT("Sef Departament");

    private final String value;

    PositionEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PositionEnum fromValue(String v) {
        for (PositionEnum c: PositionEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}