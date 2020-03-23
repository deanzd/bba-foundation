package com.eking.momp;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

public class CodeGenerator {

	public static void main(String[] args) {
		// 1. 全局配置
		GlobalConfig config = new GlobalConfig();
		String projectPath = System.getProperty("user.dir");
		config.setActiveRecord(true) // 是否支持AR模式
				.setAuthor("Dean") // 作者
				.setOutputDir(projectPath + "/src/main/java").setFileOverride(true) // 文件覆盖
				.setIdType(IdType.AUTO) // 主键策略
				.setServiceName("%sService") // 设置生成的service接口的名字的首字母是否为I
				// IEmployeeService
				.setBaseResultMap(true)// 生成基本的resultMap
				.setBaseColumnList(true);// 生成基本的SQL片段

		// 2. 数据源配置
		DataSourceConfig dsConfig = new DataSourceConfig();
		dsConfig.setDbType(DbType.MYSQL) // 设置数据库类型
				.setDriverName("com.mysql.cj.jdbc.Driver")
				
				.setUrl("jdbc:mysql://192.168.6.54:3306/momp")
				.setUsername("root")
				.setPassword("mysql");
		
//				.setUrl("jdbc:mysql://localhost:3306/momp?serverTimezone=UTC")
//				.setUsername("root")
//				.setPassword("mysql");

		// 3. 策略配置globalConfiguration中
		StrategyConfig stConfig = new StrategyConfig();
		stConfig.setCapitalMode(true) // 全局大写命名
//				.setDbColumnUnderline(true) // 指定表名 字段名是否使用下划线
				.setNaming(NamingStrategy.underline_to_camel) // 数据库表映射到实例的命名策略
				.setEntityLombokModel(true)
				.setInclude(
					"user",
					"role",
					"user_role",
					"permission",
					"role_permission"
//					"relation_type",
//					"model_relation",
//					"model",
//					"model_field",
//					"dimension",
//					"dimension_model",
//					"dimension_relation",
//					"menu",
//					"layer",
//					"model_uniqueness",
//					"model_uniqueness_item"
				); // 生成的表

		// 4. 包名策略配置
		PackageConfig pkConfig = new PackageConfig();
		pkConfig.setParent("com.eking.momp.db")
				.setMapper("mapper")// dao
				.setService(null)// servcie
				.setServiceImpl(null)
				.setController(null)// controller
				.setEntity("model")
				.setXml("sql");// mapper.xml

		// 5. 整合配置
		AutoGenerator ag = new AutoGenerator();
		ag.setGlobalConfig(config).setDataSource(dsConfig).setStrategy(stConfig).setPackageInfo(pkConfig);
		// 6. 执行
		ag.execute();
	}
}
