package org.andy.code.dataStructure.repositoryMaster;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.andy.code.dataStructure.HibernateUtil;
import org.andy.code.dataStructure.entitiyMaster.Artikel;

public class ArtikelRepository {

    public List<Artikel> findAll() {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            return session.createQuery("FROM Artikel", Artikel.class).list();
        }
    }

    public void insert(Artikel artikel) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(artikel);
            tx.commit();
        }
    }

    public void update(Artikel artikel) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(artikel);
            tx.commit();
        }
    }

    public void delete(String id) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            Artikel artikel = session.find(Artikel.class, id);
            if (artikel != null) session.remove(artikel);
            tx.commit();
        }
    }
}

