package com.cybion.commons.migration.core;

import org.openrdf.model.Statement;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.Var;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 */
public class RuleTestCase {

    private Rule rule;

    private Statement statement;

    @BeforeTest
    public void setUp() {
        statement = new StatementImpl(
                new URIImpl("http://davidepalmisano.com"),
                new URIImpl("http://xmlns.org/foaf/01/knows"),
                new URIImpl("http://matteo.mo.ci"));
        StatementPattern statementPattern = new StatementPattern();
        Var s = new Var("s");
        Var p = new Var("p", new URIImpl("http://xmlns.org/foaf/01/knows"));
        Var o = new Var("o");
        statementPattern.setSubjectVar(s);
        statementPattern.setPredicateVar(p);
        statementPattern.setObjectVar(o);
        Set apply = new HashSet<StatementPattern>();

        StatementPattern statementPattern1 = new StatementPattern();
        Var s1 = new Var("o");
        Var p1 = new Var("p", new URIImpl("http://xmlns.org/foaf/01/knows"));
        Var o1 = new Var("s");
        statementPattern1.setSubjectVar(s1);
        statementPattern1.setPredicateVar(p1);
        statementPattern1.setObjectVar(o1);
        apply.add(statementPattern1);

        StatementPattern statementPattern2 = new StatementPattern();
        Var s2 = new Var("s");
        Var p2 = new Var("p", new URIImpl("http://xmlns.org/foaf/01/knows"));
        Var o2 = new Var("o");
        statementPattern2.setSubjectVar(s2);
        statementPattern2.setPredicateVar(p2);
        statementPattern2.setObjectVar(o2);
        apply.add(statementPattern2);
        rule = new Rule(statementPattern, apply);
    }

    @AfterTest
    public void tearDown() {
    }

    @Test
    public void testMatch() throws RuleExecutionException {
        Assert.assertTrue(rule.match(statement));
    }

    @Test
    public void testApply() throws RuleExecutionException {
        Set<Statement> statements = rule.apply(statement);
        Assert.assertNotNull(statements);
        Assert.assertTrue(statements.size() > 0);
        Statement newExpectedStatement = new StatementImpl(
                new URIImpl("http://matteo.mo.ci"),
                new URIImpl("http://xmlns.org/foaf/01/knows"),
                new URIImpl("http://davidepalmisano.com")
        );
        Assert.assertTrue(statements.contains(statement));
        Assert.assertTrue(statements.contains(newExpectedStatement));

    }


}
