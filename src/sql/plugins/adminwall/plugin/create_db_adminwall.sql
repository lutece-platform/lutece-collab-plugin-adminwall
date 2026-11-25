-- liquibase formatted sql
-- changeset adminwall:create_db_adminwall.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM adminwall_post WHERE 1 = 0

--
-- Structure for table adminwall_post
--

DROP TABLE IF EXISTS adminwall_link;
DROP TABLE IF EXISTS adminwall_post;
CREATE TABLE adminwall_post (
	id_post int AUTO_INCREMENT NOT NULL,
	contenu varchar(255) default '' NOT NULL,
	date timestamp default CURRENT_TIMESTAMP NOT NULL,
	id_auteur int default 0 NOT NULL,
	auteur varchar(50) default '' NOT NULL,
	PRIMARY KEY (id_post)
);

--
-- Structure for table adminwall_hashtag
--

DROP TABLE IF EXISTS adminwall_hashtag;
CREATE TABLE adminwall_hashtag (
	id_hashtag int AUTO_INCREMENT NOT NULL,
	tag varchar(50) default '' NOT NULL,
	PRIMARY KEY (id_hashtag)
);

--
-- Structure for table adminwall_link
--

CREATE TABLE adminwall_link (
	id_link int AUTO_INCREMENT NOT NULL,
	post int default 0 NOT NULL,
	hashtag int default 0 NOT NULL,
	PRIMARY KEY (id_link),
	CONSTRAINT fk_post_id FOREIGN KEY (post) REFERENCES adminwall_post (id_post) ON DELETE CASCADE,
	CONSTRAINT fk_hashtag_id FOREIGN KEY (hashtag) REFERENCES adminwall_hashtag (id_hashtag)
);

-- changeset adminwall:create_db_adminwall-recipient.sql
-- preconditions onFail:MARK_RAN onError:WARN
--
-- Structure for table adminwall_recipient : the admin users a post is restricted to (no row = sent to all)
--

CREATE TABLE IF NOT EXISTS adminwall_recipient (
	id_post int NOT NULL,
	id_user int NOT NULL,
	PRIMARY KEY (id_post, id_user),
	CONSTRAINT fk_recipient_post_id FOREIGN KEY (id_post) REFERENCES adminwall_post (id_post) ON DELETE CASCADE
);

-- changeset adminwall:create_db_adminwall-post_read.sql
-- preconditions onFail:MARK_RAN onError:WARN
--
-- Structure for table adminwall_post_read : the posts each admin user has marked as read (no row = new for that user)
--

CREATE TABLE IF NOT EXISTS adminwall_post_read (
	id_post int NOT NULL,
	id_user int NOT NULL,
	PRIMARY KEY (id_post, id_user),
	CONSTRAINT fk_post_read_post_id FOREIGN KEY (id_post) REFERENCES adminwall_post (id_post) ON DELETE CASCADE
);
-- the posts already there are read for the users who hold the right : only the posts written from now on are new
INSERT INTO adminwall_post_read (id_post, id_user)
SELECT p.id_post, r.id_user FROM adminwall_post p, core_user_right r
WHERE r.id_right = 'ADMINWALL_ACCESS' AND p.id_auteur <> r.id_user
AND NOT EXISTS (SELECT 1 FROM adminwall_post_read pr WHERE pr.id_post = p.id_post AND pr.id_user = r.id_user);

-- changeset adminwall:create_db_adminwall-importance.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- comment the importance of a post : high, medium or standard (the default, also for the posts already there)
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.columns WHERE LOWER(table_name) = 'adminwall_post' AND LOWER(column_name) = 'importance'
ALTER TABLE adminwall_post ADD COLUMN importance varchar(10) DEFAULT 'standard' NOT NULL;
