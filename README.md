# Plugin adminwall

## Introduction

plugin-adminwall is an internal message wall for Lutece 8 back-office users: short posts of up to 255 characters,
tagged with hashtags. Admins post, browse and filter messages from one screen, and the latest posts also appear as a
widget on the admin dashboard.

| Item | Value |
| --- | --- |
| Maven coordinates | `fr.paris.lutece.plugins:plugin-adminwall`, type `lutece-plugin` |
| Lutece core | 8.0.0 or later |
| Admin right | `ADMINWALL_ACCESS`, level 3 |
| Entry point | `jsp/admin/plugins/adminwall/ManageWall.jsp` |
| Menu icon | `ti ti-messages` (Tabler) |
| Languages | English, French |

## Features

Every user with the `ADMINWALL_ACCESS` right can post, read and filter; only a post's author can delete it. A post goes
to all users by default, or to a selection of users, who are then the only ones to see it with its author.

| Feature | How it works |
| --- | --- |
| Post a message | The **New post** button opens a modal form; the text is mandatory, 255 characters at most. The author and the timestamp are set by the server. |
| Importance | The form offers **Standard** (default), **Medium** and **High**. Every display shows it as a badge with an icon: High red (`bg-red`, alert triangle), Medium orange (`bg-warning`, alert circle), Standard blue (`bg-primary`, info circle) — in the list, the home page widget and the notification menu. Any other submitted value is refused by the bean validation. |
| Recipients | The form offers **All users** (default) or **Selected users**. Selected users are picked one by one with an autocomplete on first name, last name, login or email (2 characters minimum, 10 suggestions); each pick becomes a removable chip. Only active users holding `ADMINWALL_ACCESS` are proposed. A private post shows a lock badge with its recipients' names. |
| New / read status | Each message is **new** for each user until that user marks it as read; a user's own messages are always read for them. A green **New** badge marks them in the list and the dashboard widget. **Mark as read** (on each new message) and **Mark all as read** change the status. |
| Filter by status | **All**, **New** (with the count of new messages) and **Read** buttons in the page header (`status=new` or `status=read`); they combine with the tag and author filters. |
| Notification | The dashboard `ADMINWALL_NOTIFICATION` sits in the core's notification zone of the admin header (the bell): the count of new messages with a link to the list filtered on **New**, and the 5 latest new messages. Clicking one marks it as read and opens the wall; it then leaves the menu. A **New message** button opens the wall with the creation form already open (`newPost=true`). With nothing new it renders nothing, so the bell shows no badge. |
| Hashtags | Every `#word` in a post (accented letters included) is stored as a tag and rendered as a link that filters the wall on that tag. |
| Links | URLs in a post are turned into links that open in a new tab. |
| Filter by tag | `ManageWall.jsp?view=managePosts&tag=<tag>`; an unknown tag shows an empty list. |
| Filter by author | Clicking an author's name shows only their posts (`idAuteur` parameter). |
| Paging | 50 posts per page by default (`adminwall.listPosts.itemsPerPage`), with a page-size selector. |
| Delete a post | Shown on the author's own posts only; asks for confirmation, then removes the post and its tag links. |
| Dashboard widget | The `ADMINWALL` widget (zone 1, order 4) lists the 20 most recent posts the user may read. |

## Installation

Add the dependency to the site's `pom.xml`; the database scripts run through Liquibase when the site starts.

```xml
<dependency>
    <groupId>fr.paris.lutece.plugins</groupId>
    <artifactId>plugin-adminwall</artifactId>
    <version>[2.0.0-SNAPSHOT]</version>
    <type>lutece-plugin</type>
</dependency>
```

