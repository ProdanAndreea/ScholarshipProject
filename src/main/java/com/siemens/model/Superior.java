package com.siemens.model;

import lombok.*;
import javax.xml.bind.annotation.*;


@XmlRootElement(name = "superior")
@XmlAccessorType (XmlAccessType.FIELD)
@XmlType (propOrder={"name","email", "PositionEnum"})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Superior {

    private String name;

    @XmlElement(name = "PositionEnum", required = true)
    @XmlSchemaType(name = "string")
    protected PositionEnum PositionEnum;

    private String email;

}
