package org.andy.code.dataStructure.repositoryProductive;

import org.andy.code.dataStructure.HibernateUtil;
import org.andy.code.dataStructure.entitiyProductive.Rechnung;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class RechnungRepository {

    public List<Rechnung> findAllByJahr(int jahr) {
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            return session.createQuery(
                    "FROM Rechnung r WHERE r.jahr = :jahr ORDER BY r.idNummer", Rechnung.class)
                    .setParameter("jahr", jahr)
                    .getResultList();
        }
    }
    
    public Rechnung findById(String id){
    	try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            return session.createQuery(
                    "FROM Rechnung r WHERE r.idNummer = :id", Rechnung.class)
                    .setParameter("id", id)
                    .getSingleResult();
        }
    }
    
    public Integer findMaxNummerByJahr(int jahr) {
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
        	String prefix = "RE-" + jahr + "-";
        	int prefixLength = ("RE-" + jahr + "-").length();
            Integer maxNummer = session.createQuery(
            		"SELECT CAST(SUBSTRING(r.idNummer, :prefixLen + 1) AS int) " +
                            "FROM Rechnung r " +
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

    public void save(Rechnung rechnung) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            tx = session.beginTransaction();
            session.persist(rechnung);
            tx.commit();
        }
    }

    public void update(Rechnung rechnung) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            tx = session.beginTransaction();
            session.merge(rechnung);
            tx.commit();
        }
    }
}

