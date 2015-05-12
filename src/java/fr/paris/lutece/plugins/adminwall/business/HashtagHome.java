/*
 * Copyright (c) 2002-2015, Mairie de Paris
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
 * This class provides instances management methods (create, find, ...) for Hashtag objects
 */
public final class HashtagHome
{
    // Static variable pointed at the DAO instance
    private static IHashtagDAO _dao = SpringContextService.getBean( "adminwall.hashtagDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "adminwall" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private HashtagHome(  )
    {
    }

    /**
     * Create an instance of the hashtag class
     * @param hashtag The instance of the Hashtag which contains the informations to store
     * @return The  instance of hashtag which has been created with its primary key.
     */
    public static Hashtag create( Hashtag hashtag )
    {
        _dao.insert( hashtag, _plugin );

        return hashtag;
    }

    /**
     * Update of the hashtag which is specified in parameter
     * @param hashtag The instance of the Hashtag which contains the data to store
     * @return The instance of the  hashtag which has been updated
     */
    public static Hashtag update( Hashtag hashtag )
    {
        _dao.store( hashtag, _plugin );

        return hashtag;
    }

    /**
     * Remove the hashtag whose identifier is specified in parameter
     * @param nHashtagId The hashtag Id
     */
    public static void remove( int nHashtagId )
    {
        _dao.delete( nHashtagId, _plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a hashtag whose identifier is specified in parameter
     * @param nKey The hashtag primary key
     * @return an instance of Hashtag
     */
    public static Hashtag findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the hashtag objects and returns them in form of a collection
     * @return the collection which contains the data of all the hashtag objects
     */
    public static Collection<Hashtag> getHashtagsList(  )
    {
        return _dao.selectHashtagsList( _plugin );
    }

    
     /**
     * Returns an id whose content contains the Tag specified in parameter
     * @param strTag The tag 
     * @return the hashtag Id
     */
    public static int getId( String strTag )
    {
        return _dao.selectId( strTag, _plugin );
    }
}
