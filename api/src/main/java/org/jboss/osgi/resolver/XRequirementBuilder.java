/*
 * #%L
 * JBossOSGi Resolver API
 * %%
 * Copyright (C) 2010 - 2012 JBoss by Red Hat
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

package org.jboss.osgi.resolver;

import static org.jboss.osgi.resolver.XResourceConstants.MAVEN_IDENTITY_NAMESPACE;
import static org.jboss.osgi.resolver.XResourceConstants.MODULE_IDENTITY_NAMESPACE;

import java.util.HashMap;
import java.util.Map;

import org.jboss.modules.ModuleIdentifier;

/**
 * A builder for resource requirements
 *
 * @author thomas.diesler@jboss.com
 * @since 16-Jan-2012
 */
public abstract class XRequirementBuilder {

    public static XRequirement createArtifactRequirement(ModuleIdentifier moduleId) {
        return createRequirement(MODULE_IDENTITY_NAMESPACE, moduleId.toString());
    }

    public static XRequirement createArtifactRequirement(MavenCoordinates coordinates) {
        return createRequirement(MAVEN_IDENTITY_NAMESPACE, coordinates.toExternalForm());
    }

    public static XRequirement createRequirement(String namespace, String value) {
        XResourceBuilder builder = XResourceBuilderFactory.create();
        Map<String, Object> atts = new HashMap<String, Object>();
        atts.put(namespace, value);
        return builder.addGenericRequirement(namespace, atts, null);
    }
}