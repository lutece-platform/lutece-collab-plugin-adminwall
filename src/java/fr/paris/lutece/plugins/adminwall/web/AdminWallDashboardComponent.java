/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.paris.lutece.plugins.adminwall.web;

import fr.paris.lutece.plugins.adminwall.business.Post;
import fr.paris.lutece.plugins.adminwall.business.PostHome;
import fr.paris.lutece.plugins.adminwall.service.AdminWallService;
import fr.paris.lutece.portal.business.right.Right;
import fr.paris.lutece.portal.business.right.RightHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.dashboard.DashboardComponent;
import fr.paris.lutece.portal.service.database.AppConnectionService;
import static fr.paris.lutece.portal.service.mail.MailAttachmentCacheKeyService.MARK_URL;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Brasya
 */
public class AdminWallDashboardComponent extends DashboardComponent {
// PARAMETERS
    private static final String PARAMETER_PLUGIN_NAME = "plugin_name";

    // MARKS
    private static final String MARK_URL = "url";
    private static final String MARK_ICON = "icon";
    private static final String MARK_POST_LIST = "post_list";
private static final String MARK_USERID = "userId";     

    // TEMPLATES
    private static final String TEMPLATE_DASHBOARD = "/admin/plugins/adminwall/dashboard/adminwall_dashboard.html";
    @Override
    public String getDashboardData( AdminUser user, HttpServletRequest request )
    {
        Right right = RightHome.findByPrimaryKey( getRight(  ) );
        Plugin plugin = PluginService.getPlugin( right.getPluginName(  ) );
        
        UrlItem url = new UrlItem( right.getUrl(  ) );
        url.addParameter( PARAMETER_PLUGIN_NAME, right.getPluginName(  ) );
        
        Collection<Post> collectionPosts = new ArrayList<Post>(  );
        List<Post> listPosts = (List<Post>) collectionPosts;
        
        listPosts = (List<Post>) PostHome.getPostsList(  );
        
        for ( Post pos : listPosts )
        {
            //Insertion des URL
            AdminWallService.activateURL( pos );
            //Insertion des liens de filtrages sur les hashtags
            AdminWallService.activateHashtag( pos );
        }
        
         //Infos User
        AdminUser currentUser = AdminUserService.getAdminUser( request );
        int userId = currentUser.getUserId(  );
        
        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_USERID, userId );
        model.put( MARK_URL, url.getUrl(  ) );
        model.put( MARK_ICON, plugin.getIconUrl(  ) );
        model.put( MARK_POST_LIST, listPosts);

        HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_DASHBOARD, user.getLocale(  ), model );

        return t.getHtml(  );
    }
}