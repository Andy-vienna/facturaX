package org.andy.code.dataStructure.repositoryProductive;

import org.andy.code.dataStructure.entitiyProductive.SVSteuer;
import org.andy.code.misc.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class SVSteuerRepository {

    public List<SVSteuer> findAllByJahr(int jahr) {
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            return session.createQuery(
                    "FROM SVSteuer r WHERE r.jahr = :jahr ORDER BY r.id", SVSteuer.class)
                    .setParameter("jahr", jahr)
                    .getResultList();
        }
    }
    
    public SVSteuer findById(String id){
    	try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            return session.createQuery(
                    "FROM SVSteuer r WHERE r.idNummer = :Id", SVSteuer.class)
                    .setParameter("id", id)
                    .getSingleResult();
        }
    }

    public void save(SVSteuer svsteuer) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            tx = session.beginTransaction();
            session.persist(svsteuer);
            tx.commit();
        }
    }

    public void update(SVSteuer svsteuer) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            tx = session.beginTransaction();
            session.merge(svsteuer);
            tx.commit();
        }
    }
}

