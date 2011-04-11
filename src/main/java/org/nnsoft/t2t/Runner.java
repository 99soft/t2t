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

        if (args.length == 0) {
            printHelp(options);
        }

        /*
         * Parse the configuration file and instantiates all the needed dependencies
         */
        CommandLine commandLine = null;
        try {
            commandLine = commandLineParser.parse(options, args);
        } catch (ParseException e) {
            System.err.printf("Error while parsing arguments: %s%n", e.getMessage());
            printHelp(options);
        }

        if (commandLine.hasOption('h') || commandLine.hasOption("help")) {
            printHelp(options);
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

            System.out.printf("99soft T2T %s (%s)%n",
                    properties.getProperty("version"),
                    properties.getProperty("build"));
            System.out.printf("Java version: %s, vendor: %s%n",
                    System.getProperty("java.version"),
                    System.getProperty("java.vendor"));
            System.out.printf("Java home: %s%n", System.getProperty("java.home"));
            System.out.printf("Default locale: %s_%s, platform encoding: %s%n",
                    System.getProperty("user.language"),
                    System.getProperty("user.country"),
                    System.getProperty("sun.jnu.encoding"));
            System.out.printf("OS name: \"%s\", version: \"%s\", arch: \"%s\"%n",
                    System.getProperty("os.name"),
                    System.getProperty("os.version"),
                    System.getProperty("os.arch"));

            System.exit(-1);
        }

        String confFilePath = null;
        if (commandLine.hasOption('c')) {
            confFilePath = commandLine.getOptionValue('c');
        } else if (commandLine.hasOption(CONFIGURATION)) {
            confFilePath = commandLine.getOptionValue(CONFIGURATION);
        }

        if (confFilePath == null) {
            System.err.println("'-c' xor '--configuration' parameter has to be specified");
            printHelp(options);
        }

        String entryPoint = null;
        if (commandLine.hasOption('e')) {
            entryPoint = commandLine.getOptionValue('e');
        } else if (commandLine.hasOption(ENTRYPOINT)) {
            entryPoint = commandLine.getOptionValue(ENTRYPOINT);
        }

        if (entryPoint == null) {
            System.err.println("'-e' xor '--entrypoint' parameter has to be specified");
            printHelp(options);
        }

        logger.info("Loading configuration from: '{}'", confFilePath);
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

    private static final void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("t2t", options);
        System.exit(-1);
    }

}
