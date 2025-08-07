package org.andy.code.entityProductive;

import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.Optional;

import static org.andy.code.misc.HibernateUtil.getSessionFactoryDb2;

public class FileStoreRepository {
	
	public Optional<FileStore> findById(String idNummer) {
        try (Session session = getSessionFactoryDb2().openSession()) {
            FileStore result = session.find(FileStore.class, idNummer);
            return Optional.ofNullable(result);
        }
    }

    public void save(FileStore file) {
        Transaction tx = null;
        try (Session session = getSessionFactoryDb2().openSession()) {
            tx = session.beginTransaction();
            session.persist(file);  // bei Neuanlage
            tx.commit();
        }
    }

    public void update(FileStore file) {
        Transaction tx = null;
        try (Session session = getSessionFactoryDb2().openSession()) {
            tx = session.beginTransaction();
            session.merge(file);  // ersetzt vorhandene Daten
            tx.commit();
        }
    }

    public void delete(String idNummer) {
        Transaction tx = null;
        try (Session session = getSessionFactoryDb2().openSession()) {
            tx = session.beginTransaction();
            FileStore file = session.find(FileStore.class, idNummer);
            if (file != null) {
                session.remove(file);
            }
            tx.commit();
        }
    }
}

