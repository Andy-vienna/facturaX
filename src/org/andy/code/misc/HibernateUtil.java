package org.andy.code.misc;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration cfg = new Configuration().configure();

            // ALLE Entities explizit registrieren!
            cfg.addAnnotatedClass(org.andy.code.entity.User.class);
            cfg.addAnnotatedClass(org.andy.code.entity.Owner.class);
            cfg.addAnnotatedClass(org.andy.code.entity.Kunde.class);
            cfg.addAnnotatedClass(org.andy.code.entity.Artikel.class);
            cfg.addAnnotatedClass(org.andy.code.entity.Bank.class);

            return cfg.buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError("Initial SessionFactory failed " + ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


