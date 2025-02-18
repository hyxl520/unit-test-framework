# 简介
---

在Java SSM相关的Web开发过程中，单测是一个提高代码质量的关键举措。然而在实际写单测的过程中，虽然Mockito能够帮我们解决某些需要跟数据库或者Redis交互的方法数据问题，但是如果特别需要验证与外部资源交互的逻辑时Mockito
只是对返回值进行了Mock
，内部的交互逻辑却无法真实验证。目前很多人采用的是使用`H2database`和`embedded 
Redis`来替代实际MySQL和Redis来进行测试，然而在集成和使用过程中测试数据的初始化、实际的交互这些都需要额外配置，整体还是有一定的融入难度。在实际的单测里面，很多Bean我们也并不需要，利用`@ComponentScan`来配置也比较麻烦等等问题，因此`unit-test-framework`就是为了解决这些问题而开发的，原生集成了`H2database`和`embedded Redis`，并且提供了Spring boot风格的基于注解的配置，整体做到开箱即用，具体的内容可以见如下的特性。
# 特性
---
## 原生集成`H2database`和`embedded Redis`
unit-test-framework使用H2database来模拟MySQL，利用embed Redis来模拟Redis，使得与数据库和Redis真实交互的逻辑也能得到验证。

## 更方便的表和数据初始化
要想在单测前对表和数据进行初始化，框架提供了`@EnableMockDatabase`注解，你可以按如下的方式配置初始化的H2database脚本路径即可。
```java
@EnableMockDatabase(
	//建表脚本路径，需放在classpath下
	initSchemaSqlLocations = "/sql/init.sql", 
	//数据插入脚本路径
	initDataSqlLocations = "/sql/data.sql",  
	//单测类执行完成后清理数据的脚本路径
	cleanupSqlLocations = "/sql/clean.sql", 
	//在单测类执行完后是否自动清理所有的表和数据，避免多个单测类一起执行时的表冲突
	cleanupAllTablesAndData = true
)
public class Test extend BaseTest{}
```
当然这个注解也可以用在测试的基类上，子测试类启动时都会执行脚本，更多请看后文的使用。

## 更人性化的embedded Redis启动方式
如果项目使用了Redis，只需要在单测类上使用`@EnableMockRedisServer`注解，支持配置启动的Redis服务的端口号和分配的内存大小。启动后框架会自动切换Spring redis的数据源到embedded Redis。

## 针对多单测类同时运行的优化
在跑单测的时候很多都是打包的时候一次性全执行，该框架对多单测类同时运行的Spring上下文共享做了些许兼容，保证在多单测类同时运行时的幂等性。并且`@EnableMockDatabase`注解也默认开启了单测类执行完成后自动清理该单测类所使用过的所有数据和表，避免不同单测类之间的冲突。

## 单测方法级别的表和数据初始化
`@EnableMockDatabase`注解是用在单测类上定义该单测类所有单测方法共享的数据源，然而可能实际过程中不同的单测方法对于表和数据要求不一样，因此提供了一个同`@EnableMockDatabase`功能相同的方法级别注解`@MockData`，用在方法执行前后对数据做初始化和清理。

## 更简单的无用Bean排除
单测时对很多SpringBean其实是不需要的，然而一般写单测时为了方便都会用应用实际的启动类来启动单测，这个时候所有的Bean都会加载进来，影响启动速度的同时也可能由于多个单测类执行时的刷新Bean行为导致错误，因此框架引入了更加简单但强大的Bean的排除方式，支持按类名、Class对象、实例以及正则表达式方式排除，正则表达式还能进行整个包的排除。

## Embedded Redis对Mac的支持
官方的embedded Redis依赖在Mac上运行总会失败，官方目前没有对这个有修复方案，通过走访github，发现`com.github.codemonstur`的embedded Redis对其进行了修复，因此这里也感谢这位大佬。在框架里面也集成了这个依赖替换了原来的embedded Redis，使得在Mac上也能正确地得以运行。

