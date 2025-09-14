package org.andy.code.dataStructure.repositoryProductive;

import org.andy.code.dataStructure.HibernateUtil;
import org.andy.code.dataStructure.entitiyProductive.Bestellung;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class BestellungRepository {

    public List<Bestellung> findAllByJahr(int jahr) {
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            return session.createQuery(
                    "FROM Bestellung r WHERE r.jahr = :jahr ORDER BY r.idNummer", Bestellung.class)
                    .setParameter("jahr", jahr)
                    .getResultList();
        }
    }
    
    public Bestellung findById(String id){
    	try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            return session.createQuery(
                    "FROM Bestellung r WHERE r.idNummer = :id", Bestellung.class)
                    .setParameter("id", id)
                    .getSingleResult();
        }
    }
    
    public Integer findMaxNummerByJahr(int jahr) {
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
        	String prefix = "BE-" + jahr + "-";
        	int prefixLength = ("BE-" + jahr + "-").length();
            Integer maxNummer = session.createQuery(
            		"SELECT CAST(SUBSTRING(r.idNummer, :prefixLen + 1) AS int) " +
                            "FROM Bestellung r " +
                            "WHERE r.jahr = :jahr AND r.idNummer LIKE :prefix " +
                            "ORDER BY CAST(SUBSTRING(r.idNummer, :prefixLen + 1) AS int) DESC",
                    Integer.class)
            		.setParameter("jahr", jahr)
                    .setParameter("prefix", prefix + "%")
                    .setParameter("prefixLen", prefixLength)
                    .setMaxResults(1)
                    .uniqueResult();
            return (maxNummer == null ? 0 : maxNummer);
        }
    }

    public void save(Bestellung bestellung) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            tx = session.beginTransaction();
            session.persist(bestellung);
            tx.commit();
        }
    }

    public void update(Bestellung bestellung) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            tx = session.beginTransaction();
            session.merge(bestellung);
            tx.commit();
        }
    }
}

