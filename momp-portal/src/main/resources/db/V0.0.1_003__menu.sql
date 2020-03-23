CREATE TABLE `menu` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `path` varchar(50) NOT NULL,
  `icon` varchar(50),
  `parent_id` int(11),
  `permission_id` int(11),
  `dynamic_children_code` varchar(20),
  `show_order` int(11),
  `sys_init` tinyint(1) NOT NULL,
  `create_time` datetime NOT NULL,
  `create_user` varchar(64),
  `update_time` datetime NOT NULL,
  `update_user` varchar(64),
  `deleted` datetime,
  PRIMARY KEY (`id`)
);
