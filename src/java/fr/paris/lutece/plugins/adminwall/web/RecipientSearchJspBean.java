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

import fr.paris.lutece.plugins.adminwall.service.PostResourceIdService;
import fr.paris.lutece.plugins.adminwall.service.RecipientService;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.ResponseBody;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * JSON endpoint of the recipient autocomplete of a new post. It has its own controller, bound to the header-less
 * SearchRecipients.jsp: a @ResponseBody view of the wall controller would also answer through ManageWall.jsp, which
 * writes the admin page around it.
 */
@RequestScoped
@Named( "adminwallRecipientSearchJspBean" )
@Controller( controllerJsp = RecipientSearchJspBean.CONTROLLER_JSP, controllerPath = WallJspBean.CONTROLLER_PATH, right = AdminWallJspBean.RIGHT_AdminWall, securityTokenEnabled = true )
public class RecipientSearchJspBean extends AdminWallJspBean
{
    static final String CONTROLLER_JSP = "SearchRecipients.jsp";

    // Parameters
    private static final String PARAMETER_SEARCH = "q";

    // Markers
    private static final String MARK_ITEMS = "items";

    // Views
    private static final String VIEW_SEARCH_RECIPIENTS = "searchRecipients";

    /**
     * Returns the admin users a post can be sent to, for the recipient autocomplete
     *
     * @param request
     *            The Http request
     * @return a map whose "items" entry lists at most 10 users (id, name, email)
     * @throws AccessDeniedException
     *             if the user may not write a message
     */
    @View( VIEW_SEARCH_RECIPIENTS )
    @ResponseBody
    public Map<String, Object> getSearchRecipients( HttpServletRequest request ) throws AccessDeniedException
    {
        AdminUser currentUser = AdminUserService.getAdminUser( request );

        if ( !PostResourceIdService.isAuthorized( currentUser, PostResourceIdService.PERMISSION_CREATE ) )
        {
            throw new AccessDeniedException( "AdminWall: permission denied" );
        }

        List<Map<String, Object>> listUsers = RecipientService.searchRecipients( request.getParameter( PARAMETER_SEARCH ), currentUser.getUserId( ),
                RIGHT_AdminWall );

        return Map.of( MARK_ITEMS, listUsers );
    }
}
