package com.siemens.model;

import javax.xml.bind.annotation.*;


@XmlEnum()
public enum PositionEnum {

    @XmlEnumValue("Team Leader")
    DIRECT("Team Leader"),
    @XmlEnumValue("Department Leader")
    DEPARTAMENT("Department Leader");

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