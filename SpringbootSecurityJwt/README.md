### 项目
该项目主要基于Spring Security和JWT 设计的权限系统
### 科普一下JWT
**JSON Web Token (JWT)** 是在网络应用间传递信息的一种基于JSON的开放标准 **(RFC 7519)**，用于作为JSON对象在不同系统之间进行安全地信息传输。主要使用场景一般是用来在 身份提供者和服务提供者间传递被认证的用户身份信息。

JWT由三部分组成，依次如下：
- **Header（头部）** 包含两部分：token类型和采用的签名算法
- **Payload（负载）** 是Token携带的用户自定义内容，数据是base64，非加密，可破解，不能存敏感信息；内容越多，token越长
- **Signature（签名）** 将Header+Payload组成一个字符串，通过指定的签名算法进行计算，得到一个签名值，服务端可通过这个标志，来判断Token是否合法

##### #讲解Spring Security（附赠）
有JWT解决Token问题，再结合Spring Security 可以解决用户权限模块重重问题(真的很强大)，当然除了Spring Security，还有Apache Shiro也可以解决权限，但没有Security强大，后续将写篇文章详解对比两者。

### 实现方式
> ##### pom.xml中引入 JWT 和 Spring Security 的依赖 jar
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.9.1</version>
</dependency>
```
> ##### JWT工具类，生成Token、验证Token、刷新Token等
```
@Component
public class JwtTokenUtil {

    private static final String CLAIM_KEY_USERNAME = "sub";
    private static final String CLAIM_KEY_CREATED = "created";
    
    // 生成token
    public String generateToken(UserDetails userDetails) {
        ...
    }
    
    // 刷新token
    public String refreshToken(String token) {
        ...
    }
    
    // 校验token
    public Boolean validateToken(String token, UserDetails userDetails) {
        ...
    }
```
> ##### Token过滤器, 处理接口的token
```
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal (HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String authHeader = request.getHeader(Constant.HEADER_STRING );
        if (authHeader != null && authHeader.startsWith(Constant.TOKEN_PREFIX )) {
            final String authToken = authHeader.substring(Constant.TOKEN_PREFIX.length() );
            String username = jwtTokenUtil.getUsernameFromToken(authToken);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(
                            request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        chain.doFilter(request, response);
    }
}
```

> ##### Spring Security的核心配置
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userService;

    @Bean
    public JwtTokenFilter authenticationTokenFilterBean() throws Exception {
        return new JwtTokenFilter();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        String[] urls = new String[]{"/user/login", "/user/register"};
        httpSecurity.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                // 允许注册和登录接口不需token访问
                .antMatchers(urls).permitAll()
                .anyRequest().authenticated();
        httpSecurity.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
        httpSecurity.headers().cacheControl();
    }
}

> ##### 权限测试，接口需要限制访问限制，则添加 @PreAuthorize
@RestController
public class RoleController {

    /**
     * 测试普通权限
     *
     * @return
     */
    @PreAuthorize("hasAuthority('ROLE_NORMAL')")
    @GetMapping(value="/normal/test")
    public String test1() {
        return "普通角色访问";
    }

    /**
     * 测试管理员权限
     *
     * @return
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/admin/test")
    public String test2() {
        return "管理员访问";
    }
}

> 需给用户添加角色才能访问，如下图 用户角色关系表

![用户角色关系表](https://user-gold-cdn.xitu.io/2019/7/16/16bfb59993084f6f?w=284&h=110&f=png&s=7300)

### 测试效果
- 登录成功后返回token

![](https://user-gold-cdn.xitu.io/2019/7/16/16bfb5d1c2c7c94f?w=908&h=460&f=png&s=48836)

- 不带token的情况，会被拒绝访问

![](https://user-gold-cdn.xitu.io/2019/7/16/16bfb5ec77dbb20e?w=876&h=577&f=png&s=52706)

- 正确访问方式

![](https://user-gold-cdn.xitu.io/2019/7/16/16bfb6463522a404?w=938&h=525&f=png&s=50384)
