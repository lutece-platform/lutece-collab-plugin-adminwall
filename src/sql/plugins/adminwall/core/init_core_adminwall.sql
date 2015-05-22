
--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'UPDATER_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('ADMINWALL_ACCESS','adminwall.adminFeature.AdminWall.name',3,'jsp/admin/plugins/adminwall/ManageWall.jsp','adminwall.adminFeature.AdminWall.description',1,'adminwall',NULL,NULL,NULL,4);


--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'ADMINWALL_ACCESS';
INSERT INTO core_user_right (id_right,id_user) VALUES ('ADMINWALL_ACCESS',1);

-- Dashboard
INSERT INTO core_dashboard(dashboard_name, dashboard_column, dashboard_order) VALUES('ADMINWALL', 1, 4);