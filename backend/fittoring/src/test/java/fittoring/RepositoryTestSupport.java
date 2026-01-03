package fittoring;

import fittoring.application.mentoring.repository.MentoringPaginationHelper;
import fittoring.config.JpaConfiguration;
import fittoring.config.QueryDslConfig;
import fittoring.util.DbCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({DbCleaner.class, JpaConfiguration.class, QueryDslConfig.class, MentoringPaginationHelper.class})
@DataJpaTest
public abstract class RepositoryTestSupport {

    @Autowired
    protected TestEntityManager em;
    @Autowired
    private DbCleaner dbCleaner;

    @BeforeEach
    void setUp() {
        dbCleaner.clean();
    }
}
