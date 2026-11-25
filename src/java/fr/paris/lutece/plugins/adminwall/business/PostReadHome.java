/*
 * Copyright (c) 2002-2026, Mairie de Paris
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

import jakarta.enterprise.inject.spi.CDI;

import java.util.Collection;
import java.util.Set;

/**
 * This class provides instances management methods for the read marks of the posts
 */
public final class PostReadHome
{
    private static IPostReadDAO _dao = CDI.current( ).select( IPostReadDAO.class ).get( );
    private static Plugin _plugin = PluginService.getPlugin( "adminwall" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private PostReadHome( )
    {
    }

    /**
     * Mark posts as read by a user; the posts already marked are skipped
     *
     * @param postIds
     *            the post ids
     * @param nIdUser
     *            the admin user id
     */
    public static void markRead( Collection<Integer> postIds, int nIdUser )
    {
        Set<Integer> setRead = _dao.selectReadPostIds( nIdUser, _plugin );

        for ( Integer nIdPost : postIds )
        {
            if ( setRead.add( nIdPost ) )
            {
                _dao.insert( nIdPost, nIdUser, _plugin );
            }
        }
    }

    /**
     * Remove the read marks of a post
     *
     * @param nIdPost
     *            the post id
     */
    public static void removeByPost( int nIdPost )
    {
        _dao.deleteByPost( nIdPost, _plugin );
    }

    /**
     * Returns the ids of the posts a user has marked as read
     *
     * @param nIdUser
     *            the admin user id
     * @return the post ids
     */
    public static Set<Integer> getReadPostIds( int nIdUser )
    {
        return _dao.selectReadPostIds( nIdUser, _plugin );
    }
}
