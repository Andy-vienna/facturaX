package org.andy.code.entityProductive;

import org.andy.code.misc.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class AngebotRepository {

    public List<Angebot> findAllByJahr(int jahr) {
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            return session.createQuery(
                    "FROM Angebot r WHERE r.jahr = :jahr ORDER BY r.idNummer", Angebot.class)
                    .setParameter("jahr", jahr)
                    .getResultList();
        }
    }
    
    public Angebot findById(String id){
    	try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            return session.createQuery(
                    "FROM Angebot r WHERE r.idNummer = :id", Angebot.class)
                    .setParameter("id", id)
                    .getSingleResult();
        }
    }

    public void save(Angebot angebot) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            tx = session.beginTransaction();
            session.persist(angebot);
            tx.commit();
        }
    }

    public void update(Angebot angebot) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            tx = session.beginTransaction();
            session.merge(angebot);
            tx.commit();
        }
    }
}

