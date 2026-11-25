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
package fr.paris.lutece.plugins.adminwall.web;

import fr.paris.lutece.plugins.adminwall.business.HashtagHome;
import fr.paris.lutece.plugins.adminwall.business.Link;
import fr.paris.lutece.plugins.adminwall.business.LinkHome;
import fr.paris.lutece.plugins.adminwall.business.Post;
import fr.paris.lutece.plugins.adminwall.business.PostHome;
import fr.paris.lutece.plugins.adminwall.business.PostReadHome;
import fr.paris.lutece.plugins.adminwall.business.RecipientHome;
import fr.paris.lutece.plugins.adminwall.service.AdminWallService;
import fr.paris.lutece.plugins.adminwall.service.PostResourceIdService;
import fr.paris.lutece.plugins.adminwall.service.ReadStatusService;
import fr.paris.lutece.plugins.adminwall.service.RecipientService;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import fr.paris.lutece.portal.web.util.IPager;
import fr.paris.lutece.portal.web.util.Pager;
import fr.paris.lutece.util.url.UrlItem;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * This class provides the user interface to manage Wall features ( manage,
 * create, modify, remove )
 */
@RequestScoped
@Named( "adminwallWallJspBean" )
@Controller( controllerJsp = WallJspBean.CONTROLLER_JSP, controllerPath = WallJspBean.CONTROLLER_PATH, right = AdminWallJspBean.RIGHT_AdminWall, securityTokenEnabled = true )
public class WallJspBean extends AdminWallJspBean
{
    ////////////////////////////////////////////////////////////////////////////
    // Constants
    static final String CONTROLLER_JSP = "ManageWall.jsp";
    static final String CONTROLLER_PATH = "jsp/admin/plugins/adminwall/";

    // templates
    private static final String TEMPLATE_MANAGE_POST = "/admin/plugins/adminwall/manage_posts.html";

    // Parameters
    private static final String PARAMETER_ID_POST = "idPost";
    private static final String PARAMETER_TAG = "tag";
    private static final String PARAMETER_ID_AUTEUR = "idAuteur";
    private static final String PARAMETER_RECIPIENT = "recipient";
    private static final String PARAMETER_RECIPIENT_MODE = "recipientMode";
    private static final String PARAMETER_STATUS = "status";
    private static final String PARAMETER_NEW_POST = "newPost";
    private static final String VALUE_RECIPIENT_MODE_SELECTED = "selected";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_POSTS = "adminwall.manage_posts.pageTitle";

    // Markers
    private static final String MARK_POST_LIST = "post_list";
    private static final String MARK_USERID = "userId";
    private static final String MARK_STATUS = "status";
    private static final String MARK_NEW_COUNT = "new_count";
    private static final String MARK_FILTER_PARAMETERS = "filter_parameters";
    private static final String MARK_FILTER_TAG = "filter_tag";
    private static final String MARK_FILTER_AUTHOR = "filter_author";
    private static final String MARK_FILTER_AUTHOR_NAME = "filter_author_name";
    private static final String MARK_ALL_COUNT = "all_count";
    private static final String MARK_READ_COUNT = "read_count";
    private static final String MARK_MY_COUNT = "my_count";
    private static final String MARK_TAG_LIST = "tag_list";
    private static final String MARK_OPEN_NEW_POST = "open_new_post";
    private static final String MARK_CAN_CREATE = "can_create";
    private static final String MARK_CAN_DELETE = "can_delete";

    // Constants
    private static final int MAX_SIDEBAR_TAGS = 10;

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_POST = "adminwall.message.confirmRemovePost";
    private static final String PROPERTY_DEFAULT_LIST_POST_PER_PAGE = "adminwall.listPosts.itemsPerPage";
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "adminwall.model.entity.post.attribute.";

    // Views
    private static final String VIEW_MANAGE_POSTS = "managePosts";
    private static final String VIEW_CONFIRM_REMOVE_POST = "confirmRemovePost";

