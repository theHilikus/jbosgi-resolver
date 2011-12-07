/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.osgi.resolver.spi;

import org.jboss.osgi.resolver.XBundleRevision;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.osgi.framework.resource.Capability;
import org.osgi.framework.resource.Requirement;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleWiring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The abstract implementation of a {@link org.jboss.osgi.resolver.XBundleRequirement}.
 * 
 * @author thomas.diesler@jboss.com
 * @since 02-Jul-2010
 */
public class AbstractBundleRevision extends AbstractResource implements XBundleRevision {

    private final Map<String, List<BundleCapability>> capabilities = new HashMap<String, List<BundleCapability>>();
    private final Map<String, List<BundleRequirement>> requirements = new HashMap<String, List<BundleRequirement>>();
    private final String symbolicName;
    private final Version version;

    public AbstractBundleRevision(String symbolicName, Version version) {
        this.symbolicName = symbolicName;
        this.version = version;
    }

    @Override
    public String getSymbolicName() {
        return symbolicName;
    }

    @Override
    public Version getVersion() {
        return version;
    }

    @Override
    public List<BundleCapability> getDeclaredCapabilities(String namespace) {
        List<BundleCapability> caplist = capabilities.get(namespace);
        if (caplist == null) {
            caplist = new ArrayList<BundleCapability>();
            for (Capability cap : getCapabilities(namespace)) {
                caplist.add((BundleCapability) cap);
            }
            capabilities.put(namespace, caplist);
        }
        return caplist;
    }

    @Override
    public List<BundleRequirement> getDeclaredRequirements(String namespace) {
        List<BundleRequirement> reqlist = requirements.get(namespace);
        if (reqlist == null) {
            reqlist = new ArrayList<BundleRequirement>();
            for (Requirement req : getRequirements(namespace)) {
                reqlist.add((BundleRequirement) req);
            }
            requirements.put(namespace, reqlist);
        }
        return reqlist;
    }

    @Override
    public int getTypes() {
        return 0;
    }

    @Override
    public BundleWiring getWiring() {
        return getAttachment(BundleWiring.class);
    }

    @Override
    public Bundle getBundle() {
        return getAttachment(Bundle.class);
    }

    public String toString() {
        return getClass().getSimpleName() + "[" + symbolicName + ":" + version + "]";
    }
}