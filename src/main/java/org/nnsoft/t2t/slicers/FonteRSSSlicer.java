package org.nnsoft.t2t.slicers;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 */
public class FonteRSSSlicer implements Slicer {

    private final static URI hasFonteRSS = new URIImpl("http://www.cybion.it/proconsult/url#hasFonteRss");

    public List<Statement> slice(
            URI entrypoint,
            RepositoryConnection repositoryConnection,
            URI sourceGraph
    ) throws SlicerException {
        List<Statement> fLStatements, fonteOutcomingStatements;
        List<Statement> result = new ArrayList<Statement>();
        // get list of web resources that have that specific fonterss
        try {
            fLStatements = 
                    repositoryConnection.getStatements(null, hasFonteRSS, entrypoint, false, sourceGraph).asList();
        } catch (RepositoryException e) {
            throw new SlicerException();
        }
        // get the outcoming triples from that specific fonte rss
        try {
            fonteOutcomingStatements = repositoryConnection.getStatements(
                    entrypoint, null, null, false, sourceGraph).asList();
        } catch (RepositoryException e) {
            throw new SlicerException();
        }
        result.addAll(fonteOutcomingStatements);
        for(Statement statement : fLStatements) {
            // for each resource get all its outcoming triples. 
            try {
                result.addAll(
                        repositoryConnection.getStatements(
                                statement.getSubject(), null, null, false, sourceGraph).asList()
                );
            } catch (RepositoryException e) {
                throw new SlicerException();
            }
        }
        return result;
    }
}
