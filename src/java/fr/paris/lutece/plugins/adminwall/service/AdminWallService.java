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
public final class AdminWallService
{
    private AdminWallService(  )
    {
    }

    /**
     * Activate the URL in the post
     *
     * @param post The Post
     */
    public static void activateURL( Post post )
    {
        String inputUrl = post.getContenu(  );
        String strPatternUrl = "(((https?)://)?([\\w\\.-]+)\\.([a-z\\.]{2,6})([\\/\\w\\.-]*)*\\/?)";
        Pattern pattUrl = Pattern.compile( strPatternUrl );
        Matcher matchUrl = pattUrl.matcher( inputUrl );
        StringBuffer strBuffUrl = new StringBuffer(  );
        boolean flagUrl = false;

        while ( ( flagUrl = matchUrl.find(  ) ) )
        {
            String lien = matchUrl.group( 1 );
            String http = matchUrl.group( 2 );
            String lien_href = lien;
            if ( http == null )
            { //Detection du protocole
                lien_href = "http://" + lien;
            }

            matchUrl.appendReplacement( strBuffUrl,
                "<a href=\"" + lien_href + "\" alt=lien url target=\"_blank\">" + lien + "</a>" );
        }

        matchUrl.appendTail( strBuffUrl );

        String strUrl = strBuffUrl.toString(  );
        post.setContenu( strUrl );
    }

    /**
     * Activate tag filter links in the post
     *
     * @param post The Post
     */
    public static void activateHashtag( Post post )
    {
        String inputTag = post.getContenu(  );
        String strPatternTag = "(#[\\w\\u00C0-\\u00FF\\u0153]+)";
        Pattern pattTag = Pattern.compile( strPatternTag );
        Matcher matchTag = pattTag.matcher( inputTag );
        StringBuffer strBuffTag = new StringBuffer(  );
        boolean flagTag = false;

        while ( ( flagTag = matchTag.find(  ) ) )
        {
            String tag = matchTag.group(  ).replace( "#", "" );
            String mot = matchTag.group(  );
            matchTag.appendReplacement( strBuffTag,
                "<a href=\"jsp/admin/plugins/adminwall/ManageWall.jsp?view=managePosts&tag=" + tag + "\">" + mot +
                "</a>" );
        }

        matchTag.appendTail( strBuffTag );

        String strTag = strBuffTag.toString(  );
        post.setContenu( strTag );
    }

    //Detecte les Hashtags dans le post et insert dans la DB
    /**
     * Detect hashtags from the post and insert them in the database
     *
     * @param post The Post
     */
    public static void detectHashtag( Post post )
    {
        Pattern pattTag = Pattern.compile( "#[\\w\\u00C0-\\u00FF\\u0153]+" );
        Matcher matchTag = pattTag.matcher( post.getContenu(  ) );

        while ( matchTag.find(  ) )
        {
            String[] tab = new String[2];
            tab = matchTag.group(  ).split( "#", 2 );

            Hashtag hashtag = new Hashtag(  );
            hashtag.setTag( tab[1] );
            HashtagHome.create( hashtag );

            /*Insertion du Link*/
            Link link = new Link(  );
            link.setPost( post.getIdPost(  ) );
            link.setHashtag( hashtag.getIdHashtag(  ) );
            LinkHome.create( link );

            link = null;
            hashtag = null;
        }
    }
}
