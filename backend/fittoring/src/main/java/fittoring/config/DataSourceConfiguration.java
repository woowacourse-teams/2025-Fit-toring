package fittoring.config;

import static fittoring.infrastructure.database.DataSourceType.Key.REPLICA_NAME;
import static fittoring.infrastructure.database.DataSourceType.Key.ROUTING_NAME;
import static fittoring.infrastructure.database.DataSourceType.Key.SOURCE_NAME;

import com.zaxxer.hikari.HikariDataSource;
import fittoring.infrastructure.database.DataSourceType;
import fittoring.infrastructure.database.RoutingDataSource;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

@Profile({"dev", "local"})
@Configuration
public class DataSourceConfiguration {

    @Bean
    @Qualifier(SOURCE_NAME)
    @ConfigurationProperties(prefix = "spring.datasources.writer")
    public HikariDataSource sourceDataSource() {
        return DataSourceBuilder.create()
                .type(com.zaxxer.hikari.HikariDataSource.class)
                .build();
    }

    @Bean
    @Qualifier(REPLICA_NAME)
    @ConfigurationProperties(prefix = "spring.datasources.reader")
    public HikariDataSource replicaDataSource() {
        return DataSourceBuilder.create()
                .type(com.zaxxer.hikari.HikariDataSource.class)
                .build();
    }

    @Bean
    @Qualifier(ROUTING_NAME)
    public DataSource routingDataSource(
            @Qualifier(SOURCE_NAME) DataSource sourceDataSource,
            @Qualifier(REPLICA_NAME) DataSource replicaDataSource
    ) {
        return RoutingDataSource.from(Map.of(
                DataSourceType.SOURCE, sourceDataSource,
                DataSourceType.REPLICA, replicaDataSource
        ));
    }

    @Bean
    @Primary
    public DataSource dataSource(
            @Qualifier(ROUTING_NAME) DataSource routingDataSource
    ) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }
}
