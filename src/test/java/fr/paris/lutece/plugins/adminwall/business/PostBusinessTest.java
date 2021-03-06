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

import fr.paris.lutece.test.LuteceTestCase;

import java.sql.Timestamp;


public class PostBusinessTest extends LuteceTestCase
{
    private final static int IDPOST1 = 1;
    private final static int IDPOST2 = 2;
    private final static String CONTENU1 = "Contenu1";
    private final static String CONTENU2 = "Contenu2";
    private final static Timestamp TIMESTAMP1 = new Timestamp( 1000000L );
    private final static Timestamp TIMESTAMP2 = new Timestamp( 2000000L );
    private final static String AUTEUR1 = "Auteur1";
    private final static String AUTEUR2 = "Auteur2";

    public void testBusiness(  )
    {
        // Initialize an object
        Post post = new Post(  );
        post.setIdPost( IDPOST1 );
        post.setContenu( CONTENU1 );
        post.setTimestamp( TIMESTAMP1 );
        post.setAuteur( AUTEUR1 );

        // Create test
        PostHome.create( post );

        Post postStored = PostHome.findByPrimaryKey( post.getIdPost(  ) );
        assertEquals( postStored.getIdPost(  ), post.getIdPost(  ) );
        assertEquals( postStored.getContenu(  ), post.getContenu(  ) );
        assertEquals( postStored.getTimestamp(  ), post.getTimestamp(  ) );
        assertEquals( postStored.getAuteur(  ), post.getAuteur(  ) );

        // Update test
        post.setIdPost( IDPOST2 );
        post.setContenu( CONTENU2 );
        post.setTimestamp( TIMESTAMP2 );
        post.setAuteur( AUTEUR2 );
        PostHome.update( post );
        postStored = PostHome.findByPrimaryKey( post.getIdPost(  ) );
        assertEquals( postStored.getIdPost(  ), post.getIdPost(  ) );
        assertEquals( postStored.getContenu(  ), post.getContenu(  ) );
        assertEquals( postStored.getTimestamp(  ), post.getTimestamp(  ) );
        assertEquals( postStored.getAuteur(  ), post.getAuteur(  ) );

        // List test
        PostHome.getPostsList(  );

        // Delete test
        PostHome.remove( post.getIdPost(  ) );
        postStored = PostHome.findByPrimaryKey( post.getIdPost(  ) );
        assertNull( postStored );
    }
}
