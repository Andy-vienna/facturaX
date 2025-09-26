package org.andy.code.dataStructure.repositoryProductive;

import org.andy.code.dataStructure.HibernateUtil;
import org.andy.code.dataStructure.entitiyProductive.Lieferschein;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class LieferscheinRepository {

    public List<Lieferschein> findAllByJahr(int jahr) {
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            return session.createQuery(
                    "FROM Lieferschein r WHERE r.jahr = :jahr ORDER BY r.idNummer", Lieferschein.class)
                    .setParameter("jahr", jahr)
                    .getResultList();
        }
    }
    
    public Lieferschein findById(String id){
    	try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            return session.createQuery(
                    "FROM Lieferschein r WHERE r.idNummer = :id", Lieferschein.class)
                    .setParameter("id", id)
                    .getSingleResult();
        }
    }
    
    public Integer findMaxNummerByJahr(int jahr) {
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
        	String prefix = "LS-" + jahr + "-";
        	int prefixLength = ("LS-" + jahr + "-").length();
            Integer maxNummer = session.createQuery(
            		"SELECT CAST(SUBSTRING(r.idNummer, :prefixLen + 1) AS int) " +
                            "FROM Lieferschein r " +
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

    public void save(Lieferschein lieferschein) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            tx = session.beginTransaction();
            session.persist(lieferschein);
            tx.commit();
        }
    }

    public void update(Lieferschein lieferschein) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            tx = session.beginTransaction();
            session.merge(lieferschein);
            tx.commit();
        }
    }
}

