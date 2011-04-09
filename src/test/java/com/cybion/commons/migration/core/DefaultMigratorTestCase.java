package com.cybion.commons.migration.core;

import com.cybion.commons.migration.configuration.ConfigurationManager;
import com.cybion.commons.migration.configuration.MigratorConfiguration;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.Var;
import org.openrdf.repository.Repository;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import virtuoso.sesame2.driver.VirtuosoRepository;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 */
public class DefaultMigratorTestCase {

    private Migrator migrator;

    @BeforeTest
    public void setUp() throws MigratorException {
        MigratorConfiguration configuration =
                ConfigurationManager.getInstance(new File("src/test/resources/configuration.xml"))
                        .getConfiguration();
        migrator = new DefaultMigrator(configuration);
    }

    @AfterTest
    public void tearDown() {
        migrator = null;
    }

    @Test(enabled = false)
    public void testRun() throws MigratorException {
        migrator.run(new URIImpl("http://www.cybion.it/proconsult/fonte_rss/40"));
    }

}
