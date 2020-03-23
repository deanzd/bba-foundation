# **部署**
## 1.mongodb
 - 安装：

       tar -zxvf mongodb-linux-x86_64-3.0.6.tgz 
       mv  mongodb-linux-x86_64-3.0.6/ /usr/local/webserver/mongodb
 - 位置：/usr/local/webserver/mongodb
 - 配置： ***位置***建目录data/db、log；***位置***添加mongo.conf，start_mongo.sh
 - 运行：执行start_mongo.sh
## 2.redis
 - 安装：
 
       tar -zxvf redis-5.0.5.tar.gz
       cd redis-5.0.5
       make
       cd ..
       mv redis-5.0.5 /usr/local/webserver/redis
 - 位置：/usr/local/webserver/redis
 - 配置：redis.config 
        
        daemonize yes 后台启动
        protected-mode no 非本机访问
        bind 0.0.0.0 非本机访问
 - 运行：执行start_redis.sh
## 3.elastic search
 - 安装

        elasticsearch-7.3.1-linux-x86_64.tar.gz
        mv elasticsearch-7.3.1-linux-x86_64 /usr/local/webserver/elasticsearch
 - 位置：/usr/local/webserver/elasticsearch 
 - 配置：
        
        useradd elastic 添加用户，es不能用root启动
        passwd elastic 设置密码
        
        /etc/security/limits.conf
        elastic               soft    nofile          65536
        elastic               hard    nofile          65536
        elastic               soft    nproc           4096
        elastic               hard    nproc           4096
        
        /etc/security/limits.d/90-nproc.conf
        elastic    soft    nproc     4096  
        
        /etc/sysctl.conf
        vm.max_map_count = 262144
        
        /sbin/sysctl -p  使sysctl生效
        
        ***位置***/config/elasticsearch.yml
        bootstrap.system_call_filter: false
        
 - 运行：***位置***执行start_elasticsearch.sh
## 4.rocketmq
 - 安装：
       解压就行,rocketmq-console.jar放到***位置***
 - 位置：/usr/local/webserver/rocketmq
 - 配置：运行内存的不足，按条件需要修改找到runserver.sh和runbroker.sh，编辑 
      JAVA_OPT="${JAVA_OPT} -server -Xms256m -Xmx256m -Xmn125m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=320m"
 - 运行：***位置***执行start_namesrv.sh，start_broker.sh，start_console
## 5.server
 - 位置：/usr/local/webserver/momp-server
 - 配置：***位置***添加start_momp_portal.sh；***位置***放jar包<font color="orange">momp-portal.jar</font>
 - 运行：运行start_momp_portal.sh
## 6.client
 - 位置：/usr/local/webserver/nginx
 - 配置： ***位置***/conf添加nginx.conf（worker_processes 2; #设置值和CPU核心数一致）；***位置***/html放入<font color="orange">dist内容</font>
 - 运行：启动nginx
   