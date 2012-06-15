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

package org.jboss.osgi.resolver.spi;

import static org.jboss.osgi.resolver.internal.ResolverMessages.MESSAGES;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jboss.osgi.resolver.XResource;
import org.osgi.resource.Capability;
import org.osgi.resource.Namespace;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;
import org.osgi.resource.Wire;
import org.osgi.resource.Wiring;

/**
 * The abstract implementation of a {@link Wiring}.
 * 
 * @author thomas.diesler@jboss.com
 * @since 02-Jul-2010
 */
public class AbstractWiring implements Wiring {

    private final Resource resource;
    private final List<Wire> required;
    private final List<Wire> provided;

    public AbstractWiring(XResource resource, List<Wire> required, List<Wire> provided) {
        if (resource == null)
            throw MESSAGES.illegalArgumentNull("resource");
        this.resource = resource;
        List<Wire> emptywires = Collections.emptyList();
        this.required = required != null ? required : emptywires;
        this.provided = provided != null ? provided : emptywires;
    }

    @Override
    public List<Capability> getResourceCapabilities(String namespace) {
        List<Capability> caps = new ArrayList<Capability>(resource.getCapabilities(namespace));
        Iterator<Capability> capit = caps.iterator();
        while(capit.hasNext()) {
            Capability cap = capit.next();
            // Capabilities with {@link Namespace#CAPABILITY_EFFECTIVE_DIRECTIVE} 
            // not equal to {@link Namespace#EFFECTIVE_RESOLVE} are not returned
            String effdir = cap.getDirectives().get(Namespace.CAPABILITY_EFFECTIVE_DIRECTIVE);
            if (effdir != null && !effdir.equals(Namespace.EFFECTIVE_RESOLVE)) {
                capit.remove();
            }
            // A package declared to be both exported and imported, 
            // only one is selected and the other is discarded
            String capns = cap.getNamespace();
            Object capval = cap.getAttributes().get(capns);
            for (Wire wire : required) {
                Capability wirecap = wire.getCapability();
                Object wirecapval = wirecap.getAttributes().get(wirecap.getNamespace());
                if (capns.equals(wirecap.getNamespace()) && capval.equals(wirecapval)) {
                    capit.remove();
                }
            }
        }
        return caps;
    }

    @Override
    public List<Requirement> getResourceRequirements(String namespace) {
        List<Requirement> reqs = new ArrayList<Requirement>(resource.getRequirements(namespace));
        Iterator<Requirement> reqit = reqs.iterator();
        while(reqit.hasNext()) {
            Requirement req = reqit.next();
            // Requirements with {@link Namespace#CAPABILITY_EFFECTIVE_DIRECTIVE} 
            // not equal to {@link Namespace#EFFECTIVE_RESOLVE} are not returned
            String effdir = req.getDirectives().get(Namespace.CAPABILITY_EFFECTIVE_DIRECTIVE);
            if (effdir != null && !Namespace.EFFECTIVE_RESOLVE.equals(effdir)) {
                reqit.remove();
            }
            // A package declared to be optionally imported and is not
            // actually imported, the requirement must be discarded
            String resdir = req.getDirectives().get(Namespace.REQUIREMENT_RESOLUTION_DIRECTIVE);
            if (Namespace.RESOLUTION_OPTIONAL.equals(resdir)) {
                String reqns = req.getNamespace();
                Object reqval = req.getAttributes().get(reqns);
                boolean packageWireFound = false;
                for (Wire wire : required) {
                    Capability wirecap = wire.getCapability();
                    Object wirecapval = wirecap.getAttributes().get(wirecap.getNamespace());
                    if (reqns.equals(wirecap.getNamespace()) && reqval.equals(wirecapval)) {
                        packageWireFound = true;
                        break;
                    }
                }
                if (!packageWireFound) {
                    reqit.remove();
                }
            }
        }
        return reqs;
    }

    @Override
    public List<Wire> getProvidedResourceWires(String namespace) {
        List<Wire> result = new ArrayList<Wire>();
        if (provided != null) {
            for (Wire wire : provided) {
                Capability cap = wire.getCapability();
                if (namespace == null || namespace.equals(cap.getNamespace())) {
                    result.add(wire);
                }
            }
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public List<Wire> getRequiredResourceWires(String namespace) {
        List<Wire> result = new ArrayList<Wire>();
        if (required != null) {
            for (Wire wire : required) {
                Requirement req = wire.getRequirement();
                if (namespace == null || namespace.equals(req.getNamespace())) {
                    result.add(wire);
                }
            }
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public Resource getResource() {
        return resource;
    }

    @Override
    public String toString() {
        return "Wiring[" + resource + "]";
    }
}