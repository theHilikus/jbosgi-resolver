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
package org.jboss.osgi.resolver.v2.spi;

import org.jboss.osgi.resolver.v2.VersionRange;
import org.jboss.osgi.resolver.v2.XIdentityCapability;
import org.jboss.osgi.resolver.v2.XIdentityRequirement;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRevision;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.osgi.framework.Constants.BUNDLE_VERSION_ATTRIBUTE;
import static org.osgi.framework.resource.ResourceConstants.IDENTITY_NAMESPACE;

/**
 * The abstract implementation of a {@link XIdentityRequirement}.
 *
 * @author thomas.diesler@jboss.com
 * @since 02-Jul-2010
 */
public class AbstractIdentityRequirement extends AbstractBundleRequirement implements XIdentityRequirement {

    private final String symbolicName;
    private final VersionRange versionrange;

    protected AbstractIdentityRequirement(BundleRevision brev, Map<String, Object> atts, Map<String, String> dirs) {
        super(brev, IDENTITY_NAMESPACE, atts, dirs);
        this.symbolicName = (String) getAttribute(IDENTITY_NAMESPACE);
        Object versionatt = atts.get(BUNDLE_VERSION_ATTRIBUTE);
        if (versionatt instanceof String) {
            versionatt = VersionRange.parse((String) versionatt);
        }
        versionrange = (VersionRange) versionatt;
    }

    @Override
    protected List<String> getMandatoryAttributes() {
        return Arrays.asList(IDENTITY_NAMESPACE);
    }

    @Override
    public String getSymbolicName() {
        return symbolicName;
    }

    @Override
    public VersionRange getVersionRange() {
        return versionrange;
    }

    @Override
    public boolean matches(BundleCapability cap) {

        // cannot require itself
        if (getResource() == cap.getResource())
            return false;

        if (super.matches(cap) == false)
            return false;

        // match the bundle version range
        if (versionrange != null) {
            Version version = ((XIdentityCapability) cap).getVersion();
            if (versionrange.isInRange(version) == false)
                return false;
        }

        return true;
    }
}