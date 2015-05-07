
--
-- Structure for table adminwall_post
--

DROP TABLE IF EXISTS adminwall_post;
CREATE TABLE adminwall_post (		
id_post int(11) NOT NULL default '0',
contenu varchar(255) NOT NULL default '',
date datetime NOT NULL,
id_auteur int(11) NOT NULL default '0',
auteur varchar(50) NOT NULL default '',
PRIMARY KEY (id_post)
)
COLLATE='utf8_unicode_ci'
ENGINE=InnoDB;
--
-- Structure for table adminwall_hashtag
--

DROP TABLE IF EXISTS adminwall_hashtag;
CREATE TABLE adminwall_hashtag (		
id_hashtag int(11) NOT NULL default '0',
tag varchar(50) NOT NULL default '',
PRIMARY KEY (id_hashtag)
)
COLLATE='utf8_unicode_ci'
ENGINE=InnoDB;
--
-- Structure for table adminwall_link
--
DROP TABLE IF EXISTS adminwall_link;
CREATE TABLE `adminwall_link` (
	`id_link` INT(11) NOT NULL DEFAULT '0',
	`post` INT(11) NOT NULL DEFAULT '0',
	`hashtag` INT(11) NOT NULL DEFAULT '0',
        PRIMARY KEY (id_link),
	CONSTRAINT `fk_post_id` FOREIGN KEY (`post`) REFERENCES `adminwall_post` (`id_post`) ON DELETE CASCADE,
	CONSTRAINT `fk_hashtag_id` FOREIGN KEY (`hashtag`) REFERENCES `adminwall_hashtag` (`id_hashtag`)
)
COLLATE='utf8_unicode_ci'
ENGINE=InnoDB;
