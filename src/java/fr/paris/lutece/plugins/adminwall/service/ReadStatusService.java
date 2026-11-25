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
import fr.paris.lutece.plugins.adminwall.business.PostHome;
import fr.paris.lutece.plugins.adminwall.business.PostReadHome;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Status of a post for each admin user: "new" until the user marks it as read. A user's own posts are always read for
 * them.
 */
public final class ReadStatusService
{
    /** The status of a post the user has not marked as read */
    public static final String STATUS_NEW = "new";

    /** The status of a post the user has marked as read, or written */
    public static final String STATUS_READ = "read";

    /**
     * Private constructor - this class need not be instantiated
     */
    private ReadStatusService( )
    {
    }

    /**
     * Set the status of the posts for a user
     *
     * @param posts
     *            the posts
     * @param nIdUser
     *            the id of the admin user who reads the wall
     */
    public static void fillStatus( Collection<Post> posts, int nIdUser )
    {
        Set<Integer> setRead = PostReadHome.getReadPostIds( nIdUser );

        for ( Post post : posts )
        {
            post.setUnread( post.getIdAuteur( ) != nIdUser && !setRead.contains( post.getIdPost( ) ) );
        }
    }

    /**
     * Keep the posts of a status; an unknown or empty status keeps them all
     *
     * @param posts
     *            the posts whose status is set
     * @param strStatus
     *            {@link #STATUS_NEW}, {@link #STATUS_READ} or anything else for all
     * @return the posts of that status, in the same order
     */
    public static List<Post> filterByStatus( List<Post> posts, String strStatus )
    {
        if ( STATUS_NEW.equals( strStatus ) )
        {
            return posts.stream( ).filter( Post::isUnread ).collect( Collectors.toList( ) );
        }

        if ( STATUS_READ.equals( strStatus ) )
        {
            return posts.stream( ).filter( post -> !post.isUnread( ) ).collect( Collectors.toList( ) );
        }

        return posts;
    }

    /**
     * Returns the posts that are new for a user: visible to the user, not written by the user, not marked as read
     *
     * @param nIdUser
     *            the admin user id
     * @return the new posts, the most recent first
     */
    public static List<Post> getNewPosts( int nIdUser )
    {
        List<Post> listPosts = RecipientService.filterVisiblePosts( PostHome.getPostsList( ), nIdUser );
        fillStatus( listPosts, nIdUser );

        return filterByStatus( listPosts, STATUS_NEW );
    }

    /**
     * Mark a post as read by a user
     *
     * @param post
     *            a post visible to the user
     * @param nIdUser
     *            the admin user id
     */
    public static void markRead( Post post, int nIdUser )
    {
        PostReadHome.markRead( List.of( post.getIdPost( ) ), nIdUser );
    }

    /**
     * Mark every post that is new for a user as read
     *
     * @param nIdUser
     *            the admin user id
     * @return the number of posts marked
     */
    public static int markAllRead( int nIdUser )
    {
        List<Integer> listIds = getNewPosts( nIdUser ).stream( ).map( Post::getIdPost ).collect( Collectors.toList( ) );
        PostReadHome.markRead( listIds, nIdUser );

        return listIds.size( );
    }
}
