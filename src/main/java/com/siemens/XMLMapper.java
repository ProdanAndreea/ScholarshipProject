package com.siemens;

import com.siemens.model.Client;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class XMLMapper {


    public static void main(String[] args)
    {
        //Java object. We will convert it to XML.
//        //Client client = new Client("Dexter", "dexter@gmail.com"); //Client.builder().name("Dexter").email("dexter@gmail.com").build();
//
//        //Method which uses JAXB to convert object to XML
//        jaxbObjectToXML(client);
//
//        // Unmarshalling
//        client = jaxbXMLToObject("emails.xml");
//        System.out.println(client);
    }

    private static Client jaxbXMLToObject(String filename) {
        try {
            File file = new File(filename);
            JAXBContext jaxbContext = JAXBContext.newInstance(Client.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (Client) jaxbUnmarshaller.unmarshal(file);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return new Client();
    }

    private static void jaxbObjectToXML(Client client)
    {
        try
        {
            File file = new File("emails.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(Client.class);

            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(client, file);
            jaxbMarshaller.marshal(client, System.out);
        }
        catch (JAXBException e)
        {
            e.printStackTrace();
        }
    }
}
