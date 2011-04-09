package org.nnsoft.t2t.core;

import org.openrdf.model.URI;
import org.openrdf.repository.Repository;

/**
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 */
public interface Migrator {

    public void setSourceRepository(Repository repository, URI graph)
            throws MigratorException;

    public void setDestinationRepository(Repository repository, URI graph) 
            throws MigratorException;

    public void addRule(Rule rule) throws MigratorException;
    
    public MigrationStats run(URI entrypoint) throws MigratorException;

}
