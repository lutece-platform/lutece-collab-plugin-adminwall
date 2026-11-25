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
package fr.paris.lutece.plugins.adminwall.business;

import fr.paris.lutece.portal.service.plugin.Plugin;

import java.util.List;
import java.util.Map;

/**
 * IRecipientDAO Interface : the admin users a post is restricted to
 */
public interface IRecipientDAO
{
    /**
     * Insert a recipient of a post
     *
     * @param nIdPost
     *            the post id
     * @param nIdUser
     *            the admin user id
     * @param plugin
     *            the plugin
     */
    void insert( int nIdPost, int nIdUser, Plugin plugin );

    /**
     * Delete all the recipients of a post
     *
     * @param nIdPost
     *            the post id
     * @param plugin
     *            the plugin
     */
    void deleteByPost( int nIdPost, Plugin plugin );

    /**
     * Load the recipients of a post
     *
     * @param nIdPost
     *            the post id
     * @param plugin
     *            the plugin
     * @return the admin user ids, empty when the post is sent to all
     */
    List<Integer> selectByPost( int nIdPost, Plugin plugin );

    /**
     * Load the recipients of every post that has some
     *
     * @param plugin
     *            the plugin
     * @return the admin user ids by post id; a post sent to all is absent
     */
    Map<Integer, List<Integer>> selectAll( Plugin plugin );
}
