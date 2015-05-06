/*
 * Copyright (c) 2002-2013, Mairie de Paris
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

import java.sql.Timestamp;

import javax.validation.constraints.*;
import org.hibernate.validator.constraints.*;

/**
 * This is the business class for the object Post
 */
public class Post {

    // Variables declarations 
    private int _nIdPost;

    @NotEmpty(message = "#i18n{adminwall.validation.post.Contenu.notEmpty}")
    //@NotEmpty( message = "#i18n{portal.validation.message.notEmpty}" )
    //@Pattern(regexp="\\w+", message="Voila")
    @Size(max = 255, message = "#i18n{adminwall.validation.post.Contenu.size}")
    //@Size( max = 255, message = "#i18n{portal.validation.message.sizeMax}" )
    private String _strContenu;
    private Timestamp _tTime;

    // @NotEmpty( message = "#i18n{adminwall.validation.post.Auteur.notEmpty}" )
    @NotEmpty(message = "#i18n{portal.validation.message.notEmpty}")
    // @Size( max = 50 , message = "#i18n{adminwall.validation.post.Auteur.size}" ) 
    @Size(max = 50, message = "#i18n{portal.validation.message.sizeMax}")
    private String _strAuteur;

    /**
     * Returns the IdPost
     *
     * @return The IdPost
     */
    public int getIdPost() {
        return _nIdPost;
    }

    /**
     * Sets the IdPost
     *
     * @param nIdPost The IdPost
     */
    public void setIdPost(int nIdPost) {
        _nIdPost = nIdPost;
    }

    /**
     * Returns the Contenu
     *
     * @return The Contenu
     */
    public String getContenu() {
        return _strContenu;
    }

    /**
     * Sets the Contenu
     *
     * @param strContenu The Contenu
     */
    public void setContenu(String strContenu) {
        _strContenu = strContenu;
    }

    /**
     * Returns the Time
     *
     * @return The Time
     */
    public Timestamp getTimestamp() {
        return _tTime;
    }

    /**
     * Sets the Time
     *
     * @param tTime The Time
     */
    public void setTimestamp(Timestamp tTime) {
        _tTime = tTime;
    }

    /**
     * Returns the Auteur
     *
     * @return The Auteur
     */
    public String getAuteur() {
        return _strAuteur;
    }

    /**
     * Sets the Auteur
     *
     * @param strAuteur The Auteur
     */
    public void setAuteur(String strAuteur) {
        _strAuteur = strAuteur;
    }
}
