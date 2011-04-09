package com.cybion.commons.migration.core;

/**
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 */
public class MigratorException extends Exception {
    
    public MigratorException(String message) {
        super(message);
    }

    public MigratorException(String message, Exception e) {
        super(message, e);
    }
}
