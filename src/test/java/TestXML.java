import com.siemens.model.*;
import com.siemens.xml.XMLMapper;

import javax.xml.bind.JAXBException;
import java.util.ArrayList;

public class TestXML {

    public static void main(String[] args)
    {

        /*
         *  Superiors Marshalling
         */
        Superiors superiors = new Superiors();
        superiors.setSuperiors(new ArrayList<>());
        Superior superior_1 = Superior.builder().name("Adrian").email("adrian@gmail.com").PositionEnum(PositionEnum.DIRECT).available(false).build();
        Superior superior_2 = Superior.builder().name("Mihai").email("mihai@gmail.com").PositionEnum(PositionEnum.DEPARTAMENT).available(true).build();
        superiors.getSuperiors().add(superior_1);
        superiors.getSuperiors().add(superior_2);

        try {
            XMLMapper.jaxbObjectsToXML(superiors, Superiors.class, "superiors.xml");
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        /*
         *  Superiors Unmarshalling
         */
        Superiors sups = XMLMapper.jaxbXMLToObjects( Superiors.class, "/superiors.xml");
        for(Superior sup : sups.getSuperiors())
        {
            System.out.println(sup.getName());
            System.out.println(sup.getPositionEnum());
        }

    }

}
