package org.andy.code.dataStructure.repositoryMaster;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.andy.code.dataStructure.HibernateUtil;
import org.andy.code.dataStructure.entitiyMaster.Kunde;

public class KundeRepository {

    public List<Kunde> findAll() {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            return session.createQuery("FROM Kunde", Kunde.class).list();
        }
    }
    
    public String findMaxNummer() {
        String sql ="SELECT ISNULL(MAX(TRY_CAST(SUBSTRING(s.id, 1, 10) AS int)), 0) + 1 FROM dbo.tblKunde s";
    	try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
    		Integer next = ((Number) session.createNativeQuery(sql, Integer.class)
    		    .getSingleResult()).intValue();

    		return String.format("%03d", next);
    	}
    }

    public void insert(Kunde kunde) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(kunde);
            tx.commit();
        }
    }

    public void update(Kunde kunde) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(kunde);
            tx.commit();
        }
    }

    public void delete(String id) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            Kunde kunde = session.find(Kunde.class, id);
            if (kunde != null) session.remove(kunde);
            tx.commit();
        }
    }
}

