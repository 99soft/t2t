package org.nnsoft.t2t.slicers;

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

    private final static URI hasFonteRSS = new URIImpl( "http://www.cybion.it/proconsult/url#hasFonteRss" );

    public List<Statement> slice( URI entrypoint, RepositoryConnection repositoryConnection, URI sourceGraph )
        throws SlicerException
    {
        List<Statement> fLStatements, fonteOutcomingStatements;
        List<Statement> result = new ArrayList<Statement>();
        // get list of web resources that have that specific fonterss
        try
        {
            fLStatements =
                repositoryConnection.getStatements( null, hasFonteRSS, entrypoint, false, sourceGraph ).asList();
        }
        catch ( RepositoryException e )
        {
            throw new SlicerException();
        }
        // get the outcoming triples from that specific fonte rss
        try
        {
            fonteOutcomingStatements =
                repositoryConnection.getStatements( entrypoint, null, null, false, sourceGraph ).asList();
        }
        catch ( RepositoryException e )
        {
            throw new SlicerException();
        }
        result.addAll( fonteOutcomingStatements );
        for ( Statement statement : fLStatements )
        {
            // for each resource get all its outcoming triples.
            try
            {
                result.addAll( repositoryConnection.getStatements( statement.getSubject(), null, null, false,
                                                                   sourceGraph ).asList() );
            }
            catch ( RepositoryException e )
            {
                throw new SlicerException();
            }
        }
        return result;
    }

}
