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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.paris.lutece.test.LuteceTestCase;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;


/**
 * This is the business class test for the object Post
 */
public class PostBusinessTest extends LuteceTestCase
{
    private static final String CONTENU1 = "Contenu1";
    private static final String CONTENU2 = "Contenu2";
    private static final Timestamp TIMESTAMP1 = new Timestamp( 1000000L );
    private static final Timestamp TIMESTAMP2 = new Timestamp( 2000000L );
    private static final String AUTEUR1 = "Auteur1";
    private static final String AUTEUR2 = "Auteur2";
    private static final int IDAUTEUR1 = 1001;
    private static final int IDAUTEUR2 = 1002;

    /**
     * test Post
     */
    @Test
    public void testBusiness(  )
    {
        // Initialize an object
        Post post = new Post(  );
        post.setContenu( CONTENU1 );
        post.setTimestamp( TIMESTAMP1 );
        post.setAuteur( AUTEUR1 );
        post.setIdAuteur( IDAUTEUR1 );
        post.setImportance( Post.IMPORTANCE_HIGH );

        // Create test
        PostHome.create( post );

        Post postStored = PostHome.findByPrimaryKey( post.getIdPost(  ) );
        assertNotNull( postStored );
        assertEquals( post.getIdPost(  ), postStored.getIdPost(  ) );
        assertEquals( post.getContenu(  ), postStored.getContenu(  ) );
        assertEquals( post.getTimestamp(  ), postStored.getTimestamp(  ) );
        assertEquals( post.getAuteur(  ), postStored.getAuteur(  ) );
        assertEquals( post.getIdAuteur(  ), postStored.getIdAuteur(  ) );
        assertEquals( Post.IMPORTANCE_HIGH, postStored.getImportance(  ) );

        // Update test
        post.setContenu( CONTENU2 );
        post.setTimestamp( TIMESTAMP2 );
        post.setAuteur( AUTEUR2 );
        post.setIdAuteur( IDAUTEUR2 );
        post.setImportance( null );
        PostHome.update( post );
        postStored = PostHome.findByPrimaryKey( post.getIdPost(  ) );
        assertEquals( post.getIdPost(  ), postStored.getIdPost(  ) );
        assertEquals( post.getContenu(  ), postStored.getContenu(  ) );
        assertEquals( post.getTimestamp(  ), postStored.getTimestamp(  ) );
        assertEquals( post.getAuteur(  ), postStored.getAuteur(  ) );
        assertEquals( post.getIdAuteur(  ), postStored.getIdAuteur(  ) );
        assertEquals( Post.IMPORTANCE_STANDARD, postStored.getImportance(  ) );

        // List tests
        assertTrue( PostHome.getPostsList(  ).stream(  ).anyMatch( p -> p.getIdPost(  ) == post.getIdPost(  ) ) );
        assertTrue( PostHome.getPostsListIdAuteur( IDAUTEUR2 ).stream(  ).anyMatch( p -> p.getIdPost(  ) == post.getIdPost(  ) ) );

        // Delete test
        PostHome.remove( post.getIdPost(  ) );
        postStored = PostHome.findByPrimaryKey( post.getIdPost(  ) );
        assertNull( postStored );
    }
}
