CREATE TABLE `model` (
  `id` int(11) AUTO_INCREMENT,
  `code` varchar(64) NOT NULL,
  `name` varchar(64) NOT NULL,
  `description` varchar(255),
  `icon_image` varchar(64),
  `layer_id` int(11),
  `show_order` int(11),
  `sys_init` tinyint(1) NOT NULL,
  `create_time` datetime NOT NULL,
  `create_user` varchar(64),
  `update_time` datetime NOT NULL,
  `update_user` varchar(64),
  `deleted` datetime,
  PRIMARY KEY (`id`)
);

CREATE TABLE `model_field` (
  `id` int(11) AUTO_INCREMENT,
  `code` varchar(64) NOT NULL,
  `model_id` int(11) NOT NULL,
  `name` varchar(64) NOT NULL,
  `description` varchar(255),
  `required` tinyint(1),
  `data_type` varchar(64) NOT NULL,
  `verify_regex` varchar(255),
  `show_in_table` tinyint(1) NOT NULL,
  `show_in_list` tinyint(1) NOT NULL,
  `search_key` tinyint(1),
  `show_order` int(11),
  `sys_init` tinyint(1) NOT NULL,
  `create_time` datetime NOT NULL,
  `create_user` varchar(64),
  `update_time` datetime NOT NULL,
  `update_user` varchar(64),
  `deleted` datetime,
  PRIMARY KEY (`id`)
);

CREATE TABLE `model_relation` (
  `id` int(11) AUTO_INCREMENT,
  `coce` varchar(64) NOT NULL,
  `name` varchar(64) NOT NULL,
  `relation_type_id` int(11) NOT NULL,
  `mapping` varchar(32) NOT NULL,
  `model_id` int(11) NOT NULL,
  `target_model_id` int(11) NOT NULL,
  `model_field_id` int(11),
  `description` varchar(255),
  `sys_init` tinyint(1) NOT NULL,
  `create_time` datetime NOT NULL,
  `create_user` varchar(64),
  `update_time` datetime NOT NULL,
  `update_user` varchar(64),
  `deleted` datetime,
  PRIMARY KEY (`id`)
);

CREATE TABLE `relation_type` (
  `id` int(11) AUTO_INCREMENT,
  `code` varchar(64) NOT NULL,
  `name` varchar(64) NOT NULL,
  `text` varchar(64),
  `reverse_text` varchar(64),
  `sys_init` tinyint(1) NOT NULL,
  `create_time` datetime NOT NULL,
  `create_user` varchar(64),
  `update_time` datetime NOT NULL,
  `update_user` varchar(64),
  `deleted` datetime,
 PRIMARY KEY (`id`)
);

CREATE TABLE `dimension` (
  `id` int(11) AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(255),
  `model_id` int(11) NOT NULL,
  `show_order` int(11),
  `sys_init` tinyint(1) NOT NULL,
  `create_time` datetime NOT NULL,
  `create_user` varchar(64),
  `update_time` datetime NOT NULL,
  `update_user` varchar(64),
  `deleted` datetime,
  PRIMARY KEY (`id`)
);

CREATE TABLE `dimension_model` (
  `id` int(11),
  `dimension_id` int (11) NOT NULL,
  `model_id` int(11) NOT NULL,
  `show_order` int(11),
  `sys_init` tinyint(1) NOT NULL,
  `create_time` datetime NOT NULL,
  `create_user` varchar(64),
  `update_time` datetime NOT NULL,
  `update_user` varchar(64),
  `deleted` datetime,
  PRIMARY KEY (`id`)
);

CREATE TABLE `dimension_relation` (
  `id` int(11) AUTO_INCREMENT,
  `dimension_id` int(11) NOT NULL, 
  `model_relation_id` int(11) NOT NULL,
  `sys_init` tinyint(1) NOT NULL,
  `create_time` datetime NOT NULL,
  `create_user` varchar(64),
  `update_time` datetime NOT NULL,
  `update_user` varchar(64),
  `deleted` datetime,
  PRIMARY KEY (`id`)
);
CREATE TABLE `layer` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(64) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` varchar(255),
  `show_order` int(11),
  `icon_image` varchar(255),
  `sys_init` tinyint(1) NOT NULL,
  `create_time` datetime NOT NULL,
  `create_user` varchar(64),
  `update_time` datetime NOT NULL,
  `update_user` varchar(64),
  `deleted` datetime,
  PRIMARY KEY (`id`)
);
CREATE TABLE `model_uniqueness` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `model_id` int(11) NOT NULL,
  `required` tinyint(1),
  `sys_init` tinyint(1) NOT NULL,
  `create_time` datetime NOT NULL,
  `create_user` varchar(64),
  `update_time` datetime NOT NULL,
  `update_user` varchar(64),
  `deleted` datetime,
  PRIMARY KEY (`id`)
);
CREATE TABLE `model_uniqueness_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `model_uniqueness_id` int(11) NOT NULL,
  `model_field_id` int(11) NOT NULL,
  `sys_init` tinyint(1) NOT NULL,
  `create_time` datetime NOT NULL,
  `create_user` varchar(64),
  `update_time` datetime NOT NULL,
  `update_user` varchar(64),
  `deleted` datetime,
  PRIMARY KEY (`id`)
);






