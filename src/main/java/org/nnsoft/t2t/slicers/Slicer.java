package org.nnsoft.t2t.slicers;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryConnection;

import java.util.List;

/**
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 */
public interface Slicer {

    public List<Statement> slice(
            URI entrypoint,
            RepositoryConnection repositoryConnection, 
            URI sourceGraph)
        throws SlicerException;

}
