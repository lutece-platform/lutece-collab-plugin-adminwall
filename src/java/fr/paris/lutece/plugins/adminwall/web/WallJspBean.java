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
package fr.paris.lutece.plugins.adminwall.web;

import fr.paris.lutece.plugins.adminwall.business.Hashtag;
import fr.paris.lutece.plugins.adminwall.business.HashtagHome;
import fr.paris.lutece.plugins.adminwall.business.Link;
import fr.paris.lutece.plugins.adminwall.business.LinkHome;
import fr.paris.lutece.plugins.adminwall.business.Post;
import fr.paris.lutece.plugins.adminwall.business.PostHome;
import fr.paris.lutece.plugins.adminwall.service.AdminWallService;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.*;

import javax.servlet.http.HttpServletRequest;

/**
 * This class provides the user interface to manage Wall features ( manage,
 * create, modify, remove )
 */
@Controller(controllerJsp = "ManageWall.jsp", controllerPath = "jsp/admin/plugins/adminwall/", right = "ADMINWALL_ACCESS")
public class WallJspBean extends AdminWallJspBean {

    ////////////////////////////////////////////////////////////////////////////
    // Constants
    // templates
    private static final String TEMPLATE_MANAGE_POST = "/admin/plugins/adminwall/manage_posts.html";
    private static final String TEMPLATE_CREATE_POST = "/admin/plugins/adminwall/create_post.html";

    // Parameters
    private static final String PARAMETER_ID_POST = "id_post";
    private static final String PARAMETER_TAG = "tag";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_POSTS = "adminwall.manage_posts.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_POST = "adminwall.create_post.pageTitle";

    // Markers
    private static final String MARK_POST_LIST = "post_list";
    private static final String MARK_POST = "post";
    private static final String MARK_USER_ID = "user_id";
    private static final String JSP_MANAGE_WALL = "jsp/admin/plugins/adminwall/ManageWall.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_POST = "adminwall.message.confirmRemovePost";
    private static final String PROPERTY_DEFAULT_LIST_POST_PER_PAGE = "adminwall.listPosts.itemsPerPage";
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "adminwall.model.entity.post.attribute.";

    // Views
    private static final String VIEW_MANAGE_POSTS = "managePosts";
    private static final String VIEW_CREATE_POST = "createPost";

    // Actions
    private static final String ACTION_CREATE_POST = "createPost";
    private static final String ACTION_REMOVE_POST = "removePost";
    private static final String ACTION_CONFIRM_REMOVE_POST = "confirmRemovePost";

    // Infos
    private static final String INFO_POST_CREATED = "adminwall.info.post.created";
    private static final String INFO_POST_REMOVED = "adminwall.info.post.removed";

    // Session variables to store working values
    private Post _post;
    private Hashtag _hashtag;
    private Link _link;

    @View(value = VIEW_MANAGE_POSTS, defaultView = true)
    public String getManagePosts(HttpServletRequest request) {

        Collection<Post> collectionPosts = new ArrayList<Post>();
        List<Post> listPosts = (List<Post>) collectionPosts;

        //PAGINATOR
        _strCurrentPageIndex = Paginator.getPageIndex(request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex);
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt(PROPERTY_DEFAULT_LIST_POST_PER_PAGE, 50);
        _nItemsPerPage = Paginator.getItemsPerPage(request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage);

        //URL
        UrlItem url = new UrlItem(JSP_MANAGE_WALL);
        String strUrl = url.getUrl();

        String param_tag = request.getParameter(PARAMETER_TAG);

        if (param_tag == null) {//SANS PARAMETRE/FILTRAGE
            listPosts = (List<Post>) PostHome.getPostsList();
        } else {//AVEC PARAMETRE/FILTRAGE
            int id_hashtag = HashtagHome.getId(param_tag);
            List<Link> listLinks = (List<Link>) LinkHome.getLinksListTag(id_hashtag);

            //Creation de la liste de la liste de Posts avec la liste de Links
            for (Link link : listLinks) {
                int id_post = link.getPost();
                Post pos = PostHome.findByPrimaryKey(id_post);
                listPosts.add(pos);
            }
            Collections.reverse(listPosts);
        }

        
        for (Post pos : listPosts) {
            
           //Insertion des URL
           AdminWallService.activateURL(pos);
           //Insertion des liens de filtrages sur les hashtags
           AdminWallService.activateHashtag(pos);
            
        }

        // PAGINATOR
        LocalizedPaginator paginator = new LocalizedPaginator(listPosts, _nItemsPerPage, strUrl, PARAMETER_PAGE_INDEX,
                _strCurrentPageIndex, getLocale());
        //Infos User
        AdminUser currentUser = AdminUserService.getAdminUser(request);
        int user_id = currentUser.getUserId();
        //Model
        Map<String, Object> model = getModel();
        model.put(MARK_USER_ID, user_id);
        model.put(MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage);
        model.put(MARK_PAGINATOR, paginator);
        model.put(MARK_POST_LIST, paginator.getPageItems());

        return getPage(PROPERTY_PAGE_TITLE_MANAGE_POSTS, TEMPLATE_MANAGE_POST, model);
    }

