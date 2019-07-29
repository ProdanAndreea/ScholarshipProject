package com.siemens.xml;

import com.siemens.model.Client;
import com.siemens.model.PositionEnum;
import com.siemens.model.Superior;
import com.siemens.model.Superiors;

import javax.xml.bind.JAXBException;
import java.util.ArrayList;

public class TestXML {

    public static void main(String[] args)
    {
        /*
         * Superiors Marshalling
         */
        Superiors superiors = new Superiors();
        superiors.setSuperiors(new ArrayList<>());
        Superior superior_1 = Superior.builder().name("Mark").email("mark@gmail.com").PositionEnum(PositionEnum.DEPARTAMENT).build();
        Superior superior_2 = Superior.builder().name("Anna").email("anna@gmail.com").PositionEnum(PositionEnum.DIRECT).build();
        Superior superior_3 = Superior.builder().name("Red").email("red@gmail.com").PositionEnum(PositionEnum.DEPARTAMENT).build();
        Superior superior_4 = Superior.builder().name("Vlad").email("vlad@gmail.com").PositionEnum(PositionEnum.DIRECT).build();
        superiors.getSuperiors().add(superior_1);
        superiors.getSuperiors().add(superior_2);
        superiors.getSuperiors().add(superior_3);
        superiors.getSuperiors().add(superior_4);

        XMLMapper<Superiors> xmlMapperSuperiors = new XMLMapper<>();
        try {
            xmlMapperSuperiors.jaxbObjectsToXML(superiors, Superiors.class, "superiors.xml");
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        /*
         *  Superiors Unmarshalling
         */
        XMLMapper<Superiors> xmlMapperClient = new XMLMapper<>();
        Superiors sups = xmlMapperClient.jaxbXMLToObjects( Superiors.class, "superiors.xml");
        for(Superior sup : sups.getSuperiors())
        {
            System.out.println(sup.getName());
            System.out.println(sup.getPositionEnum());
        }
    }

}
