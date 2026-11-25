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

import org.apache.commons.text.StringEscapeUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.regex.Pattern;


/**
 *
 * @author Brasya
 */
public final class AdminWallService
{
    /**
     * Creates a new instance of AdminWallService
     */
    private AdminWallService(  )
    {
    }

    /**
     * Escape the content of the post, then activate its URL and its hashtag filter links
     *
     * @param post The Post
     */
    public static void activateLinks( Post post )
    {
        post.setContenu( StringEscapeUtils.escapeXml10( post.getContenu(  ) ) );
        activateURL( post );
        activateHashtag( post );
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
            String strHref = lien;

            if ( http == null )
            { //Detection du protocole
                strHref = "http://" + lien;
            }

            matchUrl.appendReplacement( strBuffUrl,
                "<a href=\"" + strHref + "\" alt=lien url target=\"_blank\">" + lien + "</a>" );
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
        Set<String> setTags = new HashSet<>(  );

        while ( matchTag.find(  ) )
        {
            String[] tab = new String[2];
            tab = matchTag.group(  ).split( "#", 2 );

            // a tag repeated in the post is linked once
            if ( !setTags.add( tab[1] ) )
            {
                continue;
            }

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

    /**
     * Count the hashtags of the posts, each post counting once per tag
     *
     * @param posts The posts, with their raw content
     * @param nMax The maximum number of tags returned
     * @return the most used tags, as maps with a "tag" and a "count", the most used first then by name
     */
    public static List<Map<String, Object>> getTopHashtags( Collection<Post> posts, int nMax )
    {
        Pattern pattTag = Pattern.compile( "#([\\w\\u00C0-\\u00FF\\u0153]+)" );
        Map<String, Integer> mapCounts = new TreeMap<>(  );

        for ( Post post : posts )
        {
            Set<String> setTags = new HashSet<>(  );
            Matcher matchTag = pattTag.matcher( ( post.getContenu(  ) != null ) ? post.getContenu(  ) : "" );

            while ( matchTag.find(  ) )
            {
                setTags.add( matchTag.group( 1 ) );
            }

            setTags.forEach( tag -> mapCounts.merge( tag, 1, Integer::sum ) );
        }

        return mapCounts.entrySet(  ).stream(  ).sorted( ( a, b ) -> b.getValue(  ).compareTo( a.getValue(  ) ) ).limit( nMax ).map( entry -> {
            Map<String, Object> mapTag = new LinkedHashMap<>(  );
            mapTag.put( "tag", entry.getKey(  ) );
            mapTag.put( "count", entry.getValue(  ) );

            return mapTag;
        } ).collect( Collectors.toList(  ) );
    }
}
