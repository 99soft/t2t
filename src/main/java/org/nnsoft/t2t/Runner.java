/*
 *    Copyright 2011 The 99 Software Foundation
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
package org.nnsoft.t2t;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.nnsoft.t2t.configuration.ConfigurationManager;
import org.nnsoft.t2t.configuration.MigratorConfiguration;
import org.nnsoft.t2t.core.DefaultMigrator;
import org.nnsoft.t2t.core.Migrator;
import org.nnsoft.t2t.core.MigratorException;
import org.openrdf.model.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 */
public class Runner {

    public static void main(String[] args) {
        final Logger logger = LoggerFactory.getLogger(Runner.class);

        final String CONFIGURATION = "configuration";
        final String ENTRYPOINT = "entrypoint";

        Options options = new Options();
        options.addOption("h", "help", false, "print this message.");
        options.addOption("v", "version", false, "print the version information and exit.");
        options.addOption("c", CONFIGURATION, true, "XML Configuration file.");
        options.addOption("e", ENTRYPOINT, true, "URL entrypoint");
        CommandLineParser commandLineParser = new PosixParser();
        CommandLine commandLine = null;

        if (args.length == 0) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("t2t", options);
            System.exit(-1);
        }

        /*
         * Parse the configuration file and instantiates all the needed dependencies
         */
        try {
            commandLine = commandLineParser.parse(options, args);
        } catch (ParseException e) {
            logger.error("Error while parsing arguments", e);
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("t2t", options);
            System.exit(-1);
        }

        if (commandLine.hasOption('h') || commandLine.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("t2t", options);
            System.exit(-1);
        }

        if (commandLine.hasOption('v') || commandLine.hasOption("version")) {
            Properties properties = new Properties();
            InputStream input = Runner.class.getClassLoader().getResourceAsStream("META-INF/maven/org.99soft/t2t/pom.properties");

            if (input != null) {
                try {
                    properties.load(input);
                } catch (IOException e) {
                    // ignore, just don't load the properties
                } finally {
                    try {
                        input.close();
                    } catch (IOException e) {
                        // close quietly
                    }
                }
            }

            logger.info("99soft T2T {}", properties.getProperty("version"));
            logger.info("T2T home: {}", System.getenv("T2T_HOME"));
            logger.info("Java version: {}, vendor: {}",
                    System.getProperty("java.version"),
                    System.getProperty("java.vendor"));
            logger.info("Java home: {}", System.getProperty("java.home"));
            logger.info("Default locale: {}, platform encoding: {}");
            logger.info("OS name: \"{}\", version: \"{}\", arch: \"{}\", family: \"{}\"",
                    new Object[] {
                        System.getProperty("os.name"),
                        System.getProperty("os.version"),
                        System.getProperty("os.arch"),
                        System.getProperty("os.family")
                    }
            );

            System.exit(-1);
        }

        String confFilePath = commandLine.getOptionValue(CONFIGURATION);
        String entryPoint = commandLine.getOptionValue(ENTRYPOINT);
        logger.info("Loading configuration from: '" + confFilePath + "'");
        MigratorConfiguration configuration =
                ConfigurationManager.getInstance(new File(confFilePath)).getConfiguration();
        final Migrator migrator = new DefaultMigrator(configuration);

        logger.info("Starting migration...");
        try {
            migrator.run(new URIImpl(entryPoint));
        } catch (MigratorException e) {
            logger.error("Error during migration process", e);
            System.exit(-1);
        } finally {
            logger.info("Migration complete");
        }
    }

}
