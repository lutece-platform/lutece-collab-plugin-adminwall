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
 * This class provides Data Access methods for Hashtag objects
 */
public final class HashtagDAO implements IHashtagDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_hashtag ) FROM adminwall_hashtag";
    private static final String SQL_QUERY_SELECT = "SELECT id_hashtag, tag FROM adminwall_hashtag WHERE id_hashtag = ?";
    private static final String SQL_QUERY_SELECT_ID = "SELECT id_hashtag FROM adminwall_hashtag WHERE tag = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO adminwall_hashtag ( id_hashtag, tag ) VALUES ( ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM adminwall_hashtag WHERE id_hashtag = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE adminwall_hashtag SET id_hashtag = ?, tag = ? WHERE id_hashtag = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_hashtag, tag FROM adminwall_hashtag";

    /**
     * Generates a new primary key
     *
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
    public void insert( Hashtag hashtag, Plugin plugin )
    {
        /*Verification -> pas de doublon*/
        DAOUtil daoUtilV = new DAOUtil( SQL_QUERY_SELECT_ID );
        daoUtilV.setString( 1, hashtag.getTag(  ) );
        daoUtilV.executeQuery(  );

        if ( daoUtilV.next(  ) )
        {
            hashtag.setIdHashtag( daoUtilV.getInt( 1 ) );
        }
        else
        {
            DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

            hashtag.setIdHashtag( newPrimaryKey( plugin ) );

            daoUtil.setInt( 1, hashtag.getIdHashtag(  ) );
            daoUtil.setString( 2, hashtag.getTag(  ) );

            daoUtil.executeUpdate(  );
            daoUtil.free(  );
        }

        daoUtilV.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Hashtag load( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeQuery(  );

        Hashtag hashtag = null;

        if ( daoUtil.next(  ) )
        {
            hashtag = new Hashtag(  );
            hashtag.setIdHashtag( daoUtil.getInt( 1 ) );
            hashtag.setTag( daoUtil.getString( 2 ) );
        }

        daoUtil.free(  );

        return hashtag;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nHashtagId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nHashtagId );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( Hashtag hashtag, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        daoUtil.setInt( 1, hashtag.getIdHashtag(  ) );
        daoUtil.setString( 2, hashtag.getTag(  ) );
        daoUtil.setInt( 3, hashtag.getIdHashtag(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<Hashtag> selectHashtagsList( Plugin plugin )
    {
        Collection<Hashtag> hashtagList = new ArrayList<Hashtag>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            Hashtag hashtag = new Hashtag(  );

            hashtag.setIdHashtag( daoUtil.getInt( 1 ) );
            hashtag.setTag( daoUtil.getString( 2 ) );

            hashtagList.add( hashtag );
        }

        daoUtil.free(  );

        return hashtagList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int selectId( String tag, Plugin plugin )
    {
        int idHashtag;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ID, plugin );
        daoUtil.setString( 1, tag );
        daoUtil.executeQuery(  );

        daoUtil.next(  );
        idHashtag = daoUtil.getInt( 1 );

        daoUtil.free(  );

        return idHashtag;
    }
}