| Script | What it does |
| --- | --- |
| `plugin/create_db_adminwall.sql` | Creates `adminwall_post`, `adminwall_hashtag`, `adminwall_link`, `adminwall_recipient` and `adminwall_post_read`; skipped when the tables exist. |
| `core/init_core_adminwall.sql` | Registers the `ADMINWALL_ACCESS` right, grants it to user 1 (admin), and registers the home page widget (zone 1) and the notification dashboard (zone 5). |
| `upgrade/update_db_adminwall-1.9.9-2.0.0.sql` | Takes over a v1.x database (see [Upgrading](#upgrading-from-1x-lutece-7)). |

After installation, grant `ADMINWALL_ACCESS` to the users or roles who should see the wall.

## Configuration

The only property is `adminwall.listPosts.itemsPerPage=50` in `WEB-INF/conf/plugins/adminwall.properties`; override it
in `WEB-INF/conf/override/` if needed. No other plugin is required: authors are shown by their initials.

The notification needs a core with a notification zone in the admin header (the zone after the user zone: zone 5 with
`lutece.dashboard.zones.count=4`), whose dashboards are rendered inside the `.lutece-notification` element of the
bell. `init_core_adminwall.sql` places `ADMINWALL_NOTIFICATION` in zone 5; with another zone count, move it in
**Manage the dashboards**. Without that zone, the list, its filters and
the badges work all the same.

## Architecture

The plugin follows the standard Lutece 8 layers, wired by CDI (`META-INF/beans.xml`, discovery mode `annotated`) under
the package `fr.paris.lutece.plugins.adminwall`.

```
Web        ManageWall.jsp ──> WallJspBean                Dashboard widget
                                   │                        │        │
Service              AdminWallService  <────────────────────┘        │
                       │          │                                  │
Business          HashtagHome   LinkHome                         PostHome
                       │          │                                  │
DAO               HashtagDAO    LinkDAO                          PostDAO
                       │          │                                  │
Database     adminwall_hashtag  adminwall_link              adminwall_post
```

`WallJspBean` also calls the three Homes directly to create, list and delete posts.

| Class | Package | Role |
| --- | --- | --- |
| `WallJspBean` | `web` | `@RequestScoped` MVC controller named `adminwallWallJspBean`: views `managePosts`, `confirmRemovePost`; actions `createPost`, `removePost`, `markRead`, `markAllRead`; injected `IPager`. |
| `RecipientSearchJspBean` | `web` | `@RequestScoped` controller named `adminwallRecipientSearchJspBean`, bound to `SearchRecipients.jsp`: view `searchRecipients` returns the autocomplete suggestions as JSON (`@ResponseBody`). It lives apart from `WallJspBean` because `ManageWall.jsp` wraps its output in the admin header and footer. |
| `NotificationDashboardComponent` | `web` | Dashboard of the notification zone: the new messages of the user, each as a form posting `markRead` with its own token (taken without changing the token of the page being rendered). Renders nothing when nothing is new. |
| `AdminWallJspBean` | `web` | Abstract base holding the right `ADMINWALL_ACCESS`. |
| `AdminWallDashboardComponent` | `web` | Dashboard widget, created by the core from `adminwall.xml` (no CDI scope). |
| `AdminWallService` | `service` | Static helpers: escape content, link URLs and hashtags, extract hashtags. |
| `ReadStatusService` | `service` | New / read status of the posts for a user, status filter, mark as read. |
| `RecipientService` | `service` | Visibility of private posts, recipient search for the autocomplete, validation of the submitted recipients. |
| `PostHome`, `HashtagHome`, `LinkHome`, `RecipientHome`, `PostReadHome` | `business` | Static facades that look up their DAO with `CDI.current()`. |
| `PostDAO`, `HashtagDAO`, `LinkDAO`, `RecipientDAO`, `PostReadDAO` | `business` | `@ApplicationScoped` JDBC DAOs; ids come from generated keys. |

Templates live in `WEB-INF/templates/admin/plugins/adminwall/`: `manage_posts.html`, `create_post.html` (modal) and
`dashboard/adminwall_dashboard.html`, all built on the Tabler admin macros. The recipient autocomplete calls the view
`searchRecipients` of `RecipientSearchJspBean` through `jsp/admin/plugins/adminwall/SearchRecipients.jsp`, a JSP with
no admin header that returns JSON;
the chips are handled by `themes/admin/shared/plugins/adminwall/js/adminwall-recipients.js`. The notification
entries come from `dashboard/adminwall_notification.html`.

## Data model

Posts and hashtags are joined many-to-many through `adminwall_link`; `adminwall_recipient` restricts a post to some
admin users. All ids are generated by the database.

| Table | Columns | Notes |
| --- | --- | --- |
| `adminwall_post` | `id_post`, `contenu` varchar(255), `date` timestamp, `id_auteur` int, `auteur` varchar(50), `importance` varchar(10) | `id_auteur` is the admin user id; `auteur` is their display name at posting time; `importance` is `high`, `medium` or `standard` (default). |
| `adminwall_hashtag` | `id_hashtag`, `tag` varchar(50) | One row per distinct tag, reused across posts. No unique index on `tag`. |
| `adminwall_link` | `id_link`, `post`, `hashtag` | `post` → `adminwall_post` with `ON DELETE CASCADE`; `hashtag` → `adminwall_hashtag`. |
| `adminwall_post_read` | `id_post`, `id_user` | Primary key on both; `id_post` → `adminwall_post` with `ON DELETE CASCADE`. A row = that user marked the post as read. |
| `adminwall_recipient` | `id_post`, `id_user` | Primary key on both; `id_post` → `adminwall_post` with `ON DELETE CASCADE`. A post with no row is sent to all. |

Deleting a post removes its links, recipients and read marks; hashtags are never deleted.

## Security

- **Right check.** The controller is bound to `ADMINWALL_ACCESS`; without it the screen and the actions are refused.
- **CSRF tokens.** Creating and deleting a post both require the token issued by the view that shows the form or the
  confirmation.
- **Escaping.** Post content is XML-escaped before URLs and hashtags are turned into links, so markup typed in a post
  is shown as text.
- **Ownership.** The author id and name come from the logged-in user, never from the form, and only the author can
  delete a post.
- **Private posts.** A post with recipients is filtered out of the list, the tag and author filters and the dashboard
  for every other user. Submitted recipients are checked on the server: unknown, inactive or right-less users and the
  author are dropped, and "Selected users" with none left is refused.

## Upgrading from 1.x (Lutece 7)

An existing 1.x database keeps its posts: `update_db_adminwall-1.9.9-2.0.0.sql` converts the tables in place when the
site starts.

1. The menu icon of `ADMINWALL_ACCESS` is set to `ti ti-messages`.
2. On MariaDB/MySQL, `id_post`, `id_hashtag` and `id_link` become `AUTO_INCREMENT`; existing rows and foreign keys are
   kept.
3. On PostgreSQL, the same three columns become identity columns, and each sequence restarts after the highest
   existing id.
4. Each change checks the column first, so a database already converted is left alone.
5. The `adminwall_recipient` table is created (`CREATE TABLE IF NOT EXISTS`).
6. The `adminwall_post_read` table is created, and the messages already there are marked as read for every holder of
   `ADMINWALL_ACCESS`, so only the messages written after the upgrade show as new.
7. The notification dashboard `ADMINWALL_NOTIFICATION` is registered in zone 5.
8. The `importance` column is added to `adminwall_post`, `standard` for the messages already there.

On the site side, replace the 1.x dependency with 2.0.0 and remove plugin-adminavatar if adminwall was its only user.
The AdminLTE stylesheet `css/admin/AdminLTEv2.css` is no longer shipped.

Behaviour changes and fixes in 2.0.0:

- Deleting a post no longer deletes an unrelated hashtag link of another post.
- Updating a post no longer swaps the author id and name columns.
- Post content is escaped (stored XSS fixed).
- The author can no longer be forged from the form, and only the author can delete a post.
- An unknown tag in the URL shows an empty list instead of an error page.
- `init_core_adminwall.sql` no longer deletes the `UPDATER_MANAGEMENT` right.
- The dashboard widget shows the 20 most recent posts instead of all of them.

## Build

```bash
mvn clean lutece:exploded antrun:run -Dlutece-test-hsql install
```

`lutece:exploded antrun:run` assembles the test webapp and creates the HSQL test database from the SQL scripts;
`-Dlutece-test-hsql` adds the HSQL driver. Without them the unit tests cannot start.
