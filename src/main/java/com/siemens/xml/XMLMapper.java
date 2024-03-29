package com.siemens.xml;

import com.siemens.model.*;
import com.sun.org.apache.xpath.internal.operations.Bool;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.io.*;
import java.util.List;
import java.util.Optional;

@XmlSeeAlso({Superior.class, PositionEnum.class})
public class XMLMapper {

    public static <T> void jaxbObjectsToXML(T list, Class<T> concreteClass, String filename) throws JAXBException{
        JAXBContext jaxbContext = JAXBContext.newInstance(concreteClass);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        //Marshal the employees list in console
        jaxbMarshaller.marshal(list, System.out);
        //Marshal the employees list in file
        jaxbMarshaller.marshal(list, new File(filename));
    }

    @SuppressWarnings("unchecked")
    public static <T> T jaxbXMLToObjects(Class<T> concreteClass, String filename) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(concreteClass);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            return (T) jaxbUnmarshaller.unmarshal( new File(filename) );
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Superior getSuperior(String name, String fileName) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Superiors.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

//            ClassLoader cl = this.getClass().getClassLoader();
//            java.io.InputStream in = cl.getResourceAsStream("superiors.xml");

            Superiors sup = (Superiors) jaxbUnmarshaller.unmarshal(new File(fileName));
            Optional optional = sup.getSuperiors().stream().filter(superior -> superior.getName().equals(name)).findFirst();
            if (optional.isPresent()) {
                return (Superior) optional.get();
            }
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     *  check if the boss is available
     */
    public Boolean isAvailable(String bossName, String fileName) {
        return this.getSuperior(bossName, fileName).getAvailable();
    }


    public void setAvailable(String bossName, Boolean available, String fileName) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Superiors.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

//            ClassLoader cl = this.getClass().getClassLoader();
//            java.io.InputStream in = cl.getResourceAsStream("superiors.xml");

            List<Superior> superiorsList = ((Superiors) jaxbUnmarshaller.unmarshal(new File(fileName))).getSuperiors();
            for (Superior superior: superiorsList) {
                if (superior.getName().equals(bossName)) {
                    superior.setAvailable(available);
                }
            }

            Superiors superiors = new Superiors();
            superiors.setSuperiors(superiorsList);

            XMLMapper.<Superiors>jaxbObjectsToXML(superiors, Superiors.class, fileName);

        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
