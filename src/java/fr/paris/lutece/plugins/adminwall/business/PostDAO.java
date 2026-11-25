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
import fr.paris.lutece.util.sql.DAOUtil;

import jakarta.enterprise.context.ApplicationScoped;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;


/**
 * This class provides Data Access methods for Post objects
 */
@ApplicationScoped
public final class PostDAO implements IPostDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT id_post, contenu, date, id_auteur, auteur, importance FROM adminwall_post WHERE id_post = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO adminwall_post ( contenu, date, id_auteur, auteur, importance ) VALUES ( ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM adminwall_post WHERE id_post = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE adminwall_post SET id_post = ?, contenu = ?, date = ?, id_auteur= ?, auteur = ?, importance = ? WHERE id_post = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_post, contenu, date, id_auteur, auteur, importance FROM adminwall_post ORDER BY id_post DESC";
    private static final String SQL_QUERY_SELECT_ID_AUTEUR = "SELECT id_post, contenu, date, id_auteur, auteur, importance FROM adminwall_post WHERE id_auteur = ? ORDER BY id_post DESC";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( Post post, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            daoUtil.setString( 1, post.getContenu(  ) );
            daoUtil.setTimestamp( 2, post.getTimestamp(  ) );
            daoUtil.setInt( 3, post.getIdAuteur(  ) );
            daoUtil.setString( 4, post.getAuteur(  ) );
            daoUtil.setString( 5, post.getImportance(  ) );

            daoUtil.executeUpdate(  );

            if ( daoUtil.nextGeneratedKey(  ) )
            {
                post.setIdPost( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Post load( int nKey, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery(  );

            Post post = null;

            if ( daoUtil.next(  ) )
            {
                post = new Post(  );
                post.setIdPost( daoUtil.getInt( 1 ) );
                post.setContenu( daoUtil.getString( 2 ) );
                post.setTimestamp( daoUtil.getTimestamp( 3 ) );
                post.setIdAuteur( daoUtil.getInt( 4 ) );
                post.setAuteur( daoUtil.getString( 5 ) );
                post.setImportance( daoUtil.getString( 6 ) );
            }

            return post;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nPostId, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nPostId );
            daoUtil.executeUpdate(  );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( Post post, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            daoUtil.setInt( 1, post.getIdPost(  ) );
            daoUtil.setString( 2, post.getContenu(  ) );
            daoUtil.setTimestamp( 3, post.getTimestamp(  ) );
            daoUtil.setInt( 4, post.getIdAuteur(  ) );
            daoUtil.setString( 5, post.getAuteur(  ) );
            daoUtil.setString( 6, post.getImportance(  ) );
            daoUtil.setInt( 7, post.getIdPost(  ) );

            daoUtil.executeUpdate(  );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<Post> selectPostsList( Plugin plugin )
    {
        Collection<Post> postList = new ArrayList<Post>(  );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery(  );

            while ( daoUtil.next(  ) )
            {
                Post post = new Post(  );

                post.setIdPost( daoUtil.getInt( 1 ) );
                post.setContenu( daoUtil.getString( 2 ) );
                post.setTimestamp( daoUtil.getTimestamp( 3 ) );
                post.setIdAuteur( daoUtil.getInt( 4 ) );
                post.setAuteur( daoUtil.getString( 5 ) );
                post.setImportance( daoUtil.getString( 6 ) );

                postList.add( post );
            }

            return postList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<Post> selectPostsListIdAuteur( int nIdAuteur, Plugin plugin )
    {
        Collection<Post> postList = new ArrayList<Post>(  );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ID_AUTEUR, plugin ) )
        {
            daoUtil.setInt( 1, nIdAuteur );

            daoUtil.executeQuery(  );

            while ( daoUtil.next(  ) )
            {
                Post post = new Post(  );

                post.setIdPost( daoUtil.getInt( 1 ) );
                post.setContenu( daoUtil.getString( 2 ) );
                post.setTimestamp( daoUtil.getTimestamp( 3 ) );
                post.setIdAuteur( daoUtil.getInt( 4 ) );
                post.setAuteur( daoUtil.getString( 5 ) );
                post.setImportance( daoUtil.getString( 6 ) );

                postList.add( post );
            }

            return postList;
        }
    }
}
