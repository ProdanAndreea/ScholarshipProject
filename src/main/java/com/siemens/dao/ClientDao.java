package com.siemens.dao;

import java.util.List;

import com.siemens.config.HibernateUtil;
import com.siemens.model.Client;
import org.hibernate.Session;

public class ClientDao {
    public void saveClient(Client client) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(client);
        session.getTransaction().commit();
        session.close();
    }

    @SuppressWarnings("unchecked")
    public void getClients() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        List<Client> clients = session.createQuery("FROM Client").list();

        for (Client client: clients) {
            System.out.println(client);
        }

        session.getTransaction().commit();
        session.close();
    }
}
