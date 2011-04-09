package org.nnsoft.t2t.core;

import org.apache.log4j.Logger;
import org.nnsoft.t2t.configuration.MigratorConfiguration;
import org.nnsoft.t2t.configuration.NamespaceMapping;
import org.nnsoft.t2t.slicers.Slicer;
import org.nnsoft.t2t.slicers.SlicerException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.ContextStatementImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import virtuoso.sesame2.driver.VirtuosoRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Davide Palmisano ( dpalmisano@gmail.ccm )
 */
public final class DefaultMigrator implements Migrator {

    private final static Logger LOGGER = Logger.getLogger(DefaultMigrator.class);

    private Repository source;

    private URI sourceGraph;

    private Repository destination;

    private URI destinationGraph;

    private Set<Rule> rules;

    private MigratorConfiguration configuration;

    public DefaultMigrator(MigratorConfiguration configuration) {
        this.configuration = configuration;
        source = getRepository(configuration.getSourceConnection());
        sourceGraph = configuration.getSourceGraph();
        destination = getRepository(configuration.getDestinationConnection());
        destinationGraph = configuration.getDestinationGraph();
        rules = configuration.getRules();
    }

    public void setSourceRepository(Repository repository, URI graph)
            throws MigratorException {
        source = repository;
        sourceGraph = graph;
    }

    public void setDestinationRepository(Repository repository, URI graph)
            throws MigratorException {
        destination = repository;
        destinationGraph = graph;
    }

    public void addRule(Rule rule) throws MigratorException {
        if(rules == null) {
            rules = new HashSet<Rule>();
        }
        rules.add(rule);
    }

    public MigrationStats run(URI entryPoint) throws MigratorException {
        if (source == null || destination == null || sourceGraph == null || destinationGraph == null) {
            throw new IllegalStateException("DefaultMigrator is not well initialized. " +
                    "Did you call setSourceRepository and setDestinationRepository?");
        }
        // get all the triple from the source graph
        RepositoryConnection sourceRC;
        try {
            sourceRC = getConnection(source);
        } catch (RepositoryException e) {
            throw new MigratorException("");
        }
        LOGGER.info("Connection to: '" + sourceGraph + "' has been correctly established");
        List<Statement> initialStatements;
        try {
            initialStatements =
                    sliceInitialStatements(configuration.getSlicingClass(), entryPoint, sourceGraph ,sourceRC);
        } catch (ClassNotFoundException e) {
            throw new MigratorException("", e);
        } catch (IllegalAccessException e) {
            throw new MigratorException("", e);
        } catch (InstantiationException e) {
            throw new MigratorException("", e);
        } catch (SlicerException e) {
            throw new MigratorException("", e);
        }
        RepositoryConnection destinationRC;
        try {
            destinationRC = getConnection(destination);
        } catch (RepositoryException e) {
            throw new MigratorException("");
        }
        LOGGER.info("Connection to: '" + destinationGraph + "' has been correctly closed");
        int counter = 0;
        final int MAX = configuration.getCommitRate(); // every MAX statements, commit!
        try {
            for(Statement statement : initialStatements) {
                Statement renamedStatement = applyNamespaceMapping(statement);
                ContextStatementImpl cStatement = new ContextStatementImpl(
                        renamedStatement.getSubject(),
                        renamedStatement.getPredicate(),
                        renamedStatement.getObject(),
                        destinationGraph
                );
                for (Rule rule : rules) {
                    try {
                        destinationRC.add(rule.apply(cStatement), destinationGraph);
                        if (counter > MAX) {
                            LOGGER.info("committing ... ");
                            counter = 0;
                            destinationRC.commit();
                            System.gc();
                        }
                    } catch (RuleExecutionException e) {
                        throw new MigratorException("", e);
                    }
                }
                counter++;
                if (!configuration.isActiveFiltering()) {
                    destinationRC.add(cStatement);
                }
            }
            destinationRC.commit();
        } catch (RepositoryException e) {
            throw new MigratorException("");
        } finally {
            try {
                sourceRC.close();
            } catch (RepositoryException e) {
                throw new MigratorException("");
            }
            try {
                destinationRC.close();
            } catch (RepositoryException e) {
                throw new MigratorException("");
            }
        }
        return null;
    }

    private List<Statement> sliceInitialStatements(
            String slicingClass,
            URI entryPoint,
            URI sourceGraph,
            RepositoryConnection repositoryConnection
    ) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SlicerException {
        Class slicerClass = Class.forName(slicingClass);
        Slicer slicer = (Slicer) slicerClass.newInstance();
        return slicer.slice(entryPoint, repositoryConnection, sourceGraph);
    }

    private Statement applyNamespaceMapping(Statement statement) {
        Resource s = statement.getSubject();
        URI p = statement.getPredicate();
        Value o = statement.getObject();
        Set<NamespaceMapping> mappings = configuration.getNamespaceMappings();
        for(NamespaceMapping mapping : mappings) {
            if(s.stringValue().startsWith(mapping.getFrom().stringValue())) {
                String newURI = mapping.getTo().stringValue() +
                        s.stringValue().replace(mapping.getFrom().stringValue(), "");
                s = new URIImpl(newURI);

            }
            if(p.stringValue().startsWith(mapping.getFrom().stringValue())) {
                String newURI = mapping.getTo().stringValue() +
                        p.stringValue().replace(mapping.getFrom().stringValue(), "");
                p = new URIImpl(newURI);

            }
            if( (o instanceof Resource) && (o.stringValue().startsWith(mapping.getFrom().stringValue())) ) {
                String newURI = mapping.getTo().stringValue() +
                        o.stringValue().replace(mapping.getFrom().stringValue(), "");
                o = new URIImpl(newURI);
            }
        }
        return new StatementImpl(s, p, o);
    }

    private RepositoryConnection getConnection(Repository repository) throws RepositoryException {
        return repository.getConnection();
    }

    private Repository getRepository(MigratorConfiguration.ConnectionParameter connectionParameter) {
        return new VirtuosoRepository(
                "jdbc:virtuoso://" + connectionParameter.getHost() + ":" + connectionParameter.getPort(),
                connectionParameter.getUser(),
                connectionParameter.getPassword()
        );
    }
}
