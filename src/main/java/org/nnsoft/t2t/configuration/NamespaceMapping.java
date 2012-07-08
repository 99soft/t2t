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

import org.openrdf.model.impl.URIImpl;

/**
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 */
public class NamespaceMapping
{

    private URIImpl from;

    private URIImpl to;

    public NamespaceMapping( URIImpl from, URIImpl to )
    {
        this.from = from;
        this.to = to;
    }

    public URIImpl getFrom()
    {
        return from;
    }

    public URIImpl getTo()
    {
        return to;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;

        NamespaceMapping that = (NamespaceMapping) o;

        if ( from != null ? !from.equals( that.from ) : that.from != null )
            return false;
        if ( to != null ? !to.equals( that.to ) : that.to != null )
            return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = from != null ? from.hashCode() : 0;
        result = 31 * result + ( to != null ? to.hashCode() : 0 );
        return result;
    }

}
