/*
 * Copyright (c) 2002-2013, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *         and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *         and the following disclaimer in the documentation and/or other materials
 *         provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *         contributors may be used to endorse or promote products derived from
 *         this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.adminwall.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.Collection;


/**
 * This class provides instances management methods (create, find, ...) for Link objects
 */
public final class LinkHome
{
    // Static variable pointed at the DAO instance
    private static ILinkDAO _dao = SpringContextService.getBean( "adminwall.linkDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "adminwall" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private LinkHome(  )
    {
    }

    /**
     * Create an instance of the link class
     * @param link The instance of the Link which contains the informations to store
     * @return The  instance of link which has been created with its primary key.
     */
    public static Link create( Link link )
    {
        _dao.insert( link, _plugin );

        return link;
    }

    /**
     * Update of the link which is specified in parameter
     * @param link The instance of the Link which contains the data to store
     * @return The instance of the  link which has been updated
     */
    public static Link update( Link link )
    {
        _dao.store( link, _plugin );

        return link;
    }

    /**
     * Remove the link whose identifier is specified in parameter
     * @param nLinkId The link Id
     */
    public static void remove( int nLinkId )
    {
        _dao.delete( nLinkId, _plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a link whose identifier is specified in parameter
     * @param nKey The link primary key
     * @return an instance of Link
     */
    public static Link findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the link objects and returns them in form of a collection
     * @return the collection which contains the data of all the link objects
     */
    public static Collection<Link> getLinksList(  )
    {
        return _dao.selectLinksList( _plugin );
    }

    
    /**
    *Load the data of all the links whose hashtag is specified in parameter and and returns them as a collection
    *@param hashtag The hashtag
    * @return a Collection of Link
    */
    public static Collection<Link> getLinksListTag( int hashtag )
    {
        return _dao.selectLinksListTag( hashtag, _plugin );
    }
}
