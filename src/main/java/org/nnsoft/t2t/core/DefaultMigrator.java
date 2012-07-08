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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import virtuoso.sesame2.driver.VirtuosoRepository;

/**
 * @author Davide Palmisano ( dpalmisano@gmail.ccm )
 */
public final class DefaultMigrator
    implements Migrator
{

    private final static Logger LOGGER = LoggerFactory.getLogger( DefaultMigrator.class );

    private Repository source;

    private URI sourceGraph;

    private Repository destination;

    private URI destinationGraph;

    private Set<Rule> rules;

    private MigratorConfiguration configuration;

    public DefaultMigrator( MigratorConfiguration configuration )
    {
        this.configuration = configuration;
        source = getRepository( configuration.getSourceConnection() );
        sourceGraph = configuration.getSourceGraph();
        destination = getRepository( configuration.getDestinationConnection() );
        destinationGraph = configuration.getDestinationGraph();
        rules = configuration.getRules();
    }

    public void setSourceRepository( Repository repository, URI graph )
        throws MigratorException
    {
        source = repository;
        sourceGraph = graph;
    }

    public void setDestinationRepository( Repository repository, URI graph )
        throws MigratorException
    {
        destination = repository;
        destinationGraph = graph;
    }

    public void addRule( Rule rule )
        throws MigratorException
    {
        if ( rules == null )
        {
            rules = new HashSet<Rule>();
        }
        rules.add( rule );
    }

    public MigrationStats run( URI entryPoint )
        throws MigratorException
    {
        if ( source == null || destination == null || sourceGraph == null || destinationGraph == null )
        {
            throw new IllegalStateException( "DefaultMigrator is not well initialized. "
                + "Did you call setSourceRepository and setDestinationRepository?" );
        }
        // get all the triple from the source graph
        RepositoryConnection sourceRC;
        try
        {
            sourceRC = getConnection( source );
        }
        catch ( RepositoryException e )
        {
            throw new MigratorException( "" );
        }
        LOGGER.info( "Connection to: '{}' has been correctly established", sourceGraph );
        List<Statement> initialStatements;
        try
        {
            initialStatements =
                sliceInitialStatements( configuration.getSlicingClass(), entryPoint, sourceGraph, sourceRC );
        }
        catch ( ClassNotFoundException e )
        {
            throw new MigratorException( "", e );
        }
        catch ( IllegalAccessException e )
        {
            throw new MigratorException( "", e );
        }
        catch ( InstantiationException e )
        {
            throw new MigratorException( "", e );
        }
        catch ( SlicerException e )
        {
            throw new MigratorException( "", e );
        }
        RepositoryConnection destinationRC;
        try
        {
            destinationRC = getConnection( destination );
        }
        catch ( RepositoryException e )
        {
            throw new MigratorException( "" );
        }
        LOGGER.info( "Connection to: '{}' has been correctly closed", destinationGraph );
        int counter = 0;
        final int MAX = configuration.getCommitRate(); // every MAX statements, commit!
        try
        {
            for ( Statement statement : initialStatements )
            {
                Statement renamedStatement = applyNamespaceMapping( statement );
                ContextStatementImpl cStatement =
                    new ContextStatementImpl( renamedStatement.getSubject(), renamedStatement.getPredicate(),
                                              renamedStatement.getObject(), destinationGraph );
                for ( Rule rule : rules )
                {
                    try
                    {
                        destinationRC.add( rule.apply( cStatement ), destinationGraph );
                        if ( counter > MAX )
                        {
                            LOGGER.info( "committing ... " );
                            counter = 0;
                            destinationRC.commit();
                            System.gc();
                        }
                    }
                    catch ( RuleExecutionException e )
                    {
                        throw new MigratorException( "", e );
                    }
                }
                counter++;
                if ( !configuration.isActiveFiltering() )
                {
                    destinationRC.add( cStatement );
                }
            }
            destinationRC.commit();
        }
        catch ( RepositoryException e )
        {
            throw new MigratorException( "" );
        }
        finally
        {
            try
            {
                sourceRC.close();
            }
            catch ( RepositoryException e )
            {
                throw new MigratorException( "" );
            }
            try
            {
                destinationRC.close();
            }
            catch ( RepositoryException e )
            {
                throw new MigratorException( "" );
            }
        }
        return null;
    }

    private List<Statement> sliceInitialStatements( String slicingClass, URI entryPoint, URI sourceGraph,
                                                    RepositoryConnection repositoryConnection )
        throws ClassNotFoundException, IllegalAccessException, InstantiationException, SlicerException
    {
        Class<?> supposedSlicerType = Class.forName( slicingClass );
        if ( !Slicer.class.isAssignableFrom( supposedSlicerType ) )
        {
            throw new SlicerException( slicingClass + " is not assignable to " + Slicer.class.getName() );
        }

        @SuppressWarnings( "unchecked" )
        // checked before
        Class<Slicer> slicerClass = (Class<Slicer>) supposedSlicerType;
        Slicer slicer = slicerClass.newInstance();
        return slicer.slice( entryPoint, repositoryConnection, sourceGraph );
    }

    private Statement applyNamespaceMapping( Statement statement )
    {
        Resource s = statement.getSubject();
        URI p = statement.getPredicate();
        Value o = statement.getObject();
        Set<NamespaceMapping> mappings = configuration.getNamespaceMappings();
        for ( NamespaceMapping mapping : mappings )
        {
            if ( s.stringValue().startsWith( mapping.getFrom().stringValue() ) )
            {
                String newURI =
                    mapping.getTo().stringValue() + s.stringValue().replace( mapping.getFrom().stringValue(), "" );
                s = new URIImpl( newURI );

            }
            if ( p.stringValue().startsWith( mapping.getFrom().stringValue() ) )
            {
                String newURI =
                    mapping.getTo().stringValue() + p.stringValue().replace( mapping.getFrom().stringValue(), "" );
                p = new URIImpl( newURI );

            }
            if ( ( o instanceof Resource ) && ( o.stringValue().startsWith( mapping.getFrom().stringValue() ) ) )
            {
                String newURI =
                    mapping.getTo().stringValue() + o.stringValue().replace( mapping.getFrom().stringValue(), "" );
                o = new URIImpl( newURI );
            }
        }
        return new StatementImpl( s, p, o );
    }

    private RepositoryConnection getConnection( Repository repository )
        throws RepositoryException
    {
        return repository.getConnection();
    }

    private Repository getRepository( MigratorConfiguration.ConnectionParameter connectionParameter )
    {
        return new VirtuosoRepository( "jdbc:virtuoso://" + connectionParameter.getHost() + ":"
            + connectionParameter.getPort(), connectionParameter.getUser(), connectionParameter.getPassword() );
    }

}
