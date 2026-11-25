/*
 * Copyright (c) 2002-2015, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
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

import fr.paris.lutece.portal.service.rbac.RBACResource;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotEmpty;


/**
 * This is the business class for the object Post
 */
public class Post implements Serializable, RBACResource
{
    private static final long serialVersionUID = 1L;

    /** RBAC resource type of the messages ( PostResourceIdService ) */
    public static final String RESOURCE_TYPE = "ADMINWALL_POST";

    /** High importance : red badge */
    public static final String IMPORTANCE_HIGH = "high";

    /** Medium importance : warning badge */
    public static final String IMPORTANCE_MEDIUM = "medium";

    /** Standard importance, the default : primary badge */
    public static final String IMPORTANCE_STANDARD = "standard";

    // Variables declarations 
    private int _nIdPost;
    @NotEmpty( message = "#i18n{adminwall.validation.post.Contenu.notEmpty}" )
    @Size( max = 255, message = "#i18n{adminwall.validation.post.Contenu.size}" )
    private String _strContenu;
    private Timestamp _tTime;

    @NotEmpty( message = "#i18n{portal.validation.message.notEmpty}" )
    @Size( max = 50, message = "#i18n{portal.validation.message.sizeMax}" )
    private String _strAuteur;

    @Pattern( regexp = "high|medium|standard", message = "#i18n{adminwall.validation.post.importance.pattern}" )
    private String _strImportance = IMPORTANCE_STANDARD;
    private int _nIdAuteur;
    private List<Integer> _listRecipients = new ArrayList<>( );
    private List<String> _listRecipientNames = new ArrayList<>( );
    private boolean _bUnread;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResourceTypeCode(  )
    {
        return RESOURCE_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResourceId(  )
    {
        return String.valueOf( _nIdPost );
    }

    /**
     * Returns the IdPost
     *
     * @return The IdPost
     */
    public int getIdPost(  )
    {
        return _nIdPost;
    }

    /**
     * Sets the IdPost
     *
     * @param nIdPost The IdPost
     */
    public void setIdPost( int nIdPost )
    {
        _nIdPost = nIdPost;
    }

    /**
     * Returns the Contenu
     *
     * @return The Contenu
     */
    public String getContenu(  )
    {
        return _strContenu;
    }

    /**
     * Sets the Contenu
     *
     * @param strContenu The Contenu
     */
    public void setContenu( String strContenu )
    {
        _strContenu = strContenu;
    }

    /**
     * Returns the Time
     *
     * @return The Time
     */
    public Timestamp getTimestamp(  )
    {
        return _tTime;
    }

    /**
     * Sets the Time
     *
     * @param tTime The Time
     */
    public void setTimestamp( Timestamp tTime )
    {
        _tTime = tTime;
    }

    /**
     * Returns the IdAuteur
     *
     * @return The IdAuteur
     */
    public int getIdAuteur(  )
    {
        return _nIdAuteur;
    }

    /**
     * Sets the IdAuteur
     *
     * @param nIdAuteur The IdAuteur
     */
    public void setIdAuteur( int nIdAuteur )
    {
        _nIdAuteur = nIdAuteur;
    }

    /**
     * Returns the Auteur
     *
     * @return The Auteur
     */
    public String getAuteur(  )
    {
        return _strAuteur;
    }

    /**
     * Sets the Auteur
     *
     * @param strAuteur The Auteur
     */
    public void setAuteur( String strAuteur )
    {
        _strAuteur = strAuteur;
    }

    /**
     * Returns the ids of the admin users the post is restricted to
     *
     * @return the admin user ids, empty when the post is sent to all
     */
    public List<Integer> getRecipients( )
    {
        return _listRecipients;
    }

    /**
     * Sets the ids of the admin users the post is restricted to
     *
     * @param listRecipients
     *            the admin user ids, empty to send the post to all
     */
    public void setRecipients( List<Integer> listRecipients )
    {
        _listRecipients = ( listRecipients != null ) ? listRecipients : new ArrayList<>( );
    }

    /**
     * Returns the display names of the recipients, filled for display only
     *
     * @return the display names
     */
    public List<String> getRecipientNames( )
    {
        return _listRecipientNames;
    }

    /**
     * Sets the display names of the recipients
     *
     * @param listRecipientNames
     *            the display names
     */
    public void setRecipientNames( List<String> listRecipientNames )
    {
        _listRecipientNames = ( listRecipientNames != null ) ? listRecipientNames : new ArrayList<>( );
    }

    /**
     * Tells whether the post is restricted to some admin users
     *
     * @return true when the post has recipients, false when it is sent to all
     */
    public boolean isPrivate( )
    {
        return !_listRecipients.isEmpty( );
    }

    /**
     * Tells whether the post is new for the user who reads the wall
     *
     * @return true when the post is new, false when it is read or written by that user
     */
    public boolean isUnread( )
    {
        return _bUnread;
    }

    /**
     * Sets whether the post is new for the user who reads the wall
     *
     * @param bUnread
     *            true when the post is new for that user
     */
    public void setUnread( boolean bUnread )
    {
        _bUnread = bUnread;
    }

    /**
     * Returns the importance of the post
     *
     * @return {@link #IMPORTANCE_HIGH}, {@link #IMPORTANCE_MEDIUM} or {@link #IMPORTANCE_STANDARD}
     */
    public String getImportance( )
    {
        return _strImportance;
    }

    /**
     * Sets the importance of the post; an empty value is the standard importance
     *
     * @param strImportance
     *            {@link #IMPORTANCE_HIGH}, {@link #IMPORTANCE_MEDIUM} or {@link #IMPORTANCE_STANDARD}
     */
    public void setImportance( String strImportance )
    {
        _strImportance = ( strImportance == null || strImportance.isBlank( ) ) ? IMPORTANCE_STANDARD : strImportance;
    }
}
