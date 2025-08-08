package org.andy.code.dataStructure.repositoryProductive;

import org.andy.code.dataStructure.entitiyProductive.Einkauf;
import org.andy.code.misc.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class EinkaufRepository {

    public List<Einkauf> findAllByJahr(int jahr) {
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            return session.createQuery(
                    "FROM Einkauf r WHERE r.jahr = :jahr ORDER BY r.id", Einkauf.class)
                    .setParameter("jahr", jahr)
                    .getResultList();
        }
    }
    
    public Einkauf findById(String id){
    	try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            return session.createQuery(
                    "FROM Einkauf r WHERE r.idNummer = :Id", Einkauf.class)
                    .setParameter("id", id)
                    .getSingleResult();
        }
    }

    public void save(Einkauf einkauf) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            tx = session.beginTransaction();
            session.persist(einkauf);
            tx.commit();
        }
    }

    public void update(Einkauf einkauf) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            tx = session.beginTransaction();
            session.merge(einkauf);
            tx.commit();
        }
    }
}

