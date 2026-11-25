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
package fr.paris.lutece.plugins.adminwall.service;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.adminwall.business.Post;
import fr.paris.lutece.plugins.adminwall.business.PostHome;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.service.rbac.Permission;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.rbac.ResourceIdService;
import fr.paris.lutece.portal.service.rbac.ResourceType;
import fr.paris.lutece.portal.service.rbac.ResourceTypeManager;
import fr.paris.lutece.util.ReferenceList;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Locale;

/**
 * RBAC of the messages of the wall. The permissions apply to every message ( the resource id is always the wildcard ):
 * VIEW reads the wall, its dashboards and its notifications, CREATE writes a message, DELETE removes any message the
 * user can read ( a moderator ); the author of a message removes it without that permission.
 */
public class PostResourceIdService extends ResourceIdService
{
    /** Read the messages */
    public static final String PERMISSION_VIEW = "VIEW";

    /** Write a message */
    public static final String PERMISSION_CREATE = "CREATE";

    /** Remove any message, not only one's own */
    public static final String PERMISSION_DELETE = "DELETE";

    private static final String PLUGIN_NAME = "adminwall";
    private static final String PROPERTY_LABEL_RESOURCE_TYPE = "adminwall.permission.resourceType.post.label";
    private static final String PROPERTY_LABEL_VIEW = "adminwall.permission.resourceType.post.view";
    private static final String PROPERTY_LABEL_CREATE = "adminwall.permission.resourceType.post.create";
    private static final String PROPERTY_LABEL_DELETE = "adminwall.permission.resourceType.post.delete";
    private static final int TITLE_MAX_LENGTH = 50;

    /**
     * Constructor
     */
    public PostResourceIdService( )
    {
        setPluginName( PLUGIN_NAME );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void register( )
    {
        ResourceType rt = new ResourceType( );
        rt.setResourceIdServiceClass( PostResourceIdService.class.getName( ) );
        rt.setPluginName( PLUGIN_NAME );
        rt.setResourceTypeKey( Post.RESOURCE_TYPE );
        rt.setResourceTypeLabelKey( PROPERTY_LABEL_RESOURCE_TYPE );

        registerPermission( rt, PERMISSION_VIEW, PROPERTY_LABEL_VIEW );
        registerPermission( rt, PERMISSION_CREATE, PROPERTY_LABEL_CREATE );
        registerPermission( rt, PERMISSION_DELETE, PROPERTY_LABEL_DELETE );

        ResourceTypeManager.registerResourceType( rt );
    }

    /**
     * The permissions are given on every message at once: no message is listed, the role screen then only offers the
     * wildcard
     *
     * @param locale
     *            The locale
     * @return an empty list
     */
    @Override
    public ReferenceList getResourceIdList( Locale locale )
    {
        return new ReferenceList( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle( String strId, Locale locale )
    {
        Post post = PostHome.findByPrimaryKey( NumberUtils.toInt( strId, -1 ) );

        return ( post != null ) ? StringUtils.abbreviate( StringUtils.defaultString( post.getContenu( ) ), TITLE_MAX_LENGTH ) : StringUtils.EMPTY;
    }

    /**
     * Tells whether a user has a permission on the messages of the wall
     *
     * @param user
     *            The user, may be null
     * @param strPermission
     *            One of the PERMISSION_* constants
     * @return true if one of the roles of the user grants the permission
     */
    public static boolean isAuthorized( User user, String strPermission )
    {
        return ( user != null ) && RBACService.isAuthorized( Post.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID, strPermission, user );
    }

    private static void registerPermission( ResourceType rt, String strKey, String strTitleKey )
    {
        Permission permission = new Permission( );
        permission.setPermissionKey( strKey );
        permission.setPermissionTitleKey( strTitleKey );
        rt.registerPermission( permission );
    }
}
