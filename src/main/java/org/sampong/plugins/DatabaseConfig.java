package org.sampong.plugins;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.sampong.models.Account;
import org.sampong.models.TransactionRecord;
import org.sampong.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(String.valueOf(DatabaseConfig.class));
    private static final HikariDataSource dataSource;
    private static final SessionFactory sessionFactory;

    private static final String jdbcUrl = "jdbc:postgresql://localhost:5432/baas_project";
    private static final String username = "";
    private static final String password = "";
    private static final String driver = "org.postgresql.Driver";

    static {
        try {
            // Create HikariCP DataSource
            dataSource = createHikariDataSource();

            // Create Hibernate Configuration
            Configuration configuration = new Configuration();

            // Set Hibernate properties programmatically
            Properties hibernateProperties = getHibernateProperties();
            configuration.setProperties(hibernateProperties);

            // Add annotated classes
            configuration.addAnnotatedClass(User.class);
            configuration.addAnnotatedClass(Account.class);
            configuration.addAnnotatedClass(TransactionRecord.class);

            // Build ServiceRegistry
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .applySetting("hibernate.connection.datasource", dataSource)
                    .build();

            // Build SessionFactory
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);

//            System.out.println("Hibernate SessionFactory created successfully with HikariCP");
            logger.info("Start ====>> Hibernate SessionFactory created successfully with HikariCP");

        } catch (Exception e) {
            logger.error("Failed to create SessionFactory: {}", e.getMessage());
//            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    private static Properties getHibernateProperties() {
        Properties properties = new Properties();

//        // Connection provider
//        properties.setProperty("hibernate.connection.provider_class",
//                "com.zaxxer.hikari.hibernate.HikariConnectionProvider");

        // Database dialect
//        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        // Schema management
        properties.setProperty("hibernate.hbm2ddl.auto", "update");

        // SQL logging and formatting
        properties.setProperty("hibernate.show_sql", "false");
        properties.setProperty("hibernate.format_sql", "true");
        properties.setProperty("hibernate.use_sql_comments", "true");

        // Performance settings
        properties.setProperty("hibernate.jdbc.batch_size", "25");
        properties.setProperty("hibernate.order_inserts", "true");
        properties.setProperty("hibernate.order_updates", "true");
        properties.setProperty("hibernate.jdbc.batch_versioned_data", "true");

        // Cache settings
        properties.setProperty("hibernate.cache.use_second_level_cache", "false");
        properties.setProperty("hibernate.cache.use_query_cache", "false");

        // Statistics
        properties.setProperty("hibernate.generate_statistics", "true");

        // Connection handling
//        properties.setProperty("hibernate.connection.autocommit", "false");
        properties.setProperty("hibernate.current_session_context_class", "thread");

        // Configure hibernate byte code provider
        properties.setProperty("hibernate.bytecode.provider", "bytebuddy");

        return properties;
    }

    private static HikariDataSource createHikariDataSource() {
        HikariConfig config = new HikariConfig();

        // Database config
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driver);


        // Pool settings
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setLeakDetectionThreshold(60000);

        // PostgreSQL optimizations
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("ApplicationName", "HibernateHikariCPBaasProject");

        config.setPoolName("HikariCP-PostgreSQL");

        HikariDataSource ds = new HikariDataSource(config);
        logger.info("Start ====>> HikariCP DataSource created successfully");
        return ds;
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static HikariDataSource getDataSource() {
        return dataSource;
    }


    public static void printConnectionPoolStats() {
        if (dataSource != null) {
            System.out.println("=== HikariCP Pool Statistics ===");
            System.out.println("Active connections: " + dataSource.getHikariPoolMXBean().getActiveConnections());
            System.out.println("Idle connections: " + dataSource.getHikariPoolMXBean().getIdleConnections());
            System.out.println("Total connections: " + dataSource.getHikariPoolMXBean().getTotalConnections());
            System.out.println("Threads awaiting connection: " + dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection());
        }
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
        if (dataSource != null) {
            dataSource.close();
        }
        logger.info(" ====>> Session factory and HikariCP DataSource closed");
    }
}