    // Actions
    private static final String ACTION_CREATE_POST = "createPost";
    private static final String ACTION_REMOVE_POST = "removePost";
    private static final String ACTION_MARK_READ = "markRead";
    private static final String ACTION_MARK_ALL_READ = "markAllRead";

    // Infos
    private static final String INFO_POST_CREATED = "adminwall.info.post.created";
    private static final String INFO_POST_REMOVED = "adminwall.info.post.removed";
    private static final String INFO_ALL_READ = "adminwall.info.post.allRead";

    // Errors
    private static final String ERROR_NO_RECIPIENT = "adminwall.message.error.noRecipient";
    private static final String ERROR_ACCESS_DENIED = "AdminWall: permission denied";

    @Inject
    @Pager( listBookmark = MARK_POST_LIST, defaultItemsPerPage = PROPERTY_DEFAULT_LIST_POST_PER_PAGE, baseUrl = CONTROLLER_PATH + CONTROLLER_JSP )
    private IPager<Post, Post> _pager;

    /**
     * Returns the page with posts management
     *
     * @param request The Http request
     * @param model The model
     * @return the html code of the page
     * @throws AccessDeniedException if the user may not read the messages
     */
    @View( value = VIEW_MANAGE_POSTS, defaultView = true, securityTokenAction = ACTION_CREATE_POST )
    public String getManagePosts( HttpServletRequest request, Models model ) throws AccessDeniedException
    {
        checkPermission( request, PostResourceIdService.PERMISSION_VIEW );

        List<Post> listPosts = new ArrayList<>(  );

        UrlItem urlPager = new UrlItem( CONTROLLER_PATH + CONTROLLER_JSP );
        urlPager.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_MANAGE_POSTS );

        //TAG
        String paramTag = request.getParameter( PARAMETER_TAG );

        if ( paramTag == null )
        { //SANS PARAMETRE/FILTRAGE
            listPosts.addAll( PostHome.getPostsList(  ) );
        }
        else
        { //AVEC PARAMETRE/FILTRAGE
            urlPager.addParameter( PARAMETER_TAG, paramTag );

            int idHashtag = HashtagHome.getId( paramTag );

            //Creation de la liste de la liste de Posts avec la liste de Links
            Set<Integer> setIdPosts = new LinkedHashSet<>(  );

            for ( Link link : LinkHome.getLinksListTag( idHashtag ) )
            {
                setIdPosts.add( link.getPost(  ) );
            }

            for ( Integer nIdPost : setIdPosts )
            {
                Post pos = PostHome.findByPrimaryKey( nIdPost );

                if ( pos != null )
                {
                    listPosts.add( pos );
                }
            }

            Collections.reverse( listPosts );
        }

        //AUTEUR
        String paramIdAuteur = request.getParameter( PARAMETER_ID_AUTEUR );

        if ( paramIdAuteur != null )
        { //AVEC PARAMETRE/FILTRAGE
            urlPager.addParameter( PARAMETER_ID_AUTEUR, paramIdAuteur );

            int nIdAuteur = NumberUtils.toInt( paramIdAuteur, -1 );
            listPosts = new ArrayList<>( PostHome.getPostsListIdAuteur( nIdAuteur ) );
        }

        // a post restricted to some recipients is only shown to its author and to them
        AdminUser currentUser = AdminUserService.getAdminUser( request );
        listPosts = RecipientService.filterVisiblePosts( listPosts, currentUser.getUserId(  ) );

        // STATUS : new until the user marks the post as read
        ReadStatusService.fillStatus( listPosts, currentUser.getUserId(  ) );

        long nNewCount = listPosts.stream(  ).filter( Post::isUnread ).count(  );
        int nAllCount = listPosts.size(  );

