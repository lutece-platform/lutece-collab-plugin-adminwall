/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.paris.lutece.plugins.adminwall.service;

import fr.paris.lutece.plugins.adminwall.business.Hashtag;
import fr.paris.lutece.plugins.adminwall.business.HashtagHome;
import fr.paris.lutece.plugins.adminwall.business.Link;
import fr.paris.lutece.plugins.adminwall.business.LinkHome;
import fr.paris.lutece.plugins.adminwall.business.Post;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Brasya
 */
public final class AdminWallService {
    
    //Active les URL dans les posts
    public static void activateURL(Post post){
            String input_url = post.getContenu();
            String pattStr_url = "(((https?)://)?([\\w\\.-]+)\\.([a-z\\.]{2,6})([\\/\\w\\.-]*)*\\/?)";
            Pattern p_url = Pattern.compile(pattStr_url);
            Matcher m_url = p_url.matcher(input_url);
            StringBuffer bufStr_url = new StringBuffer();
            boolean flag_url = false;

            while ((flag_url = m_url.find())) {
                String lien = m_url.group(1);
                String http = m_url.group(2); 
                if(http == null){ //Detection du protocole
                    lien= "http://"+lien;
                }
                m_url.appendReplacement(bufStr_url,
                        "<a href=\""+lien+"\" alt=lien url>" + lien + "</a>");
            }

            m_url.appendTail(bufStr_url);

            String chaine_url = bufStr_url.toString();
            post.setContenu(chaine_url);
    }
    
    //Active les liens de filtrage par tag dans les posts
    public static void activateHashtag(Post post){
     String input = post.getContenu();
            String pattStr = "(#[\\w\\u00C0-\\u00FF\\u0153]+)";
            Pattern p = Pattern.compile(pattStr);
            Matcher m = p.matcher(input);
            StringBuffer bufStr = new StringBuffer();
            boolean flag = false;

            while ((flag = m.find())) {
                String tag = m.group().replace("#", "");
                String mot = m.group();
                m.appendReplacement(bufStr,
                        "<a href=\"jsp/admin/plugins/adminwall/ManageWall.jsp?view=managePosts&tag=" + tag
                        + "\">" + mot + "</a>");
            }
            m.appendTail(bufStr);

            String chaine = bufStr.toString();
            post.setContenu(chaine);
    }
    
    //Detecte les Hashtags dans le post et insert dans la DB
    public static void detectHashtag(Post post){
        Pattern p = Pattern.compile("#[\\w\\u00C0-\\u00FF\\u0153]+");
        Matcher m = p.matcher(post.getContenu());

        while (m.find()) {
            String[] tab = new String[2];
            tab = m.group().split("#", 2);
            Hashtag hashtag = new Hashtag();
            hashtag.setTag(tab[1]);
            HashtagHome.create(hashtag);

            /*Insertion du Link*/
            Link link = new Link();
            link.setPost(post.getIdPost());
            link.setHashtag(hashtag.getIdHashtag());
            LinkHome.create(link);

            link = null;
            hashtag = null;
        }
    }
    
}
