### **端口**
momp-portal:8080
momp-admin:8769
redis:6379
elasticsearch:9200
mysql:3306
mongodb:27017
kibana:5601
rocketmq:9876
rocketmq-console:9877

### **框架说明**
&emsp;**1.mybatis-plus**
<br>&emsp;&emsp;com.eking.momp.CodeGenerator生成代码
<br>&emsp;**2.mongodb**
<br>&emsp;&emsp;启动命令 ./mongod --bind_ip_all -f /usr/local/mongodb/etc/mongodb.conf
<br>&emsp;**3.easyexcel**
<br>&emsp;&emsp;使用easyexcel作为数据导出框架
<br>&emsp;**4.redis**
<br>&emsp;&emsp;缓存框架

### **权限控制**
&emsp;**1.url权限**

&emsp;&emsp;通过PreAuthorize注解，hasAuthority检查权限，hasRole检查角色
	   @PreAuthorize("hasAuthority('user.query')")
	   @PreAuthorize("hasRole('admin')")

&emsp;**2.数据权限**

- 简单权限
  
  &emsp;PostAuthorize注解，通过返回值属性比对
         @PostAuthorize("returnObject.username == principal.username")

  &emsp;PreAuthorize注解，校验传入参数
	     @PreAuthorize("#id == principal.userId")
	     @PreAuthorize("#user.id == principal.userId")

- 复杂操作
  
  &emsp;使用自定义注解，具体逻辑判断在com.eking.momp.aop.DataPermissionAspect实现
	     @DataPermission(source = DataPermission.Source.USER, key = "id")
	     public User updateUser(@PathVariable Integer id, @RequestBody User user) 

### **异常处理**
&emsp;在com.eking.momp.common.exception包里自定义异常，统一处理异常
     @ExceptionHandler
      public ResultBean userNotFound(UserNotFoundExecpetion execpetion) {
         log.error("User not found {}", execpetion.getUserId());
         Locale locale = LocaleContextHolder.getLocale();
         String message = messageSource.getMessage("message.userNotFound", null, locale);
         return ResultBean.error(message);
     }

### **国际化**
&emsp;国际化文件在resouces/i18n下