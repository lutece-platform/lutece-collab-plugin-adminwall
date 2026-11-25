/*
 * Copyright (c) 2002-2026, Mairie de Paris
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.paris.lutece.plugins.adminwall.service.ReadStatusService;
import fr.paris.lutece.test.LuteceTestCase;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

/**
 * Test of the new / read status of the posts
 */
public class PostReadBusinessTest extends LuteceTestCase
{
    private static final int ID_AUTHOR = 3001;
    private static final int ID_READER = 3002;

    /**
     * A post is new for a reader until marked as read, never new for its author, and its read marks go with it
     */
    @Test
    public void testReadStatus( )
    {
        Post post1 = createPost( "first" );
        Post post2 = createPost( "second" );
        List<Post> listPosts = Arrays.asList( post1, post2 );

        int nNewBefore = ReadStatusService.getNewPosts( ID_READER ).size( );

        ReadStatusService.fillStatus( listPosts, ID_AUTHOR );
        assertFalse( post1.isUnread( ) );
        assertFalse( post2.isUnread( ) );

        ReadStatusService.fillStatus( listPosts, ID_READER );
        assertTrue( post1.isUnread( ) );
        assertTrue( post2.isUnread( ) );

        ReadStatusService.markRead( post1, ID_READER );
        ReadStatusService.markRead( post1, ID_READER );
        ReadStatusService.fillStatus( listPosts, ID_READER );
        assertFalse( post1.isUnread( ) );
        assertTrue( post2.isUnread( ) );
        assertEquals( List.of( post2 ), ReadStatusService.filterByStatus( listPosts, ReadStatusService.STATUS_NEW ) );
        assertEquals( List.of( post1 ), ReadStatusService.filterByStatus( listPosts, ReadStatusService.STATUS_READ ) );
        assertEquals( 2, ReadStatusService.filterByStatus( listPosts, "other" ).size( ) );

        assertEquals( nNewBefore - 1, ReadStatusService.getNewPosts( ID_READER ).size( ) );
        ReadStatusService.markAllRead( ID_READER );
        assertTrue( ReadStatusService.getNewPosts( ID_READER ).isEmpty( ) );

        PostReadHome.removeByPost( post1.getIdPost( ) );
        assertFalse( PostReadHome.getReadPostIds( ID_READER ).contains( post1.getIdPost( ) ) );

        PostHome.remove( post1.getIdPost( ) );
        PostHome.remove( post2.getIdPost( ) );
    }

    private static Post createPost( String strContent )
    {
        Post post = new Post( );
        post.setContenu( strContent );
        post.setTimestamp( new Timestamp( System.currentTimeMillis( ) ) );
        post.setAuteur( "Author" );
        post.setIdAuteur( ID_AUTHOR );
        PostHome.create( post );

        return post;
    }
}
