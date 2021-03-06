# 使用方式
------
## 1) 定义service接口,接口有两种形式: 
   > *a类: 纯数据模型接口
   #### crudDao已具备增删改查功能, 如果需特殊功能,自行添加接口如下面customUpdate
```
@Mapper
@WebxService("orderdata")
@Sharding(databaseShardingStrategy = CitySharding.class)
@CacheBy( prefix = "testtt_",cacheKey = ":1.id", ttl = 60*60)
public interface UserDao  extends crudDao<User, UserQuery> {
    @WebxRequestMapping
    @Update("update user set age=:1.age where id=:2")
   //@CacheBy( prefix = "te2sttt_",cacheKey = ":1.id", ttl = 60*60) 支持单独设置缓存规则
    public  int customUpdate(User user1, int schoolId);
}
```

> *b类: 领域模型接口
```
@WebxService("orderdata")
public interface UserService {

    @WebxRequestMapping
    public  int getLevel(long userId);
}
```
***
## 2) 服务端


```
@EnableDiscoveryClient
@SpringBootApplication()
@EnableMybatisX("a类接口包名")
@EnableWebx("b类接口包名")
public class App {
}
```
配置文件
```
spring.application.name = orderdata
orderdata.version=v3
spring.cloud.zookeeper.connect-string=10.1.44.62:2181
spring.cloud.zookeeper.discovery.instance-host=10.1.44.62
spring.cloud.zookeeper.discovery.instance-port=${server.port}
```
###启动成功后自动注册节点如下(带版本号节点)<br/>
![image](https://github.com/mybatisx/mybatisx/blob/master/Doc/imgs/zk.png)
***
## 2) 客户端


```
@EnableDiscoveryClient
@SpringBootApplication()
@EnableWebx
public class App {
}
```
配置文件
```
spring.application.name = otherapp
orderdata.version=v3  //指定调用orderdata的v3节点下的服务
spring.cloud.zookeeper.connect-string=10.1.44.62:2181
```
```
@Component
public class Test {

    @WebxReference
    private UserDao dao;

    @WebxReference
    private UserService userService;

    public void  test(){
        var age = (int) (1 + Math.random() * (100 - 1 + 1));
        var user = User.builder().id(6).age(age).namex("test"+age).build();
        var k= userService.getLevel(5);
        var v = dao.insert(user);
        String mm = "33";
    }
}
```
