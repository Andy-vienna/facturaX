package org.andy.code.entity;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.andy.code.misc.HibernateUtil;

public class KundeRepository {

    public List<Kunde> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Kunde", Kunde.class).list();
        }
    }

    public void insert(Kunde kunde) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(kunde);
            tx.commit();
        }
    }

    public void update(Kunde kunde) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(kunde);
            tx.commit();
        }
    }

    public void delete(String id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Kunde kunde = session.find(Kunde.class, id);
            if (kunde != null) session.remove(kunde);
            tx.commit();
        }
    }
}

