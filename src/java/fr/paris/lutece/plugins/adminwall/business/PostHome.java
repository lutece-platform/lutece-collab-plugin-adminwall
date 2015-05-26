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
 * This class provides instances management methods (create, find, ...) for Post objects
 */
public final class PostHome
{
    // Static variable pointed at the DAO instance
    private static IPostDAO _dao = SpringContextService.getBean( "adminwall.postDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "adminwall" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private PostHome(  )
    {
    }

    /**
     * Create an instance of the post class
     * @param post The instance of the Post which contains the informations to store
     * @return The  instance of post which has been created with its primary key.
     */
    public static Post create( Post post )
    {
        _dao.insert( post, _plugin );

        return post;
    }

    /**
     * Update of the post which is specified in parameter
     * @param post The instance of the Post which contains the data to store
     * @return The instance of the  post which has been updated
     */
    public static Post update( Post post )
    {
        _dao.store( post, _plugin );

        return post;
    }

    /**
     * Remove the post whose identifier is specified in parameter
     * @param nPostId The post Id
     */
    public static void remove( int nPostId )
    {
        _dao.delete( nPostId, _plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a post whose identifier is specified in parameter
     * @param nKey The post primary key
     * @return an instance of Post
     */
    public static Post findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the post objects and returns them in form of a collection
     * @return the collection which contains the data of all the post objects
     */
    public static Collection<Post> getPostsList(  )
    {
        return _dao.selectPostsList( _plugin );
    }

    /**
     * Load the data of all the post objects whose Auteur identifier is specified in parameter and return them in form of a collection
     * @param nIdAuteur The Auteur primary key
     * @return the collection which contains the data of all the post objects
     */
    public static Collection<Post> getPostsListIdAuteur( int nIdAuteur )
    {
        return _dao.selectPostsListIdAuteur( nIdAuteur, _plugin );
    }
}
