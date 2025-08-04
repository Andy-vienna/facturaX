package org.andy.code.entityMaster;

import java.util.List;

import org.andy.code.misc.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ReNrRepository {
	
	public List<ReNr> findAll() {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            return session.createQuery("FROM ReNr", ReNr.class).list();
        }
    }
	
	public void insert(ReNr reNr) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(reNr);
            tx.commit();
        }
    }

    public void update(ReNr reNr) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(reNr);
            tx.commit();
        }
    }

}
