package com.cybion.commons.migration.configuration;

import com.cybion.commons.migration.core.Rule;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.Var;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 */
public class ConfigurationManager {

    private static ConfigurationManager instance;

    public static ConfigurationManager getInstance(File file) {
        if( instance == null) {
            instance = new ConfigurationManager(file);
        }
        return instance;
    }

    private ConfigurationManager(File file) {
        if(file == null)
            throw new IllegalArgumentException("Configuration file path cannot be null");
        if(!file.exists()) {
            throw new IllegalArgumentException("Configuration file: '" +
                    file.getPath() + "' does not exists");
        }
        XMLConfiguration xmlConfiguration;
        try {
            xmlConfiguration = new XMLConfiguration(file.getAbsolutePath());
        } catch (ConfigurationException e) {
            throw new RuntimeException("Error while loading XMLConfiguration", e);
        }
        configuration = new MigratorConfiguration();
        configuration.setSourceGraph(getSourceGraph(xmlConfiguration));
        configuration.setSourceConnection(getSourceConnection(xmlConfiguration));
        configuration.setDestinationGraph(getDestinationGraph(xmlConfiguration));
        configuration.setDestinationConnection(getDestinationConnection(xmlConfiguration));
        configuration.setCommitRate(getCommitRate(xmlConfiguration));
        configuration.setActiveFiltering(isActiveFiltering(xmlConfiguration));
        configuration.setRules(getRules(xmlConfiguration));
        configuration.setNamespaceMappings(getNamespaceMappings(xmlConfiguration));
        configuration.setSlicingClass(getSlicingClass(xmlConfiguration));
    }

    private String getSlicingClass(XMLConfiguration xmlConfiguration) {
        HierarchicalConfiguration source = xmlConfiguration.configurationAt("source");
        String slicingClass = source.getString("slicing-class");
        return slicingClass;
    }

    private Set<NamespaceMapping> getNamespaceMappings(XMLConfiguration xmlConfiguration) {
        Set<NamespaceMapping> result = new HashSet<NamespaceMapping>();
        HierarchicalConfiguration rules = xmlConfiguration.configurationAt("namespace-mappings");
        List<HierarchicalConfiguration> mappings = rules.configurationsAt("mapping");
        for(HierarchicalConfiguration mapping : mappings) {
            String from = mapping.getString("from");
            String to = mapping.getString("to");
            result.add(new NamespaceMapping(new URIImpl(from), new URIImpl(to)));
        }
        return result;
    }

    private boolean isActiveFiltering(XMLConfiguration xmlConfiguration) {
        return xmlConfiguration.getBoolean("active-filtering");
    }

    private Set<Rule> getRules(XMLConfiguration xmlConfiguration) {
        Set<Rule> result = new HashSet<Rule>();
        HierarchicalConfiguration rules = xmlConfiguration.configurationAt("rules");
        List<HierarchicalConfiguration> rulesList = rules.configurationsAt("rule");
        for(HierarchicalConfiguration rule : rulesList) {
            result.add(getRule(rule));
        }
        return result;
    }

    private Rule getRule(HierarchicalConfiguration rule) {
        HierarchicalConfiguration match = rule.configurationAt("match");
        String pattern = match.getString("pattern");
        StatementPattern matchStatementPattern = parsePattern(pattern);

        Set<StatementPattern> applyStatementPatterns = new HashSet<StatementPattern>();
        HierarchicalConfiguration apply = rule.configurationAt("apply");
        List<HierarchicalConfiguration> patterns = apply.configurationsAt("patterns");
        for(HierarchicalConfiguration applyPattern : patterns) {
            applyStatementPatterns.add(parsePattern(applyPattern.getString("pattern")));
        }
        return new Rule(matchStatementPattern, applyStatementPatterns);
    }

    private StatementPattern parsePattern(String string) {
        String rawVars[] = string.trim().split(" ");
        Var clensedVars[] = new Var[rawVars.length];
        int index = 0;
        for(String var : rawVars) {
            if(var.startsWith("?")) {
                Var clensedVar = new Var();
                clensedVar.setName(var.replace("?", ""));
                clensedVars[index] = clensedVar;
                index++;
                continue;
            }
            if(var.startsWith("<") && var.endsWith(">")) {
                Var clensedVar = new Var();
                clensedVar.setAnonymous(true);
                clensedVar.setName("anonymous-" + index);
                clensedVar.setValue(new URIImpl(var.replace("?", "").replace("<", "").replace(">", "")));
                clensedVars[index] = clensedVar;
                index++;
                continue;
            }
        }
        return new StatementPattern(clensedVars[0], clensedVars[1], clensedVars[2]);
    }

    private int getCommitRate(XMLConfiguration xmlConfiguration) {
        return xmlConfiguration.getInt("commit-rate");
    }

    private MigratorConfiguration.ConnectionParameter getDestinationConnection(XMLConfiguration xmlConfiguration) {
        HierarchicalConfiguration destination = xmlConfiguration.configurationAt("destination");
        HierarchicalConfiguration connection = destination.configurationAt("connection");
        String host = connection.getString("host");
        int port = connection.getInt("port");
        String user = connection.getString("username");
        String passwd = connection.getString("password");
        return new MigratorConfiguration.ConnectionParameter(host, port, user, passwd);
    }

    private URI getDestinationGraph(XMLConfiguration xmlConfiguration) {
        HierarchicalConfiguration destination = xmlConfiguration.configurationAt("destination");
        return new URIImpl(destination.getString("graph"));
    }

    private MigratorConfiguration.ConnectionParameter getSourceConnection(XMLConfiguration xmlConfiguration) {
        HierarchicalConfiguration source = xmlConfiguration.configurationAt("source");
        HierarchicalConfiguration connection = source.configurationAt("connection");
        String host = connection.getString("host");
        int port = connection.getInt("port");
        String user = connection.getString("username");
        String passwd = connection.getString("password");
        return new MigratorConfiguration.ConnectionParameter(host, port, user, passwd);
    }

    private URI getSourceGraph(XMLConfiguration xmlConfiguration) {
        HierarchicalConfiguration source = xmlConfiguration.configurationAt("source");
        return new URIImpl(source.getString("graph"));
    }

    private MigratorConfiguration configuration;

    public MigratorConfiguration getConfiguration() {
        return configuration;
    }

}
