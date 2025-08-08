package org.andy.code.dataStructure.repositoryMaster;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.andy.code.dataStructure.entitiyMaster.Tax;
import org.andy.code.misc.HibernateUtil;

public class TaxRepository {

    public List<Tax> findAll() {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            return session.createQuery("FROM Tax", Tax.class).list();
        }
    }

    public void insert(Tax tax) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(tax);
            tx.commit();
        }
    }

    public void update(Tax tax) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(tax);
            tx.commit();
        }
    }

}

