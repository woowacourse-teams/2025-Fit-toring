package fittoring.application.community.dummy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "dummy.admin-api")
public class DummyAdminApiProperties {

    private final boolean enabled;
    private final String scenariosBasePath;
    private final String guestPasswordHash;
}
