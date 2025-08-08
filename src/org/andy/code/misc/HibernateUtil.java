package org.andy.code.misc;

import org.andy.code.dataStructure.entitiyMaster.Artikel;
import org.andy.code.dataStructure.entitiyMaster.Bank;
import org.andy.code.dataStructure.entitiyMaster.Gwb;
import org.andy.code.dataStructure.entitiyMaster.Kunde;
import org.andy.code.dataStructure.entitiyMaster.Owner;
import org.andy.code.dataStructure.entitiyMaster.Tax;
import org.andy.code.dataStructure.entitiyMaster.Text;
import org.andy.code.dataStructure.entitiyMaster.User;
import org.andy.code.dataStructure.entitiyProductive.Angebot;
import org.andy.code.dataStructure.entitiyProductive.Ausgaben;
import org.andy.code.dataStructure.entitiyProductive.Einkauf;
import org.andy.code.dataStructure.entitiyProductive.FileStore;
import org.andy.code.dataStructure.entitiyProductive.Rechnung;
import org.andy.code.dataStructure.entitiyProductive.SVSteuer;
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

    private static SessionFactory buildSessionFactoryDb1() {
        Map<String, Object> settings = new HashMap<>();

        // ---- HikariCP aktivieren ----
        settings.put("hibernate.connection.provider_class",
                "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");

        // Dialekt (optional, aber bei SQL Server sinnvoll)
        //settings.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");

        // ---- HikariCP-Einstellungen (DB1) ----
        settings.put("hibernate.hikari.jdbcUrl",
                "jdbc:sqlserver://ANDY-LENOVO:1433;databaseName=dbFacturaX-01TEST;encrypt=true;trustServerCertificate=true");
        settings.put("hibernate.hikari.username", "sa");
        settings.put("hibernate.hikari.password", "P@ssw0rd");

        // Pooling-Parameter – an deine Last anpassen
        settings.put("hibernate.hikari.maximumPoolSize", "10");
        settings.put("hibernate.hikari.minimumIdle", "2");
        settings.put("hibernate.hikari.idleTimeout", "300000");   // 5 min
        settings.put("hibernate.hikari.maxLifetime", "1800000");  // 30 min
        settings.put("hibernate.hikari.connectionTimeout", "30000");
        settings.put("hibernate.hikari.poolName", "FX-DB1");

        // Hibernate
        settings.put("hibernate.show_sql", "false");
        settings.put("hibernate.format_sql", "false");
        settings.put("hibernate.hbm2ddl.auto", "none"); // wie bisher

        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(settings)
                .build();

        MetadataSources sources = new MetadataSources(serviceRegistry);
        sources.addAnnotatedClass(User.class);
        sources.addAnnotatedClass(Owner.class);
        sources.addAnnotatedClass(Kunde.class);
        sources.addAnnotatedClass(Artikel.class);
        sources.addAnnotatedClass(Bank.class);
        sources.addAnnotatedClass(Text.class);
        sources.addAnnotatedClass(Tax.class);
        sources.addAnnotatedClass(Gwb.class);

        Metadata metadata = sources.getMetadataBuilder().build();
        return metadata.getSessionFactoryBuilder().build();
    }

    private static SessionFactory buildSessionFactoryDb2() {
        Map<String, Object> settings = new HashMap<>();

        // ---- HikariCP aktivieren ----
        settings.put("hibernate.connection.provider_class",
                "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");

        // Dialekt (optional, aber bei SQL Server sinnvoll)
        //settings.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");

        // ---- HikariCP-Einstellungen (DB2) ----
        settings.put("hibernate.hikari.jdbcUrl",
                "jdbc:sqlserver://ANDY-LENOVO:1433;databaseName=dbFacturaX-02TEST;encrypt=true;trustServerCertificate=true");
        settings.put("hibernate.hikari.username", "sa");
        settings.put("hibernate.hikari.password", "P@ssw0rd");

        settings.put("hibernate.hikari.maximumPoolSize", "10");
        settings.put("hibernate.hikari.minimumIdle", "2");
        settings.put("hibernate.hikari.idleTimeout", "300000");
        settings.put("hibernate.hikari.maxLifetime", "1800000");
        settings.put("hibernate.hikari.connectionTimeout", "30000");
        settings.put("hibernate.hikari.poolName", "FX-DB2");

        // Hibernate
        settings.put("hibernate.show_sql", "true");
        settings.put("hibernate.format_sql", "true");
        settings.put("hibernate.hbm2ddl.auto", "update");

        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(settings)
                .build();

        MetadataSources sources = new MetadataSources(serviceRegistry);
        sources.addAnnotatedClass(Angebot.class);
        sources.addAnnotatedClass(Rechnung.class);
        sources.addAnnotatedClass(FileStore.class);
        sources.addAnnotatedClass(Einkauf.class);
        sources.addAnnotatedClass(Ausgaben.class);
        sources.addAnnotatedClass(SVSteuer.class);

        Metadata metadata = sources.getMetadataBuilder().build();
        return metadata.getSessionFactoryBuilder().build();
    }

    public static SessionFactory getSessionFactoryDb1() { return sessionFactoryDb1; }
    public static SessionFactory getSessionFactoryDb2() { return sessionFactoryDb2; }
}
