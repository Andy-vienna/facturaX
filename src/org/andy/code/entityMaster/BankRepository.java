package org.andy.code.entityMaster;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.andy.code.misc.HibernateUtil;

public class BankRepository {

    public List<Bank> findAll() {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            return session.createQuery("FROM Bank", Bank.class).list();
        }
    }

    public void insert(Bank bank) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(bank);
            tx.commit();
        }
    }

    public void update(Bank bank, int id) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            bank.setId(id);
            session.merge(bank);
            tx.commit();
        }
    }

    public void delete(int id) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            Bank bank = session.find(Bank.class, id);
            if (bank != null) session.remove(bank);
            tx.commit();
        }
    }
}

