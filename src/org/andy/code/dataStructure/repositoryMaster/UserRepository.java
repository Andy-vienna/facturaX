package org.andy.code.dataStructure.repositoryMaster;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.andy.code.dataStructure.HibernateUtil;
import org.andy.code.dataStructure.entitiyMaster.User;

public class UserRepository {

    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            return session.createQuery("FROM User", User.class).list();
        }
    }

    public void insert(User user) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(user);
            tx.commit();
        }
    }

    public void update(User user) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(user);
            tx.commit();
        }
    }

    public void delete(String id) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            User user = session.find(User.class, id);
            if (user != null) session.remove(user);
            tx.commit();
        }
    }
}

