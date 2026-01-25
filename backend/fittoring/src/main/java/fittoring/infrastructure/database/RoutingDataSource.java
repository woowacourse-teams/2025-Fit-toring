package fittoring.infrastructure.database;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
public class RoutingDataSource extends AbstractRoutingDataSource {

    public static RoutingDataSource from(Map<Object, Object> dataSources) {
        RoutingDataSource routingDataSource = new RoutingDataSource();
        routingDataSource.setDefaultTargetDataSource(dataSources.get(DataSourceType.SOURCE));
        routingDataSource.setTargetDataSources(dataSources);
        routingDataSource.afterPropertiesSet();
        return routingDataSource;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        if (readOnly) {
            log.debug("readOnly = true, request to source (temporary override)");
            return DataSourceType.SOURCE;
        }

        log.debug("readOnly = false, request to source");
        return DataSourceType.SOURCE;
    }
}
