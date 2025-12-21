package com.devision.job_manager_auth.config.sharding;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * This class reads the shard configurations, creates connection pools for each database, and sets up Spring's JPA infrastructure to use the routing datasource
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.devision.job_manager_auth.repository",
        entityManagerFactoryRef = "shardingEntityManagerFactory",
        transactionManagerRef = "shardingTransactionManager"
)
public class ShardingDataSourceConfig {
    private final ShardingProperties shardingProperties;

    /**
     * Creates the routing datasource that delegates to shard-specific datasources
     */
    @Bean
    @Primary
    public DataSource dataSource() {
        ShardRoutingDataSource routingDataSource = new ShardRoutingDataSource();

        Map<Object, Object> targetDataSources = new HashMap<>();

        shardingProperties.getShards().forEach((shardKey, shardProps) -> {
            log.info("Configuring datasource for shard: {}", shardKey);
            HikariDataSource ds = createHikariDataSource(shardKey, shardProps);
            targetDataSources.put(shardKey, ds);
        });

        routingDataSource.setTargetDataSources(targetDataSources);

        // Set the default datasource
        String defaultShardKey = shardingProperties.getDefaultShard();
        DataSource defaultDataSource = (DataSource) targetDataSources.get(defaultShardKey);

        if (defaultDataSource == null) {
            throw new IllegalStateException(
                    "Default shard '" + defaultShardKey + "' not found in configured shards. " +
                            "Available shards: " + targetDataSources.keySet()
            );
        }

        routingDataSource.setDefaultTargetDataSource(defaultDataSource);
        routingDataSource.afterPropertiesSet();

        log.info("Sharding datasource configured with {} shards. Default shard: {}",
                targetDataSources.size(), defaultShardKey);

        return routingDataSource;
    }

    /**
     * Creates a HikariCP datasource for a specific shard
     */
    private HikariDataSource createHikariDataSource(String shardKey,
                                                    ShardingProperties.ShardProperties props) {
        HikariDataSource dataSource = new HikariDataSource();

        dataSource.setPoolName("HikariPool-" + shardKey);
        dataSource.setJdbcUrl(props.getUrl());
        dataSource.setUsername(props.getUsername());
        dataSource.setPassword(props.getPassword());
        dataSource.setDriverClassName(props.getDriverClassName());

        // Connection pool settings
        dataSource.setMaximumPoolSize(props.getMaximumPoolSize());
        dataSource.setMinimumIdle(props.getMinimumIdle());
        dataSource.setConnectionTimeout(props.getConnectionTimeout());
        dataSource.setIdleTimeout(props.getIdleTimeout());
        dataSource.setMaxLifetime(props.getMaxLifetime());

        return dataSource;
    }

    /**
     * Creates the EntityManagerFactory for JPA using the routing datasource
     */
    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean shardingEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("dataSource") DataSource dataSource) {

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "validate");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.format_sql", "true");

        return builder
                .dataSource(dataSource)
                .packages("com.devision.job_manager_auth.entity")
                .persistenceUnit("sharding")
                .properties(properties)
                .build();
    }

    /**
     * Creates the transaction manager for the sharding datasource
     */
    @Bean
    @Primary
    public PlatformTransactionManager shardingTransactionManager(
            @Qualifier("shardingEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
