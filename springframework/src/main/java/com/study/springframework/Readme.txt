Spring提供三种装配机制：
1、在XML中进行显示配置
2、在JAVA中进行显示配置
3、隐式的Bean发现机制和自动装配

自动化装配bean
组件扫描（component scanning）: spring会自动发现应用上下文中所创建的的bean
自动装配（autowiring）:Spring自动满足bean之间的依赖


spring Bean作用域：
单例（Singleton）:在整个应用中，只创建bean的一个实例
原型（Prototype）:每次注入或通过spring应用上下文获取的时候，都会创建一个新的bean实例
会话（Session）：在Web应用中，为每个会话创建一个bean实例
请求（Request）：在Web应用中，为每个请求创建一个bean实例


Spring提供4种类型的AOP支持：
1、基于代理的经典Spring AOP
2、纯POJO切面
3、@AspectJ注解驱动的切面
4、注入式AspectJ切面（适用于Spring的各种版本）