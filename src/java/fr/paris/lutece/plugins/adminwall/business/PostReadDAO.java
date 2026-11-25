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
import fr.paris.lutece.util.sql.DAOUtil;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashSet;
import java.util.Set;

/**
 * This class provides Data Access methods for the read marks of the posts
 */
@ApplicationScoped
public class PostReadDAO implements IPostReadDAO
{
    private static final String SQL_QUERY_INSERT = "INSERT INTO adminwall_post_read ( id_post, id_user ) VALUES ( ?, ? )";
    private static final String SQL_QUERY_DELETE_BY_POST = "DELETE FROM adminwall_post_read WHERE id_post = ?";
    private static final String SQL_QUERY_SELECT_BY_USER = "SELECT id_post FROM adminwall_post_read WHERE id_user = ?";

    /**
     * {@inheritDoc}
     */
    @Override
    public void insert( int nIdPost, int nIdUser, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin ) )
        {
            daoUtil.setInt( 1, nIdPost );
            daoUtil.setInt( 2, nIdUser );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByPost( int nIdPost, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_POST, plugin ) )
        {
            daoUtil.setInt( 1, nIdPost );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Integer> selectReadPostIds( int nIdUser, Plugin plugin )
    {
        Set<Integer> setPostIds = new HashSet<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_USER, plugin ) )
        {
            daoUtil.setInt( 1, nIdUser );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                setPostIds.add( daoUtil.getInt( 1 ) );
            }
        }

        return setPostIds;
    }
}
