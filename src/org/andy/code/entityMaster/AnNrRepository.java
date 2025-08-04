package org.andy.code.entityMaster;

import java.util.List;

import org.andy.code.misc.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class AnNrRepository {
	
	public List<AnNr> findAll() {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            return session.createQuery("FROM AnNr", AnNr.class).list();
        }
    }
	
	public void insert(AnNr anNr) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(anNr);
            tx.commit();
        }
    }

    public void update(AnNr anNr) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(anNr);
            tx.commit();
        }
    }

}
