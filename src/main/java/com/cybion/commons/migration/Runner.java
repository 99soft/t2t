package com.cybion.commons.migration;

import com.cybion.commons.migration.configuration.ConfigurationManager;
import com.cybion.commons.migration.configuration.MigratorConfiguration;
import com.cybion.commons.migration.core.DefaultMigrator;
import com.cybion.commons.migration.core.Migrator;
import com.cybion.commons.migration.core.MigratorException;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.openrdf.model.impl.URIImpl;

import java.io.File;

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
