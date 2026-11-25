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
package fr.paris.lutece.plugins.adminwall.web;

import fr.paris.lutece.plugins.adminwall.business.Post;
import fr.paris.lutece.plugins.adminwall.service.PostResourceIdService;
import fr.paris.lutece.plugins.adminwall.service.ReadStatusService;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.dashboard.DashboardComponent;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.security.SecurityTokenHandler;
import fr.paris.lutece.portal.service.security.ISecurityTokenService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.web.cdi.mvc.Models;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Dashboard of the notification zone of the admin header: the messages that are new for the current user. Each entry is
 * a small form posting the markRead action of the wall, so a clicked message is marked as read and leaves the menu.
 * Renders nothing when nothing is new, so the core shows neither the entry nor the badge of the bell.
 */
public class NotificationDashboardComponent extends DashboardComponent
{
    /** The token key of the markRead action of the wall (its securityTokenAction) */
    static final String TOKEN_ACTION = "createPost";

    private static final int MAX_POSTS = 5;
    private static final int MAX_TEXT_LENGTH = 80;

    private static final String TEMPLATE_DASHBOARD = "/admin/plugins/adminwall/dashboard/adminwall_notification.html";

    private static final String MARK_POST_LIST = "post_list";
    private static final String MARK_NEW_COUNT = "new_count";
    private static final String MARK_LABEL = "label";
    private static final String MARK_CAN_CREATE = "can_create";
    private static final String KEY_ID = "id";
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_TEXT = "text";
    private static final String KEY_IMPORTANCE = "importance";

    private static final String MESSAGE_LABEL_ONE = "adminwall.notification.labelOne";
    private static final String MESSAGE_LABEL_MANY = "adminwall.notification.labelMany";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDashboardData( AdminUser user, HttpServletRequest request )
    {
        // nothing for the users who may not read the messages
        if ( !PostResourceIdService.isAuthorized( user, PostResourceIdService.PERMISSION_VIEW ) )
        {
            return StringUtils.EMPTY;
        }

        List<Post> listNew = ReadStatusService.getNewPosts( user.getUserId( ) );

        if ( listNew.isEmpty( ) )
        {
            return StringUtils.EMPTY;
        }

        List<Map<String, Object>> listItems = new ArrayList<>( );

        for ( Post post : listNew.subList( 0, Math.min( MAX_POSTS, listNew.size( ) ) ) )
        {
            Map<String, Object> mapItem = new LinkedHashMap<>( );
            mapItem.put( KEY_ID, post.getIdPost( ) );
            mapItem.put( KEY_AUTHOR, StringUtils.defaultString( post.getAuteur( ) ) );
            mapItem.put( KEY_IMPORTANCE, post.getImportance( ) );
            mapItem.put( KEY_TEXT, StringUtils.abbreviate( StringUtils.defaultString( post.getContenu( ) ), MAX_TEXT_LENGTH ) );
            listItems.add( mapItem );
        }

        int nCount = listNew.size( );
        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_POST_LIST, listItems );
        model.put( MARK_NEW_COUNT, nCount );
        model.put( MARK_CAN_CREATE, PostResourceIdService.isAuthorized( user, PostResourceIdService.PERMISSION_CREATE ) );
        model.put( MARK_LABEL, I18nService.getLocalizedString( ( nCount == 1 ) ? MESSAGE_LABEL_ONE : MESSAGE_LABEL_MANY, new Object [ ] {
                nCount
        }, user.getLocale( ) ) );
        // the token of the forms : the template service writes it in each of them
        model.put( SecurityTokenHandler.MARK_CSRF_TOKEN, getMarkReadToken( request ) );

        return AppTemplateService.getTemplate( TEMPLATE_DASHBOARD, user.getLocale( ), model ).getHtml( );
    }

    /**
     * Returns a token accepted by the markRead action of the wall. The token service also writes it in the models of
     * the request, which the page being rendered may use for its own forms: their value is put back.
     *
     * @param request
     *            The Http request
     * @return the token
     */
    private static String getMarkReadToken( HttpServletRequest request )
    {
        Instance<Models> instanceModels = CDI.current( ).select( Models.class );
        Models models = instanceModels.isResolvable( ) ? instanceModels.get( ) : null;
        Object previousToken = ( models != null ) ? models.get( SecurityTokenHandler.MARK_CSRF_TOKEN ) : null;

        String strToken = CDI.current( ).select( ISecurityTokenService.class ).get( ).getToken( request, TOKEN_ACTION );

        if ( models != null )
        {
            models.put( SecurityTokenHandler.MARK_CSRF_TOKEN, previousToken );
        }

        return strToken;
    }
}
