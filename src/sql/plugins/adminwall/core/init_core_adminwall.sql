
--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'UPDATER_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('ADMINWALL_ACCESS','adminwall.adminFeature.AdminWall.name',1,'jsp/admin/plugins/adminwall/ManageWall.jsp','adminwall.adminFeature.AdminWall.description',0,'adminwall',NULL,NULL,NULL,4);


--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'ADMINWALL_ACCESS';
INSERT INTO core_user_right (id_right,id_user) VALUES ('ADMINWALL_ACCESS',1);

