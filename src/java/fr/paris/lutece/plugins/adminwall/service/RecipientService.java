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
package fr.paris.lutece.plugins.adminwall.service;

import fr.paris.lutece.plugins.adminwall.business.Post;
import fr.paris.lutece.plugins.adminwall.business.RecipientHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.business.rbac.RBACHome;
import fr.paris.lutece.portal.business.user.AdminUserHome;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Recipients of a post: a post with no recipient is sent to all, a post with recipients is shown only to its author and
 * to them
 */
public final class RecipientService
{
    /** Minimum number of characters before a search for recipients */
    public static final int SEARCH_MIN_LENGTH = 2;

    private static final int SEARCH_MAX_RESULTS = 10;
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";

    /**
     * Private constructor - this class need not be instantiated
     */
    private RecipientService( )
    {
    }

    /**
     * Keep the posts the user may read, and set the recipients of each of them
     *
     * @param posts
     *            the posts, in display order
     * @param nIdUser
     *            the id of the admin user who reads the wall
     * @return the posts sent to all, written by the user, or sent to the user, in the same order
     */
    public static List<Post> filterVisiblePosts( Collection<Post> posts, int nIdUser )
    {
        Map<Integer, List<Integer>> mapRecipients = RecipientHome.getRecipientsMap( );
        List<Post> listVisible = new ArrayList<>( );

        for ( Post post : posts )
        {
            List<Integer> listRecipients = mapRecipients.getOrDefault( post.getIdPost( ), Collections.emptyList( ) );
            post.setRecipients( new ArrayList<>( listRecipients ) );

            if ( isVisible( post, nIdUser ) )
            {
                listVisible.add( post );
            }
        }

        return listVisible;
    }

    /**
     * Tells whether a user may read a post whose recipients are set
     *
     * @param post
     *            the post
     * @param nIdUser
     *            the admin user id
     * @return true when the post is sent to all, written by the user, or sent to the user
     */
    public static boolean isVisible( Post post, int nIdUser )
    {
        return !post.isPrivate( ) || ( post.getIdAuteur( ) == nIdUser ) || post.getRecipients( ).contains( nIdUser );
    }

    /**
     * Fill the display names of the recipients of the posts
     *
     * @param posts
     *            the posts whose recipients are set
     */
    public static void fillRecipientNames( Collection<Post> posts )
    {
        Map<Integer, String> mapNames = new HashMap<>( );

        for ( Post post : posts )
        {
            List<String> listNames = new ArrayList<>( );

            for ( Integer nIdUser : post.getRecipients( ) )
            {
                String strName = mapNames.computeIfAbsent( nIdUser, id -> {
                    AdminUser user = AdminUserHome.findByPrimaryKey( id );
                    return ( user != null ) ? getDisplayName( user ) : StringUtils.EMPTY;
                } );

                if ( StringUtils.isNotBlank( strName ) )
                {
                    listNames.add( strName );
                }
            }

            post.setRecipientNames( listNames );
        }
    }

    /**
     * Search the admin users a post can be sent to
     *
     * @param strSearch
     *            the words typed, matched against the first name, last name, login and email
     * @param nIdCurrentUser
     *            the author, never proposed
     * @param strRight
     *            the right needed to read the wall
     * @return at most 10 users, as maps with an id, a name and an email; empty below 2 characters
     */
    public static List<Map<String, Object>> searchRecipients( String strSearch, int nIdCurrentUser, String strRight )
    {
        String strTerm = normalize( strSearch );

        if ( strTerm.length( ) < SEARCH_MIN_LENGTH )
        {
            return new ArrayList<>( );
        }

        return getEligibleUsers( strRight ).stream( ).filter( user -> user.getUserId( ) != nIdCurrentUser ).filter( user -> matches( user, strTerm ) )
                .sorted( Comparator.comparing( ( AdminUser user ) -> normalize( user.getLastName( ) ) ).thenComparing( user -> normalize( user.getFirstName( ) ) ) )
                .limit( SEARCH_MAX_RESULTS ).map( RecipientService::toSuggestion ).collect( Collectors.toList( ) );
    }

    /**
     * Keep the valid recipients among the submitted ids
     *
     * @param values
     *            the submitted ids, may be null
     * @param nIdAuthor
     *            the author, dropped from the recipients
     * @param strRight
     *            the right needed to read the wall
     * @return the distinct ids of active admin users holding the right, in the submitted order
     */
    public static List<Integer> validateRecipients( String [ ] values, int nIdAuthor, String strRight )
    {
        if ( values == null || values.length == 0 )
        {
            return new ArrayList<>( );
        }

        Set<Integer> setEligible = getEligibleUsers( strRight ).stream( ).map( AdminUser::getUserId ).collect( Collectors.toSet( ) );
        Set<Integer> setRecipients = new LinkedHashSet<>( );

        for ( String strValue : values )
        {
            int nIdUser = NumberUtils.toInt( strValue, -1 );

            if ( nIdUser != nIdAuthor && setEligible.contains( nIdUser ) )
            {
                setRecipients.add( nIdUser );
            }
        }

        return new ArrayList<>( setRecipients );
    }

    /**
     * Display name of an admin user
     *
     * @param user
     *            the admin user
     * @return the first name and the last name
     */
    public static String getDisplayName( AdminUser user )
    {
        return StringUtils.trimToEmpty( StringUtils.trimToEmpty( user.getFirstName( ) ) + " " + StringUtils.trimToEmpty( user.getLastName( ) ) );
    }

    /**
     * The users a post can be sent to: active, with the right of the wall and a role that lets them read the messages
     * ( PostResourceIdService.PERMISSION_VIEW )
     */
    private static List<AdminUser> getEligibleUsers( String strRight )
    {
        Set<Integer> setReaders = new HashSet<>( );

        for ( String strRoleKey : RBACHome.findRoleKeys( Post.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID, PostResourceIdService.PERMISSION_VIEW ) )
        {
            AdminUserHome.findByRole( strRoleKey ).forEach( user -> setReaders.add( user.getUserId( ) ) );
        }

        return AdminUserHome.findByRight( strRight ).stream( ).filter( AdminUser::isStatusActive ).filter( user -> setReaders.contains( user.getUserId( ) ) )
                .collect( Collectors.toList( ) );
    }

    private static boolean matches( AdminUser user, String strTerm )
    {
        return normalize( user.getFirstName( ) + " " + user.getLastName( ) ).contains( strTerm )
                || normalize( user.getLastName( ) + " " + user.getFirstName( ) ).contains( strTerm ) || normalize( user.getAccessCode( ) ).contains( strTerm )
                || normalize( user.getEmail( ) ).contains( strTerm );
    }

    private static Map<String, Object> toSuggestion( AdminUser user )
    {
        Map<String, Object> mapItem = new LinkedHashMap<>( );
        mapItem.put( KEY_ID, user.getUserId( ) );
        mapItem.put( KEY_NAME, getDisplayName( user ) );
        mapItem.put( KEY_EMAIL, StringUtils.defaultString( user.getEmail( ) ) );

        return mapItem;
    }

    private static String normalize( String strValue )
    {
        return StringUtils.stripAccents( StringUtils.trimToEmpty( strValue ) ).toLowerCase( Locale.ROOT );
    }
}
