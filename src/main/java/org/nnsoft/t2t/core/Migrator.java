package org.nnsoft.t2t.core;

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

import org.openrdf.model.URI;
import org.openrdf.repository.Repository;

/**
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 */
interface Migrator
{

    void setSourceRepository( Repository repository, URI graph )
        throws MigratorException;

    void setDestinationRepository( Repository repository, URI graph )
        throws MigratorException;

    void addRule( Rule rule )
        throws MigratorException;

    MigrationStats run( URI entrypoint )
        throws MigratorException;

}
