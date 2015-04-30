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
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.Collection;


/**
 * This class provides Data Access methods for Link objects
 */
public final class LinkDAO implements ILinkDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_link ) FROM adminwall_link";
    private static final String SQL_QUERY_SELECT = "SELECT id_link, post, hashtag FROM adminwall_link WHERE id_link = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO adminwall_link ( id_link, post, hashtag) VALUES ( ?, ?, ?) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM adminwall_link WHERE id_link = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE adminwall_link SET id_link = ?, post = ?, hashtag = ? WHERE id_link = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_link, post, hashtag FROM adminwall_link";
    private static final String SQL_QUERY_SELECT_TAG = "SELECT id_link, post, hashtag FROM adminwall_link WHERE hashtag= ?";

    /**
     * Generates a new primary key
     * @param plugin The Plugin
     * @return The new primary key
     */
    public int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery(  );

        int nKey = 1;

        if ( daoUtil.next(  ) )
        {
            nKey = daoUtil.getInt( 1 ) + 1;
        }

        daoUtil.free(  );

        return nKey;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( Link link, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        link.setIdLink( newPrimaryKey( plugin ) );

        daoUtil.setInt( 1, link.getIdLink(  ) );
        daoUtil.setInt( 2, link.getPost(  ) );
        daoUtil.setInt( 3, link.getHashtag(  ) );

        daoUtil.executeUpdate(  ); //BUG
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Link load( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeQuery(  );

        Link link = null;

        if ( daoUtil.next(  ) )
        {
            link = new Link(  );
            link.setIdLink( daoUtil.getInt( 1 ) );
            link.setPost( daoUtil.getInt( 2 ) );
            link.setHashtag( daoUtil.getInt( 3 ) );
        }

        daoUtil.free(  );

        return link;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nLinkId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nLinkId );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( Link link, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        daoUtil.setInt( 1, link.getIdLink(  ) );
        daoUtil.setInt( 2, link.getPost(  ) );
        daoUtil.setInt( 3, link.getHashtag(  ) );
        daoUtil.setInt( 4, link.getIdLink(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<Link> selectLinksList( Plugin plugin )
    {
        Collection<Link> linkList = new ArrayList<Link>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            Link link = new Link(  );

            link.setIdLink( daoUtil.getInt( 1 ) );
            link.setPost( daoUtil.getInt( 2 ) );
            link.setHashtag( daoUtil.getInt( 3 ) );

            linkList.add( link );
        }

        daoUtil.free(  );

        return linkList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<Link> selectLinksListTag( int hashtag, Plugin plugin )
    {
        Collection<Link> linkList = new ArrayList<Link>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_TAG, plugin );
        daoUtil.setInt( 1, hashtag );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            Link link = new Link(  );

            link.setIdLink( daoUtil.getInt( 1 ) );
            link.setPost( daoUtil.getInt( 2 ) );
            link.setHashtag( daoUtil.getInt( 3 ) );

            linkList.add( link );
        }

        daoUtil.free(  );

        return linkList;
    }
}
