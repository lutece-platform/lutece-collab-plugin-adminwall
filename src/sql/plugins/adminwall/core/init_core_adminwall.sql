-- liquibase formatted sql
-- changeset adminwall:init_core_adminwall.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM core_admin_right WHERE id_right = 'ADMINWALL_ACCESS'
--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'ADMINWALL_ACCESS';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('ADMINWALL_ACCESS','adminwall.adminFeature.AdminWall.name',3,'jsp/admin/plugins/adminwall/ManageWall.jsp','adminwall.adminFeature.AdminWall.description',1,'adminwall',NULL,'ti ti-messages',NULL,4);


--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'ADMINWALL_ACCESS';
INSERT INTO core_user_right (id_right,id_user) VALUES ('ADMINWALL_ACCESS',1);

-- Dashboard
DELETE FROM core_dashboard WHERE dashboard_name = 'ADMINWALL';
INSERT INTO core_dashboard(dashboard_name, dashboard_column, dashboard_order) VALUES('ADMINWALL', 1, 4);

-- changeset adminwall:init_core_adminwall-notification.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- comment the new messages of the user in the notification zone of the admin header (zone 5 with lutece.dashboard.zones.count=4)
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM core_dashboard WHERE dashboard_name = 'ADMINWALL_NOTIFICATION'
INSERT INTO core_dashboard(dashboard_name, dashboard_column, dashboard_order) VALUES('ADMINWALL_NOTIFICATION', 5, 1);

-- changeset adminwall:init_core_adminwall-rbac.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- comment RBAC of the messages (resource type ADMINWALL_POST) : adminwall_user reads and writes, adminwall_moderator deletes any message ; both for the admin
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM core_admin_role WHERE role_key IN ( 'adminwall_user', 'adminwall_moderator' )
INSERT INTO core_admin_role (role_key, role_description) VALUES ('adminwall_user', 'Back Office messages - read and write');
INSERT INTO core_admin_role (role_key, role_description) VALUES ('adminwall_moderator', 'Back Office messages - delete any message');
INSERT INTO core_admin_role_resource (role_key, resource_type, resource_id, permission) VALUES ('adminwall_user', 'ADMINWALL_POST', '*', 'VIEW');
INSERT INTO core_admin_role_resource (role_key, resource_type, resource_id, permission) VALUES ('adminwall_user', 'ADMINWALL_POST', '*', 'CREATE');
INSERT INTO core_admin_role_resource (role_key, resource_type, resource_id, permission) VALUES ('adminwall_moderator', 'ADMINWALL_POST', '*', 'DELETE');
INSERT INTO core_user_role (role_key, id_user) VALUES ('adminwall_user', 1);
INSERT INTO core_user_role (role_key, id_user) VALUES ('adminwall_moderator', 1);