    /**
     * Returns the form to create a post
     *
     * @param request The Http request
     * @return the html code of the post form
     */
    @View(VIEW_CREATE_POST)
    public String getCreatePost(HttpServletRequest request) {
        _post = (_post != null) ? _post : new Post();

        Map<String, Object> model = getModel();
        model.put(MARK_POST, _post);

        return getPage(PROPERTY_PAGE_TITLE_CREATE_POST, TEMPLATE_CREATE_POST, model);
    }

    /**
     * Process the data capture form of a new post
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     */
    @Action(ACTION_CREATE_POST)
    public String doCreatePost(HttpServletRequest request) {
        _post = (_post != null) ? _post : new Post();

        /*Date Automatique*/
        Date dDate = new Date();
        Timestamp time= new Timestamp(dDate.getTime());
        _post.setTimestamp(time);
        
        
        
        
        /*Auteur Automatique*/
        AdminUser currentUser = AdminUserService.getAdminUser(request);

        String prenom = currentUser.getFirstName();
        String nom = currentUser.getLastName();
        _post.setAuteur(prenom + " " + nom);

        populate(_post, request);

        // Check constraints
        if (!validateBean(_post, VALIDATION_ATTRIBUTES_PREFIX)) {
            return redirectView(request, VIEW_MANAGE_POSTS);
        }

        PostHome.create(_post);

        /*Detection Hashtags+ajout BDD*/
        AdminWallService.detectHashtag(_post);

        _post = null;
        addInfo(INFO_POST_CREATED, getLocale());

        return redirectView(request, VIEW_MANAGE_POSTS);
    }

    /**
     * Manages the removal form of a post whose identifier is in the http
     * request
     *
     * @param request The Http request
     * @return the html code to confirm
     */
    @Action(ACTION_CONFIRM_REMOVE_POST)
    public String getConfirmRemovePost(HttpServletRequest request) {
        int nId = Integer.parseInt(request.getParameter(PARAMETER_ID_POST));
        UrlItem url = new UrlItem(getActionUrl(ACTION_REMOVE_POST));
        url.addParameter(PARAMETER_ID_POST, nId);

        String strMessageUrl = AdminMessageService.getMessageUrl(request, MESSAGE_CONFIRM_REMOVE_POST, url.getUrl(),
                AdminMessage.TYPE_CONFIRMATION);

        return redirect(request, strMessageUrl);
    }

    /**
     * Handles the removal form of a post
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage posts
     */
    @Action(ACTION_REMOVE_POST)
    public String doRemovePost(HttpServletRequest request) {
        int nId = Integer.parseInt(request.getParameter(PARAMETER_ID_POST));
        PostHome.remove(nId);
        LinkHome.remove(nId);
        addInfo(INFO_POST_REMOVED, getLocale());

        return redirectView(request, VIEW_MANAGE_POSTS);
    }

}