## 提供默认的启动类
有些时候我们想写单测，但是可能写单测的地方没有Spring的启动类，但是需要Spring的环境，比如一些工具包下，因此框架也提供了一个默认的启动类实现`DefaultTestBootApplication`，它足够简单，只包含了框架需要的一些Bean。

## 提供多种数据库支持
H2database是支持多种数据库类型的，你只需要修改`@EnableMockDatabase`的databastType即可
```java
@EnableMockDatabase(initSchemaSqlLocations = "/sql/init.sql", initDataSqlLocations = "/sql/data.sql",databaseType = MockDatabaseType.ORACLE)
```

# 使用
---
- **引入依赖**
	```xml
	<dependency>  
	    <groupId>io.github.hyxl520</groupId>  
	    <artifactId>unit-test-engine</artifactId>  
	    <version>1.0.0-SNAPSHOT</version>  
	</dependency>
	```
	你也可以将代码下载下来，导入到你的项目里面，你可以推到你们公司的私有仓库，也可以直接嵌入开发项目使用。
- **创建单测类**
```java
//你可以定义一个测试的基类，就不要每个测试类都加上注解了
//1. 你可以使用默认的测试启动类，也可以使用你自定义的
@SpringBootTest(classes = DefaultTestBootApplication.class)
//2. 组合注解，声明使用JingGeUnitTest，使用该注解后才能启动框架相关功能
@JingGeUnitTest
//3. 定义要排除的Bean，参数是要排除Bean的定义器，具体使用可见后文
@EnableUselessBeanAutoRemoved(definers = {RedisExcludeDefiner.class})
//4. 开启embedded Redis服务，此时spring redis数据源会被替换，配置端口号是26379，分配内存为128MB
@EnableMockRedisServer(port = 26379, mallocMemorySize = 128)
public class BaseTest {  
}

//定义实际的测试类，再基类定义过的注解可以不用再定义
public class Test1 extends BaseTest{
	//此时自动注入的RedisTemplate数据源已经被重定向到了embedded redis，如果基类或者子类没有@EnableMockRedisServer注解，则Spring Redis的相关Bean都会被移除，加速启动
    @Autowired(required = false)  
    private RedisTemplate<String,String> redisTemplate;

	@Test
	@MockData(initSchemaSqlLocations = "/sql/init.sql",initDataSqlLocations = "/sql/data.sql")  
	public void test1() {  
	    if(redisTemplate!=null) {  
	       redisTemplate.opsForValue().set("@test1", "1");  
	       System.out.println(redisTemplate.opsForValue().get("@test1"));  
	    }else{  
	       System.out.println("redis template is null");  
	    }  
	    System.out.println("hello unit test");  
	}
}


```
> **注意**：你的SQL建表脚本应该是H2database语法的，而不是MySQL语法，H2database默认就是已经有数据库的，只需要写建表语句即可
	
- **自定义Bean排除**
要自定义排除一些Bean，可以使用接口`BeanExcludeDefiner`接口来实现，如下默认示列
```java
/**  
 * 排除Redis相关依赖，当没有使用{@link io.github.hyxl520.annotations.EnableMockRedisServer}时生效  
 *  
 * @author Jingge  
 * @date 2025-02-11 11:04  
 * @email 1158055613@qq.com  
 */public class RedisExcludeDefiner implements BeanExcludeDefiner {  
    @Override  
    public List<Class<?>> excludeBeansByClass() {  
       return List.of(RedisProperties.class, RedisAutoConfiguration.class, RedisTemplate.class,  StringRedisTemplate.class, RedisConverter.class, RedisKeyValueAdapter.class,  RedisKeyValueTemplate.class);  
    }  
  
    @Override  
    public List<String> excludeBeansByName() {  
       return List.of("redisReferenceResolver");  
    }  

	//只有当符合条件的时候才启动该定义器对应的排除策略
    @Override  
    public boolean conditionOn(BeanDefinitionRegistryFacade beanDefinitionRegistryFacade) {  
    return !beanDefinitionRegistryFacade.containsBeanDefinition(RedisServerConfig.class.getName());  
    }  
}
```
使用的话只需要使用注解`EnableUselessBeanAutoRemoved`定义definer即可，同理也可以放在基类上。