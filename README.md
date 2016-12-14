#Spring 4.3

Some important concepts of Spring Framework

* Focus on POJO's that have a *Single Purpose*
* Program to interfaces

##Spring Configuration

* Java Configuration classes
* XML Configuration
* Annotations
* Testable code
* Low Coupling
* High Cohesion

Beware of duplicate bean definitions  
It is *not* illegal to define the same bean more than once. You will just get the last bean Spring sees defined.  
To prevent this behavior look at `@Resource`

###Java Configuration Class

functions should be declared `public`.  
Each bean is given a unique id / name.
Bean ids should not contain implementation detail.

```kotlin
@Configuration
open class RewardsConfig {

    @Autowired lateinit var dataSource: DataSource

    @Bean
    open fun rewardNetworkImpl(accountRepository: JdbcAccountRepository,
                          restaurantRepository: JdbcRestaurantRepository,
                          rewardRepository: JdbcRewardRepository) =
            RewardNetworkImpl(
                    accountRepository,
                    restaurantRepository,
                    rewardRepository
            )

    @Bean
    open fun accountRepository() = JdbcAccountRepository().apply { setDataSource(dataSource) }

    @Bean
    open fun restaurantRepository() = JdbcRestaurantRepository().apply { setDataSource(dataSource) }

    @Bean
    open fun rewardRepository() = JdbcRewardRepository().apply { setDataSource(dataSource) }
}
```

####Descriptors

You can define descriptors on the bean definitions

####Imports

Organize your `@Configuration` classes however you like.
Best practive: seperate out `application` beans from `infrastructure` beans.
You can `import` other configuration files with `@import` on top of the class declaration.

```kotlin
@Configuration
@Import(TestInfrastructureConfig::class)
open class RewardsConfig { ... }
```

####Start the context

//TODO : older ways creating the ApplicationContext

Start the `ApplicationContext`

```kotlin
val context: ApplicationContext = SpringApplication.run(RewardsConfig::class.java)
```

####Get beans from context

Get beans directly from the context
There are three ways:

- Classic way: cast is needed

```kotlin
val transferService = context.getBean("transferService") as TransferService
```

- Use typed method to avoid cast

```kotlin
val transferService = context.getBean("transferService", TransferService::class.java) as TransferService
```

- No need for bean id if type is unique

```kotlin
val rewardNetwork = rewardNetwork = context.getBean(RewardNetwork::class.java)
```

####Bean Scope
A scope defines how your beans are defined.  
Examples of scopes are:  
- singleton
- prototype
- session (new instance created per user session) 
- request (new instance per request)

Custom scopes are allowed, but are rarely used.

#####Singleton
Default a bean is scoped as a `singleton`.

```kotlin
@Bean 
fun accountService() : AccountService = AccountService() 

//Equivalent

@Bean
@Scope("singleton")
fun accountService() : AccountService = AccountService()
```

#####Prototype
A new instance is created every time bean is referenced

```kotlin
@Bean 
@Scope("prototype")
fun accountService() : AccountService = AccountService() 
```

### XML Configuration

Old way of configuring your beans.
Spring pushes java configuration instead of XML.
Matches it by type

// **TODO**

Downsides to XML configuration

##Property Values

Externalize your properties.  

###Environment

Get environmental variables from the runtime environment  
*Environment Variables* and *Java System Properties* are always populated automatically

```kotlin
@Bean
fun dataSource(): DataSource {
    val ds = BasicDataSource()
    ds.setDriverClassName(env.getProperty(DB_DRIVER))
    ds.setUrl(env.getProperty(DB_URL))
    ds.setUser(env.getProperty(DB_USER))
    ds.setPassword(env.getProperty(DB_PWD))
    return ds
}
```

Environment obtains values from *property sources*  
`@PropertySource` contributes additional properties

```kotlin
@Configuration
@PropertySource("classpath:/com/organization/config/app.properties")
@PropertySource("file:config/local.properties")
class ApplicationConfig {
    
    @Bean
    fun dataSource(
        @Value("${db.driver}") driver : String, 
        @Value("${db.url}")  url : String, 
        @Value("${db.user}") user : String, 
        @Value("${db.password}") password : String) {
    }
}
```

>How are these `${ ... }` evaluated?

They need a dedicated bean: `PropertySourcesPlaceholderConfigurer`    
**NOTE** A static bean otherwise this still won't work

```java
@Bean
public static PropertySourcesPlaceholderConfigurer pspc() {
    return new PropertySourcesPlaceholderConfigurer();
}
```

###Profiles
Beans can be grouped according to for example their environment they live in.  
>Beans without a profile will always be available / loaded

They way you define them is with the `@Profile` annotation.  
You can either place them on a class or a method header

```kotlin
@onfiguration
@Profile("dev")
class DevConfig
```

or 

```kotlin
@Bean(name="dataSource")
@Profile("dev")
@PropertySource("dev.properties") 
fun dataSrouceForDev() : DataSource
```

>`@Profile` can control which `@PropertySources` are included in the Environment

How do you active these profiles at run-time?

```bash
-Dspring.profiles.active=dev,jpa       #dev profile and jpa profile are active now. 
```

Or pragmatically

```java
System.setProperty("spring.profiles.active", "dev, jpa");
SpringApplication.run(AppConfig.class);
```

Under tests (and only then) you can use the `@ActiveProfiles`

```kotlin
@ActiveProfiles("dev, jpa")
class Test
```

##SPEL
