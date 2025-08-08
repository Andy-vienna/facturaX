package org.andy.code.dataStructure.repositoryProductive;

import org.andy.code.dataStructure.entitiyProductive.Ausgaben;
import org.andy.code.misc.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class AusgabenRepository {

    public List<Ausgaben> findAllByJahr(int jahr) {
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            return session.createQuery(
                    "FROM Ausgaben r WHERE r.jahr = :jahr ORDER BY r.id", Ausgaben.class)
                    .setParameter("jahr", jahr)
                    .getResultList();
        }
    }
    
    public Ausgaben findById(String id){
    	try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            return session.createQuery(
                    "FROM Ausgaben r WHERE r.idNummer = :Id", Ausgaben.class)
                    .setParameter("id", id)
                    .getSingleResult();
        }
    }

    public void save(Ausgaben ausgaben) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            tx = session.beginTransaction();
            session.persist(ausgaben);
            tx.commit();
        }
    }

    public void update(Ausgaben ausgaben) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            tx = session.beginTransaction();
            session.merge(ausgaben);
            tx.commit();
        }
    }
}

