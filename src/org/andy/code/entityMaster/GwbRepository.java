package org.andy.code.entityMaster;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.andy.code.misc.HibernateUtil;

public class GwbRepository {

    public List<Gwb> findAll() {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            return session.createQuery("FROM Gwb", Gwb.class).list();
        }
    }

    public void insert(Gwb gwb) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(gwb);
            tx.commit();
        }
    }

    public void update(Gwb gwb) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(gwb);
            tx.commit();
        }
    }

}

