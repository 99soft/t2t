package org.nnsoft.t2t.configuration;

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

import org.nnsoft.t2t.core.Rule;
import org.openrdf.model.URI;

import java.util.Set;

/**
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 */
public class MigratorConfiguration
{

    private URI sourceGraph;

    private ConnectionParameter sourceConnection;

    private URI destinationGraph;

    private ConnectionParameter destinationConnection;

    private Set<Rule> rules;

    private Set<NamespaceMapping> namespaceMappings;

    private int commitRate;

    private boolean activeFiltering;

    private String slicingClass;

    public URI getSourceGraph()
    {
        return sourceGraph;
    }

    public boolean isActiveFiltering()
    {
        return activeFiltering;
    }

    public void setActiveFiltering( boolean activeFiltering )
    {
        this.activeFiltering = activeFiltering;
    }

    public void setSourceGraph( URI sourceGraph )
    {
        this.sourceGraph = sourceGraph;
    }

    public void setSourceConnection( ConnectionParameter sourceConnection )
    {
        this.sourceConnection = sourceConnection;
    }

    public void setDestinationGraph( URI destinationGraph )
    {
        this.destinationGraph = destinationGraph;
    }

    public void setDestinationConnection( ConnectionParameter destinationConnection )
    {
        this.destinationConnection = destinationConnection;
    }

    public void setRules( Set<Rule> rules )
    {
        this.rules = rules;
    }

    public void setCommitRate( int commitRate )
    {
        this.commitRate = commitRate;
    }

    public URI getDestinationGraph()
    {
        return destinationGraph;
    }

    public Set<Rule> getRules()
    {
        return rules;
    }

    public int getCommitRate()
    {
        return commitRate;
    }

    public ConnectionParameter getSourceConnection()
    {
        return sourceConnection;
    }

    public ConnectionParameter getDestinationConnection()
    {
        return destinationConnection;
    }

    public Set<NamespaceMapping> getNamespaceMappings()
    {
        return namespaceMappings;
    }

    public void setNamespaceMappings( Set<NamespaceMapping> namespaceMappings )
    {
        this.namespaceMappings = namespaceMappings;
    }

    public String getSlicingClass()
    {
        return slicingClass;
    }

    public void setSlicingClass( String slicingClass )
    {
        this.slicingClass = slicingClass;
    }

    public static class ConnectionParameter
    {

        private String host;

        private int port;

        private String user;

        private String password;

        public ConnectionParameter( String host, int port, String user, String password )
        {
            this.host = host;
            this.port = port;
            this.user = user;
            this.password = password;
        }

        public String getHost()
        {
            return host;
        }

        public int getPort()
        {
            return port;
        }

        public String getUser()
        {
            return user;
        }

        public String getPassword()
        {
            return password;
        }

        @Override
        public boolean equals( Object o )
        {
            if ( this == o )
                return true;
            if ( o == null || getClass() != o.getClass() )
                return false;

            ConnectionParameter that = (ConnectionParameter) o;

            if ( port != that.port )
                return false;
            if ( host != null ? !host.equals( that.host ) : that.host != null )
                return false;
            if ( password != null ? !password.equals( that.password ) : that.password != null )
                return false;
            if ( user != null ? !user.equals( that.user ) : that.user != null )
                return false;

            return true;
        }

        @Override
        public int hashCode()
        {
            int result = host != null ? host.hashCode() : 0;
            result = 31 * result + port;
            result = 31 * result + ( user != null ? user.hashCode() : 0 );
            result = 31 * result + ( password != null ? password.hashCode() : 0 );
            return result;
        }
    }

}
