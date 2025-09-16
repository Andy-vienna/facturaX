package org.andy.code.dataStructure.repositoryMaster;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.andy.code.dataStructure.HibernateUtil;
import org.andy.code.dataStructure.entitiyMaster.Lieferant;

public class LieferantRepository {

	public List<Lieferant> findAll() {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            return session.createQuery("FROM Lieferant", Lieferant.class).list();
        }
    }
	
	public Lieferant findById(String id){
    	try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            return session.createQuery(
                    "FROM Lieferant r WHERE r.id = :id", Lieferant.class)
                    .setParameter("id", id)
                    .getSingleResult();
        }
    }
    
    public String findMaxNummer() {
        String sql ="SELECT ISNULL(MAX(TRY_CAST(SUBSTRING(s.id, 1, 10) AS int)), 0) + 1 FROM dbo.tblLieferant s";
    	try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
    		Integer next = ((Number) session.createNativeQuery(sql, Integer.class)
    		    .getSingleResult()).intValue();

    		return String.format("%03d", next);
    	}
    }

    public void insert(Lieferant lieferant) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(lieferant);
            tx.commit();
        }
    }

    public void update(Lieferant lieferant) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(lieferant);
            tx.commit();
        }
    }

    public void delete(String id) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            Lieferant lieferant = session.find(Lieferant.class, id);
            if (lieferant != null) session.remove(lieferant);
            tx.commit();
        }
    }
}

