package org.andy.code.misc;

import org.andy.code.entityMaster.Artikel;
import org.andy.code.entityMaster.Bank;
import org.andy.code.entityMaster.Gwb;
import org.andy.code.entityMaster.Kunde;
import org.andy.code.entityMaster.Owner;
import org.andy.code.entityMaster.Tax;
import org.andy.code.entityMaster.Text;
import org.andy.code.entityMaster.User;
import org.andy.code.entityProductive.Angebot;
import org.andy.code.entityProductive.FileStore;
import org.andy.code.entityProductive.Rechnung;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import java.util.HashMap;
import java.util.Map;


public class HibernateUtil {

    private static SessionFactory sessionFactoryDb1;
    private static SessionFactory sessionFactoryDb2;

    static {
        sessionFactoryDb1 = buildSessionFactoryDb1();
        sessionFactoryDb2 = buildSessionFactoryDb2();
    }
    
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################

    private static SessionFactory buildSessionFactoryDb1() {
    	Map<String, Object> settings = new HashMap<>();
        settings.put("hibernate.connection.driver_class", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        settings.put("hibernate.connection.url", "jdbc:sqlserver://ANDY-LENOVO:1433;databaseName=dbFacturaX-01TEST;encrypt=true;trustServerCertificate=true");
        settings.put("hibernate.connection.username", "sa");
        settings.put("hibernate.connection.password", "P@ssw0rd");
        settings.put("hibernate.show_sql", "true");
        settings.put("hibernate.format_sql", "true");
        settings.put("hibernate.hbm2ddl.auto", "update");
        settings.put("hibernate.transaction.jta.platform", "true");

        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(settings)
                .build();

        MetadataSources metadataSources = new MetadataSources(serviceRegistry);
        metadataSources.addAnnotatedClass(User.class);
        metadataSources.addAnnotatedClass(Owner.class);
        metadataSources.addAnnotatedClass(Kunde.class);
        metadataSources.addAnnotatedClass(Artikel.class);
        metadataSources.addAnnotatedClass(Bank.class);
        metadataSources.addAnnotatedClass(Text.class);
        metadataSources.addAnnotatedClass(Tax.class);
        metadataSources.addAnnotatedClass(Gwb.class);
        
        Metadata metadata = metadataSources.getMetadataBuilder().build();
        return metadata.getSessionFactoryBuilder().build();
    }

    private static SessionFactory buildSessionFactoryDb2() {
    	Map<String, Object> settings = new HashMap<>();
        settings.put("hibernate.connection.driver_class", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        settings.put("hibernate.connection.url", "jdbc:sqlserver://ANDY-LENOVO:1433;databaseName=dbFacturaX-02TEST;encrypt=true;trustServerCertificate=true");
        settings.put("hibernate.connection.username", "sa");
        settings.put("hibernate.connection.password", "P@ssw0rd");
        settings.put("hibernate.show_sql", "true");
        settings.put("hibernate.format_sql", "true");
        settings.put("hibernate.hbm2ddl.auto", "update");
        settings.put("hibernate.transaction.jta.platform", "true");

        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(settings)
                .build();

        MetadataSources metadataSources = new MetadataSources(serviceRegistry);
        metadataSources.addAnnotatedClass(Angebot.class); // nur Entitäten von DB2
        metadataSources.addAnnotatedClass(Rechnung.class);
        metadataSources.addAnnotatedClass(FileStore.class);

        Metadata metadata = metadataSources.getMetadataBuilder().build();
        return metadata.getSessionFactoryBuilder().build();
    }
    
	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################

    public static SessionFactory getSessionFactoryDb1() {
        return sessionFactoryDb1;
    }

    public static SessionFactory getSessionFactoryDb2() {
        return sessionFactoryDb2;
    }
}

