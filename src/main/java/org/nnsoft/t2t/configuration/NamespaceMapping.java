package org.nnsoft.t2t.configuration;

import org.openrdf.model.impl.URIImpl;

/**
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 */
public class NamespaceMapping {

    private URIImpl from;

    private URIImpl to;

    public NamespaceMapping(URIImpl from, URIImpl to) {
        this.from = from;
        this.to = to;
    }

    public URIImpl getFrom() {
        return from;
    }

    public URIImpl getTo() {
        return to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NamespaceMapping that = (NamespaceMapping) o;

        if (from != null ? !from.equals(that.from) : that.from != null) return false;
        if (to != null ? !to.equals(that.to) : that.to != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = from != null ? from.hashCode() : 0;
        result = 31 * result + (to != null ? to.hashCode() : 0);
        return result;
    }
}
