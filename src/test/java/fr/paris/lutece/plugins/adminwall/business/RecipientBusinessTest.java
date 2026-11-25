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

import fr.paris.lutece.plugins.adminwall.service.RecipientService;
import fr.paris.lutece.test.LuteceTestCase;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

/**
 * Test of the recipients of a post and of the visibility they give
 */
public class RecipientBusinessTest extends LuteceTestCase
{
    private static final int ID_AUTHOR = 2001;
    private static final int ID_RECIPIENT1 = 2002;
    private static final int ID_RECIPIENT2 = 2003;
    private static final int ID_OTHER = 2004;

    /**
     * A post with recipients is stored, read back, shown to its author and recipients only, and its recipients go with it
     */
    @Test
    public void testRecipients( )
    {
        Post postPrivate = createPost( "private" );
        Post postPublic = createPost( "public" );

        RecipientHome.create( postPrivate.getIdPost( ), Arrays.asList( ID_RECIPIENT1, ID_RECIPIENT2 ) );

        assertEquals( Arrays.asList( ID_RECIPIENT1, ID_RECIPIENT2 ), RecipientHome.getRecipients( postPrivate.getIdPost( ) ) );
        assertTrue( RecipientHome.getRecipients( postPublic.getIdPost( ) ).isEmpty( ) );
        assertFalse( RecipientHome.getRecipientsMap( ).containsKey( postPublic.getIdPost( ) ) );

        List<Post> listPosts = Arrays.asList( postPrivate, postPublic );
        assertEquals( 2, RecipientService.filterVisiblePosts( listPosts, ID_AUTHOR ).size( ) );
        assertEquals( 2, RecipientService.filterVisiblePosts( listPosts, ID_RECIPIENT2 ).size( ) );

        List<Post> listForOther = RecipientService.filterVisiblePosts( listPosts, ID_OTHER );
        assertEquals( 1, listForOther.size( ) );
        assertEquals( postPublic.getIdPost( ), listForOther.get( 0 ).getIdPost( ) );

        RecipientHome.removeByPost( postPrivate.getIdPost( ) );
        assertTrue( RecipientHome.getRecipients( postPrivate.getIdPost( ) ).isEmpty( ) );

        PostHome.remove( postPrivate.getIdPost( ) );
        PostHome.remove( postPublic.getIdPost( ) );
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
