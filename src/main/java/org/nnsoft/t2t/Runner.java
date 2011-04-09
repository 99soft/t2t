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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;
import org.nnsoft.t2t.configuration.ConfigurationManager;
import org.nnsoft.t2t.configuration.MigratorConfiguration;
import org.nnsoft.t2t.core.DefaultMigrator;
import org.nnsoft.t2t.core.Migrator;
import org.nnsoft.t2t.core.MigratorException;
import org.openrdf.model.impl.URIImpl;

/**
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 */
public class Runner {

    private static final Logger logger = Logger.getLogger(Runner.class);

    private static Migrator migrator;

    public static void main(String[] args) {
        final String CONFIGURATION = "configuration";
        final String ENTRYPOINT = "entrypoint";

        Options options = new Options();
        options.addOption(CONFIGURATION, true, "XML Configuration file.");
        options.addOption(ENTRYPOINT, true, "URL entrypoint");
        CommandLineParser commandLineParser = new PosixParser();
        CommandLine commandLine = null;
        if(args.length != 4) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Runner", options);
            System.exit(-1);
        }
        /**
         * Parse the configuration file and instantiates all the needed dependencies
         */
        try {
            commandLine = commandLineParser.parse(options, args);
        } catch (ParseException e) {
            logger.error("Error while parsing arguments", e);
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Runner", options);
            System.exit(-1);
        }
        String confFilePath = commandLine.getOptionValue(CONFIGURATION);
        String entryPoint = commandLine.getOptionValue(ENTRYPOINT);
        logger.info("Loading configuration from: '" + confFilePath + "'");
        MigratorConfiguration configuration =
                ConfigurationManager.getInstance(new File(confFilePath)).getConfiguration();
        migrator = new DefaultMigrator(configuration);

        logger.info("Starting migration ... ");
        try {
            migrator.run(new URIImpl(entryPoint));
        } catch (MigratorException e) {
            logger.error("Error during migration process", e);
            System.exit(-1);
        }
    }

}
