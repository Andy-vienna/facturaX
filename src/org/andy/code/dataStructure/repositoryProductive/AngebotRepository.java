package org.andy.code.dataStructure.repositoryProductive;

import org.andy.code.dataStructure.HibernateUtil;
import org.andy.code.dataStructure.entitiyProductive.Angebot;
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
    
    public Integer findMaxNummerByJahr(int jahr) {
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
        	String prefix = "AN-" + jahr + "-";
        	int prefixLength = ("AN-" + jahr + "-").length();
            return session.createQuery(
            		"SELECT CAST(SUBSTRING(r.idNummer, :prefixLen + 1) AS int) " +
                            "FROM Angebot r " +
                            "WHERE r.jahr = :jahr AND r.idNummer LIKE :prefix " +
                            "ORDER BY CAST(SUBSTRING(r.idNummer, :prefixLen + 1) AS int) DESC",
                    Integer.class)
            		.setParameter("jahr", jahr)
                    .setParameter("prefix", prefix + "%")
                    .setParameter("prefixLen", prefixLength)
                    .setMaxResults(1)
                    .uniqueResult();
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

