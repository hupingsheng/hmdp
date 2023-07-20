## 短信登录





### **登录状态的校验**

每次http请求访问都应该对登录状态进行校验【即是否是已登录用户】，这里合理的想到用拦截器对每一个controller的访问进行统一拦截。

![image-20230720220530145](readme/image-20230720220530145.png)

编写拦截器代码，编写一个类实现HandlerInterceptor

```java
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1. 服务器根据http request 获取之前创建的session
        HttpSession session = request.getSession();

        //  2. 获取session中的用户
        Object user = session.getAttribute("user");

        // 3. 判断用户是否存在
        if(user == null){
            response.setStatus(401);
            return false;
        }


        // 4. 存在，保存在ThreadLocal
        // 保存到threadlocal，是为了防止多线程下，每个线程【每个tomcat请求都是独立的线程】拥有一个独立的session实例，防止互相干扰
        UserHolder.saveUser((User)user);

        // 5. 放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        UserHolder.removeUser();

    }
}

```

值得注意的是，这里将获取到的登录用户保存到`ThreadLocal`下，考虑到一个web网站，同时会有多个用户进行访问，而对于每个访问，tomcat服务器都会从线程池中找一个线程来完成，所以为了避免当前的用户信息混淆，这里将用户信息保存到ThreadLocal，做到线程隔离。题外话，tomcat的默认最大线程数是150，当然了，可以修改配置，将其调大一点。根据实际的硬件上限等确定



将刚才的拦截器进行统一注册。使其生效

```java
registry.addInterceptor(new LoginInterceptor())
```

具体代码如下

```java
@Configuration
public class MvcConfig implements WebMvcConfigurer {


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 登录拦截器new LoginInterceptor()
        registry.addInterceptor(new LoginInterceptor())
                //排除不需要的拦截器
                .excludePathPatterns(
                        "/shop/**",
                        "/voucher/**",
                        "/shop-type/**",
                        "/upload/**",
                        "/blog/hot",
                        "/user/code",
                        "/user/login"
                ).order(1);
    }
}
```





### **关于当前登录用户信息的的session存储的细粒度的控制**

优化前

![image-20230720223109776](readme/image-20230720223109776.png)

优化后

![image-20230720223056276](readme/image-20230720223056276.png)

只存储必要的信息，比如id

- 一方面存储少是为了信息的安全考虑，避免将密码等敏感信息泄漏出去

- 另一方面，当前的登陆用户信息都要存储到session和ThreadLocal的tomcat服务器中，存储过多信息，占用内存空间。

因此在登录后的`session.setAttribute("user", user);`时就要修改，后续的存入ThreadLocal自然就是只存必要的属性

更改为

```java
import cn.hutool.core.bean.BeanUtil;

session.setAttribute("user", BeanUtil.copyProperties(user, UserDTO.class));
```

```java
@Data
public class UserDTO {
    private Long id;
    private String nickName;
    private String icon;
}
```

其中`BeanUtil.copyProperties`是hutool的一个工具类，是将类的属性进行有选择性的赋值，将需要的对象属性构成一个新的类，这个工具会自动将类中的属性选择性赋值。ps：盲猜利用反射完成。