        // SIDEBAR : the most used hashtags of the posts the user may read, whatever the current filters
        boolean bFiltered = ( paramTag != null ) || ( paramIdAuteur != null );
        List<Post> listAllVisible = bFiltered
            ? RecipientService.filterVisiblePosts( PostHome.getPostsList(  ), currentUser.getUserId(  ) ) : listPosts;
        model.put( MARK_TAG_LIST, AdminWallService.getTopHashtags( listAllVisible, MAX_SIDEBAR_TAGS ) );
        model.put( MARK_MY_COUNT, listAllVisible.stream(  ).filter( p -> p.getIdAuteur(  ) == currentUser.getUserId(  ) ).count(  ) );
        model.put( MARK_ALL_COUNT, nAllCount );
        model.put( MARK_READ_COUNT, nAllCount - nNewCount );

        if ( paramIdAuteur != null )
        {
            AdminUser author = AdminUserHome.findByPrimaryKey( NumberUtils.toInt( paramIdAuteur, -1 ) );
            model.put( MARK_FILTER_AUTHOR_NAME, ( author != null ) ? RecipientService.getDisplayName( author ) : paramIdAuteur );
        }
        String strStatus = getStatusParameter( request );

        if ( strStatus != null )
        {
            urlPager.addParameter( PARAMETER_STATUS, strStatus );
            listPosts = ReadStatusService.filterByStatus( listPosts, strStatus );
        }

        model.put( MARK_STATUS, StringUtils.defaultString( strStatus ) );
        model.put( MARK_NEW_COUNT, nNewCount );
        model.put( MARK_FILTER_PARAMETERS, getFilterQueryString( request ) );
        // reached from the "New message" button of the notification menu : the form opens at once
        model.put( MARK_OPEN_NEW_POST, Boolean.parseBoolean( request.getParameter( PARAMETER_NEW_POST ) ) );
        model.put( MARK_FILTER_TAG, StringUtils.defaultString( paramTag ) );
        model.put( MARK_FILTER_AUTHOR, StringUtils.defaultString( paramIdAuteur ) );

        // PAGINATOR : the URL, the hashtag links and the recipient names are only built for the posts of the displayed page
        _pager.withBaseUrl( urlPager.getUrl(  ) ).withListItem( listPosts ).populateModels( request, model, WallJspBean::activateLinks,
            getLocale(  ) );

