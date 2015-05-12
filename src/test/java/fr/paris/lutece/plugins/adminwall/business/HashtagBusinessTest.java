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


public class HashtagBusinessTest extends LuteceTestCase
{
    private final static int IDHASHTAG1 = 1;
    private final static int IDHASHTAG2 = 2;
    private final static String TAG1 = "Tag1";
    private final static String TAG2 = "Tag2";

    public void testBusiness(  )
    {
        // Initialize an object
        Hashtag hashtag = new Hashtag(  );
        hashtag.setIdHashtag( IDHASHTAG1 );
        hashtag.setTag( TAG1 );

        // Create test
        HashtagHome.create( hashtag );

        Hashtag hashtagStored = HashtagHome.findByPrimaryKey( hashtag.getIdHashtag(  ) );
        assertEquals( hashtagStored.getIdHashtag(  ), hashtag.getIdHashtag(  ) );
        assertEquals( hashtagStored.getTag(  ), hashtag.getTag(  ) );

        // Update test
        hashtag.setIdHashtag( IDHASHTAG2 );
        hashtag.setTag( TAG2 );
        HashtagHome.update( hashtag );
        hashtagStored = HashtagHome.findByPrimaryKey( hashtag.getIdHashtag(  ) );
        assertEquals( hashtagStored.getIdHashtag(  ), hashtag.getIdHashtag(  ) );
        assertEquals( hashtagStored.getTag(  ), hashtag.getTag(  ) );

        // List test
        HashtagHome.getHashtagsList(  );

        // Delete test
        HashtagHome.remove( hashtag.getIdHashtag(  ) );
        hashtagStored = HashtagHome.findByPrimaryKey( hashtag.getIdHashtag(  ) );
        assertNull( hashtagStored );
    }
}
