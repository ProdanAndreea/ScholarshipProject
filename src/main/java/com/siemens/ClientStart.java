package com.siemens;

import com.siemens.config.HibernateUtil;
import com.siemens.controller.ClientController;
import com.siemens.model.Client;
import com.siemens.dao.ClientDao;
import org.hibernate.Session;

/**
 * @Author: Siemens CT Cluj-Napoca, Romania
 * @Since: Jul 25, 2019
 * @Description:
 *   Starting point for the Client application.
 */
public class ClientStart {

    private ClientStart() {
    }

    public static void main(String[] args) {
        new ClientController();

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        /*
        // Check database version
        String sql = "select version()";
        String result = (String) session.createNativeQuery(sql).getSingleResult();
        System.out.println(result);
        session.getTransaction().commit();
        session.close();
        */

        ClientDao cd = new ClientDao();

        cd.getClients();
    }
}
