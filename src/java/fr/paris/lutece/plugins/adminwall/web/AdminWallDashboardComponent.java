/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.paris.lutece.plugins.adminwall.web;

import fr.paris.lutece.plugins.adminwall.business.Post;
import fr.paris.lutece.plugins.adminwall.business.PostHome;
import fr.paris.lutece.plugins.adminwall.service.AdminWallService;
import fr.paris.lutece.plugins.adminwall.service.PostResourceIdService;
import fr.paris.lutece.plugins.adminwall.service.RecipientService;
import fr.paris.lutece.plugins.adminwall.service.ReadStatusService;
import fr.paris.lutece.portal.business.right.Right;
import fr.paris.lutece.portal.business.right.RightHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.dashboard.DashboardComponent;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;


/**
 *
 * @author Brasya
 */
public class AdminWallDashboardComponent extends DashboardComponent
{
    // PARAMETERS
    private static final String PARAMETER_PLUGIN_NAME = "plugin_name";

    // MARKS
    private static final String MARK_URL = "url";
    private static final String MARK_POST_LIST = "post_list";

    // CONSTANTS
    private static final int MAX_POSTS = 20;

    // TEMPLATES
    private static final String TEMPLATE_DASHBOARD = "/admin/plugins/adminwall/dashboard/adminwall_dashboard.html";

    @Override
    public String getDashboardData( AdminUser user, HttpServletRequest request )
    {
        // the widget is only shown to the users who may read the messages
        if ( !PostResourceIdService.isAuthorized( user, PostResourceIdService.PERMISSION_VIEW ) )
        {
            return StringUtils.EMPTY;
        }

        Right right = RightHome.findByPrimaryKey( getRight(  ) );

        UrlItem url = new UrlItem( right.getUrl(  ) );
        url.addParameter( PARAMETER_PLUGIN_NAME, right.getPluginName(  ) );

        // the posts are sorted from the most recent : the dashboard shows the latest ones
        // a post restricted to some recipients is only shown to its author and to them
        List<Post> listPosts = RecipientService.filterVisiblePosts( PostHome.getPostsList(  ), user.getUserId(  ) ).stream(  ).limit( MAX_POSTS )
                .collect( Collectors.toList(  ) );

        for ( Post pos : listPosts )
        {
            //Insertion des URL et des liens de filtrages sur les hashtags
            AdminWallService.activateLinks( pos );
        }

        RecipientService.fillRecipientNames( listPosts );
        ReadStatusService.fillStatus( listPosts, user.getUserId(  ) );

        Map<String, Object> model = new HashMap<>(  );
        model.put( MARK_URL, url.getUrl(  ) );
        model.put( MARK_POST_LIST, listPosts );

        HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_DASHBOARD, user.getLocale(  ), model );

        return t.getHtml(  );
    }
}
