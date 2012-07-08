package org.nnsoft.t2t.core;

/*
 *    Copyright 2011-2012 The 99 Software Foundation
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import org.nnsoft.t2t.configuration.ConfigurationManager;
import org.nnsoft.t2t.configuration.MigratorConfiguration;
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
public class DefaultMigratorTestCase
{

    private Migrator migrator;

    @BeforeTest
    public void setUp()
        throws MigratorException
    {
        MigratorConfiguration configuration =
            ConfigurationManager.getInstance( new File( "src/test/resources/configuration.xml" ) ).getConfiguration();
        migrator = new DefaultMigrator( configuration );
    }

    @AfterTest
    public void tearDown()
    {
        migrator = null;
    }

    @Test( enabled = false )
    public void testRun()
        throws MigratorException
    {
        migrator.run( new URIImpl( "http://www.cybion.it/proconsult/fonte_rss/40" ) );
    }

}
