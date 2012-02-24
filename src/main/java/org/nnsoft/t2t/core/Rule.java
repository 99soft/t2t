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
package org.nnsoft.t2t.core;

import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.Var;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 */
public class Rule {

    private StatementPattern match;

    private Set<StatementPattern> apply;

    public Rule(StatementPattern match, Set<StatementPattern> apply) {
        this.match = match;
        this.apply = apply;
    }

    public boolean match(Statement statement)
            throws RuleExecutionException {
        Var sVar = match.getSubjectVar();
        Var pVar = match.getPredicateVar();
        Var oVar = match.getObjectVar();
        if(sVar.getValue() != null && !sVar.getValue().equals(statement.getSubject())) {
            return false;
        }
        if(pVar.getValue() != null && !pVar.getValue().equals(statement.getPredicate())) {
            return false;
        }
        if(oVar.getValue() != null && !oVar.getValue().equals(statement.getObject())) {
            return false;
        }
        return true;
    }

    public Set<Statement> apply(Statement statement) 
            throws RuleExecutionException {
        Set<Statement> result = new HashSet<Statement>();
        if(match(statement)) {
            // bind the statement values to the match pattern
            Var[] bindings = getBindings(match, statement);
            // System.out.println("applaying rule!");
            for(StatementPattern apply : this.apply) {
                Var applySVar = apply.getSubjectVar();
                Var applyPVar = apply.getPredicateVar();
                Var applyOVar = apply.getObjectVar();
                Value subjectValue = getBindValue(bindings, applySVar);
                Value predicateValue = getBindValue(bindings, applyPVar);
                Value objectValue = getBindValue(bindings, applyOVar);
                Statement newStatement = new StatementImpl(
                        new URIImpl(subjectValue.stringValue()),
                        new URIImpl(predicateValue.stringValue()),
                        objectValue
                );
                result.add(newStatement);
            }
            return result;
        }
        return result;
    }

    private Var[] getBindings(StatementPattern match, Statement statement) {
        Var sVar = new Var(match.getSubjectVar().getName(), statement.getSubject());
        Var pVar = new Var(match.getPredicateVar().getName(), statement.getPredicate());
        Var oVar = new Var(match.getObjectVar().getName(), statement.getObject());
        Var[] bindings = new Var[3];
        bindings[0] = sVar;
        bindings[1] = pVar;
        bindings[2] = oVar;
        return bindings;
    }

    private Value getBindValue(Var[] bindings, Var var) throws RuleExecutionException {
        if(var.isAnonymous() && var.getValue() != null) {
            return var.getValue();
        }
        for(Var bind : bindings) {
            if(bind.getName().equals(var.getName()))
                return bind.getValue();
        }
        throw new RuleExecutionException("");
    }

    @Override
    public String toString() {
        return "Rule{" +
                "match=" + match +
                ", apply=" + apply +
                '}';
    }
}