        //Infos User
        model.put( MARK_USERID, currentUser.getUserId(  ) );
        model.put( MARK_CAN_CREATE, PostResourceIdService.isAuthorized( currentUser, PostResourceIdService.PERMISSION_CREATE ) );
        model.put( MARK_CAN_DELETE, PostResourceIdService.isAuthorized( currentUser, PostResourceIdService.PERMISSION_DELETE ) );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_POSTS, TEMPLATE_MANAGE_POST, model );
    }

    /**
     * Insert the URL and the hashtag filter links in the posts
     *
     * @param listPosts The posts
     * @return the posts
     */
    private static List<Post> activateLinks( List<Post> listPosts )
    {
        for ( Post pos : listPosts )
        {
            //Insertion des URL et des liens de filtrages sur les hashtags
            AdminWallService.activateLinks( pos );
        }

        RecipientService.fillRecipientNames( listPosts );

        return listPosts;
    }

    /**
     * Process the data capture form of a new post
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException if the user may not write a message
     */
    @Action( ACTION_CREATE_POST )
    public String doCreatePost( HttpServletRequest request ) throws AccessDeniedException
    {
        checkPermission( request, PostResourceIdService.PERMISSION_CREATE );

        Post post = new Post(  );
        populate( post, request );

        /*Date Automatique*/
        post.setTimestamp( new Timestamp( System.currentTimeMillis(  ) ) );

        /*Auteur Automatique*/
        AdminUser currentUser = AdminUserService.getAdminUser( request );

        String prenom = currentUser.getFirstName(  );
        String nom = currentUser.getLastName(  );
        post.setAuteur( prenom + " " + nom );
        post.setIdAuteur( currentUser.getUserId(  ) );

        // Check constraints
        if ( !validateBean( post, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_MANAGE_POSTS );
        }

        // recipients : none means the post is sent to all
        List<Integer> listRecipients = new ArrayList<>(  );

        if ( VALUE_RECIPIENT_MODE_SELECTED.equals( request.getParameter( PARAMETER_RECIPIENT_MODE ) ) )
        {
            listRecipients = RecipientService.validateRecipients( request.getParameterValues( PARAMETER_RECIPIENT ), currentUser.getUserId(  ),
                    RIGHT_AdminWall );

            if ( listRecipients.isEmpty(  ) )
            {
                addError( ERROR_NO_RECIPIENT, getLocale(  ) );

                return redirectView( request, VIEW_MANAGE_POSTS );
            }
        }

        PostHome.create( post );
        RecipientHome.create( post.getIdPost(  ), listRecipients );

        /*Detection Hashtags+ajout BDD*/
        AdminWallService.detectHashtag( post );

        addInfo( INFO_POST_CREATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_POSTS );
    }

    /**
     * Manages the removal form of a post whose identifier is in the http
     * request
     *
     * @param request The Http request
     * @return the html code to confirm
     */
    @View( value = VIEW_CONFIRM_REMOVE_POST, securityTokenAction = ACTION_REMOVE_POST )
    public String getConfirmRemovePost( HttpServletRequest request )
    {
        Post post = findRemovablePost( request );

        if ( post == null )
        {
            return redirectView( request, VIEW_MANAGE_POSTS );
        }

        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_POST ) );
        url.addParameter( PARAMETER_ID_POST, post.getIdPost(  ) );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_POST, url.getUrl(  ),
                AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a post
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage posts
     */
    @Action( ACTION_REMOVE_POST )
    public String doRemovePost( HttpServletRequest request )
    {
        Post post = findRemovablePost( request );

        if ( post != null )
        {
            LinkHome.removeByPost( post.getIdPost(  ) );
            RecipientHome.removeByPost( post.getIdPost(  ) );
            PostReadHome.removeByPost( post.getIdPost(  ) );
            PostHome.remove( post.getIdPost(  ) );
            addInfo( INFO_POST_REMOVED, getLocale(  ) );
        }

        return redirectView( request, VIEW_MANAGE_POSTS );
    }

    /**
     * Marks as read the post whose identifier is in the request, when the current user may read it
     *
     * @param request The Http request
     * @return the jsp URL of the list, with its filters
     * @throws AccessDeniedException if the user may not read the messages
     */
    @Action( value = ACTION_MARK_READ, securityTokenAction = ACTION_CREATE_POST )
    public String doMarkRead( HttpServletRequest request ) throws AccessDeniedException
    {
        checkPermission( request, PostResourceIdService.PERMISSION_VIEW );

        AdminUser currentUser = AdminUserService.getAdminUser( request );
        Post post = PostHome.findByPrimaryKey( NumberUtils.toInt( request.getParameter( PARAMETER_ID_POST ), -1 ) );

        if ( post != null )
        {
            post.setRecipients( RecipientHome.getRecipients( post.getIdPost(  ) ) );

            if ( RecipientService.isVisible( post, currentUser.getUserId(  ) ) )
            {
                ReadStatusService.markRead( post, currentUser.getUserId(  ) );
            }
        }

        return redirectToList( request );
    }

    /**
     * Marks as read every post that is new for the current user
     *
     * @param request The Http request
     * @return the jsp URL of the list, with its filters
     * @throws AccessDeniedException if the user may not read the messages
     */
    @Action( value = ACTION_MARK_ALL_READ, securityTokenAction = ACTION_CREATE_POST )
    public String doMarkAllRead( HttpServletRequest request ) throws AccessDeniedException
    {
        checkPermission( request, PostResourceIdService.PERMISSION_VIEW );

        AdminUser currentUser = AdminUserService.getAdminUser( request );
        ReadStatusService.markAllRead( currentUser.getUserId(  ) );
        addInfo( INFO_ALL_READ, getLocale(  ) );

        return redirectToList( request );
    }

    /**
     * Redirects to the list, keeping its tag, author and status filters
     *
     * @param request The Http request
     * @return the jsp URL of the list
     */
    private String redirectToList( HttpServletRequest request )
    {
        Map<String, String> mapParameters = new LinkedHashMap<>(  );

        for ( String strParameter : new String[] { PARAMETER_TAG, PARAMETER_ID_AUTEUR } )
        {
            String strValue = request.getParameter( strParameter );

            if ( StringUtils.isNotBlank( strValue ) )
            {
                mapParameters.put( strParameter, URLEncoder.encode( strValue, StandardCharsets.UTF_8 ) );
            }
        }

        String strStatus = getStatusParameter( request );

        if ( strStatus != null )
        {
            mapParameters.put( PARAMETER_STATUS, strStatus );
        }

        return redirect( request, VIEW_MANAGE_POSTS, mapParameters );
    }

    /**
     * Returns the tag and author filters of the request as a query string, for the status filter links
     *
     * @param request The Http request
     * @return the encoded parameters, each preceded by an ampersand, or an empty string
     */
    private static String getFilterQueryString( HttpServletRequest request )
    {
        StringBuilder sbParameters = new StringBuilder(  );

        for ( String strParameter : new String[] { PARAMETER_TAG, PARAMETER_ID_AUTEUR } )
        {
            String strValue = request.getParameter( strParameter );

            if ( StringUtils.isNotBlank( strValue ) )
            {
                sbParameters.append( '&' ).append( strParameter ).append( '=' ).append( URLEncoder.encode( strValue, StandardCharsets.UTF_8 ) );
            }
        }

        return sbParameters.toString(  );
    }

    /**
     * Returns the status filter of the request
     *
     * @param request The Http request
     * @return {@link ReadStatusService#STATUS_NEW}, {@link ReadStatusService#STATUS_READ}, or null for all
     */
    private static String getStatusParameter( HttpServletRequest request )
    {
        String strStatus = request.getParameter( PARAMETER_STATUS );

        if ( ReadStatusService.STATUS_NEW.equals( strStatus ) || ReadStatusService.STATUS_READ.equals( strStatus ) )
        {
            return strStatus;
        }

        return null;
    }

    /**
     * Loads the post whose identifier is in the request, when the current user may remove it: its author, or a user
     * with the DELETE permission ( a moderator ) for a post he may read
     *
     * @param request The Http request
     * @return the post, or null when it does not exist or the user may not remove it
     */
    private static Post findRemovablePost( HttpServletRequest request )
    {
        int nId = NumberUtils.toInt( request.getParameter( PARAMETER_ID_POST ), -1 );
        Post post = PostHome.findByPrimaryKey( nId );
        AdminUser currentUser = AdminUserService.getAdminUser( request );

        if ( ( post == null ) || ( currentUser == null ) )
        {
            return null;
        }

        if ( post.getIdAuteur(  ) == currentUser.getUserId(  ) )
        {
            return post;
        }

        post.setRecipients( RecipientHome.getRecipients( post.getIdPost(  ) ) );

        if ( PostResourceIdService.isAuthorized( currentUser, PostResourceIdService.PERMISSION_DELETE ) &&
                RecipientService.isVisible( post, currentUser.getUserId(  ) ) )
        {
            return post;
        }

        return null;
    }

    /**
     * Refuses the request when the current user has not the permission on the messages
     *
     * @param request The Http request
     * @param strPermission One of the PostResourceIdService.PERMISSION_* constants
     * @throws AccessDeniedException if the user has not the permission
     */
    private static void checkPermission( HttpServletRequest request, String strPermission ) throws AccessDeniedException
    {
        if ( !PostResourceIdService.isAuthorized( AdminUserService.getAdminUser( request ), strPermission ) )
        {
            throw new AccessDeniedException( ERROR_ACCESS_DENIED );
        }
    }
}
