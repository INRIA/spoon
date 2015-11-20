/**
 * Copyright (C) 2002-2006 - INRIA (www.inria.fr)
 *
 * CAROL: Common Architecture for RMI ObjectWeb Layer
 *
 * This library is developed inside the ObjectWeb Consortium,
 * http://www.objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * --------------------------------------------------------------------------
 * $Id: CmiContext.java,v 1.2 2006-01-26 16:28:55 pelletib Exp $
 * --------------------------------------------------------------------------
 */
package org.objectweb.carol.jndi.spi;

import java.io.Serializable;
import java.rmi.Remote;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.rmi.CORBA.PortableRemoteObjectDelegate;

import org.objectweb.carol.jndi.wrapping.JNDIResourceWrapper;
import org.objectweb.carol.jndi.wrapping.UnicastJNDIReferenceWrapper;
import org.objectweb.carol.rmi.exception.NamingExceptionHelper;
import org.objectweb.carol.util.configuration.ConfigurationRepository;

/**
 * @author Florent Benoit
 * @author Benoit Pelletier
 */
public class CmiContext extends AbsContext implements Context {

    /**
     * Constructs an CMI Wrapper context
     * @param cmiContext the inital CMI context
     */
    public CmiContext(Context cmiContext) {
        super(cmiContext);
    }


    /**
     * If this object is a reference wrapper return the reference If this object
     * is a resource wrapper return the resource
     * @param o the object to resolve
     * @param name name of the object to unwrap
     * @return the unwrapped object
     * @throws NamingException if the object cannot be unwraped
     */
    protected Object unwrapObject(Object o, Name name) throws NamingException {
        return super.defaultUnwrapObject(o, name);
    }

    /**
     * Wrap an Object : If the object is a reference wrap it into a Reference
     * Wrapper Object here the good way is to contact the carol configuration to
     * get the portable remote object
     * @param o the object to encode
     * @param name of the object
     * @param replace if the object need to be replaced
     * @return a <code>Remote JNDIRemoteReference Object</code> if o is a
     *         resource o if else
     * @throws NamingException if object cannot be wrapped
     */
    protected Object wrapObject(Object o, Name name, boolean replace) throws NamingException {
            try {
                // Add wrapper for the two first cases. Then it will use PortableRemoteObject instead of UnicastRemoteObject
                // and when fixing JRMP exported objects port, it use JRMPProdelegate which is OK.
                if ((!(o instanceof Remote)) && (o instanceof Referenceable)) {
                    return new UnicastJNDIReferenceWrapper(((Referenceable) o).getReference(), getObjectPort());
                } else if ((!(o instanceof Remote)) && (o instanceof Reference)) {
                    return new UnicastJNDIReferenceWrapper((Reference) o, getObjectPort());
                } else if ((!(o instanceof Remote)) && (!(o instanceof Referenceable)) && (!(o instanceof Reference))
                        && (o instanceof Serializable)) {
                    // Only Serializable (not implementing Remote or Referenceable or
                    // Reference)
                    JNDIResourceWrapper irw = new JNDIResourceWrapper((Serializable) o);
                    PortableRemoteObjectDelegate proDelegate = ConfigurationRepository.getCurrentConfiguration().getProtocol().getPortableRemoteObject();
                    proDelegate.exportObject(irw);

                    Remote oldObj = (Remote) addToExported(name, irw);
                    if (oldObj != null) {
                        if (replace) {
                            proDelegate.unexportObject(oldObj);
                        } else {
                            proDelegate.unexportObject(irw);
                            addToExported(name, oldObj);
                            throw new NamingException("Object '" + o + "' with name '" + name + "' is already bind");
                        }
                    }
                    return irw;
                } else {
                    return o;
                }
            } catch (Exception e) {
                throw NamingExceptionHelper.create("Cannot wrap object '" + o + "' with name '" + name + "' : "
                        + e.getMessage(), e);
            }
    }
}
