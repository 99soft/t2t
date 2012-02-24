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
package org.nnsoft.t2t.core;

import org.nnsoft.t2t.configuration.ConfigurationManager;
import org.nnsoft.t2t.configuration.MigratorConfiguration;
import org.openrdf.model.impl.URIImpl;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;

/**
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 */
public class ConfigurationTestCase {

    private ConfigurationManager configurationManager;

    @BeforeTest
    public void setUp() {
        configurationManager = ConfigurationManager.getInstance(new File("src/test/resources/configuration.xml"));
    }

    @AfterTest
    public void tearDown() {
        configurationManager = null;

    }

    @Test
    public void testConfiguraion() {
        MigratorConfiguration migratorConfiguration = configurationManager.getConfiguration();
        Assert.assertNotNull(migratorConfiguration);
        Assert.assertEquals(migratorConfiguration.getCommitRate(), 500);
        Assert.assertEquals(migratorConfiguration.isActiveFiltering(), false);
        Assert.assertEquals(migratorConfiguration.getSourceGraph(), new URIImpl("http://www.cybion.it/proconsult/url"));
        Assert.assertEquals(migratorConfiguration.getDestinationGraph(), new URIImpl("http://collective.com/resources/web"));
        Assert.assertEquals(migratorConfiguration.getSourceConnection(), new MigratorConfiguration.ConnectionParameter(
                "cibionte.dyndns.org",
                1111,
                "dba",
                "cybiondba")
        );
        Assert.assertEquals(migratorConfiguration.getDestinationConnection(), new MigratorConfiguration.ConnectionParameter(
                "cibionte.dyndns.org",
                1111,
                "dba",
                "cybiondba")
        );
        Assert.assertTrue(migratorConfiguration.getRules().size() == 1);
        Assert.assertTrue(migratorConfiguration.getNamespaceMappings().size() == 1);
    }

}
