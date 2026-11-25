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
import java.util.List;
import java.util.Map;

/**
 * This class provides instances management methods for the recipients of a post
 */
public final class RecipientHome
{
    private static IRecipientDAO _dao = CDI.current( ).select( IRecipientDAO.class ).get( );
    private static Plugin _plugin = PluginService.getPlugin( "adminwall" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private RecipientHome( )
    {
    }

    /**
     * Restrict a post to the given admin users
     *
     * @param nIdPost
     *            the post id
     * @param listUserIds
     *            the admin user ids
     */
    public static void create( int nIdPost, Collection<Integer> listUserIds )
    {
        for ( Integer nIdUser : listUserIds )
        {
            _dao.insert( nIdPost, nIdUser, _plugin );
        }
    }

    /**
     * Remove all the recipients of a post
     *
     * @param nIdPost
     *            the post id
     */
    public static void removeByPost( int nIdPost )
    {
        _dao.deleteByPost( nIdPost, _plugin );
    }

    /**
     * Returns the recipients of a post
     *
     * @param nIdPost
     *            the post id
     * @return the admin user ids, empty when the post is sent to all
     */
    public static List<Integer> getRecipients( int nIdPost )
    {
        return _dao.selectByPost( nIdPost, _plugin );
    }

    /**
     * Returns the recipients of every post that has some
     *
     * @return the admin user ids by post id; a post sent to all is absent
     */
    public static Map<Integer, List<Integer>> getRecipientsMap( )
    {
        return _dao.selectAll( _plugin );
    }
}
