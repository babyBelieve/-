package cn.itcast.wanxinp2p.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.AuthenticationEntryPoint;

@Configuration
public class ResouceServerConfig {//配置类

    public static final String RESOURCE_ID = "wanxin-resource";

    private AuthenticationEntryPoint point = new RestOAuth2AuthExceptionEntryPoint();

    private RestAccessDeniedHandler handler = new RestAccessDeniedHandler();

    /**
     * 统一认证中心 资源拦截
     */
    @Configuration
    @EnableResourceServer //后端各个微服务都是 ==>资源服务器
    public class UAAServerConfig extends ResourceServerConfigurerAdapter {

        @Autowired
        private TokenStore tokenStore;

        @Override
        public void configure(ResourceServerSecurityConfigurer resources)
                throws Exception {
            resources.tokenStore(tokenStore).resourceId(RESOURCE_ID)
                    .stateless(true);
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {//校验访问端有没有权限
            http.sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .and()
                    .authorizeRequests()
                    .antMatchers("/uaa/druid/**").denyAll()
                    .antMatchers("/uaa/**").permitAll();
        }

    }


    /**
     * c端用户服务 资源拦截
     */
    @Configuration
    @EnableResourceServer
    public class ConsumerServerConfig extends ResourceServerConfigurerAdapter {//针对Consumer微服务

        @Autowired
        private TokenStore tokenStore;

        @Override
        public void configure(ResourceServerSecurityConfigurer resources)
                throws Exception {
            resources.tokenStore(tokenStore).resourceId(RESOURCE_ID)
                    .stateless(true);

            resources.authenticationEntryPoint(point).accessDeniedHandler(handler);
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .and()
                    .authorizeRequests()
                    .antMatchers("/consumer/l/**").denyAll()
                    .antMatchers("/consumer/my/**").access("#oauth2.hasScope('read') and #oauth2.clientHasRole('ROLE_CONSUMER')")//受保护的,需要具备相应的权限(ROLE_CONSUMER)才能访问
                    .antMatchers("/consumer/m/**").access("#oauth2.hasScope('read') and #oauth2.clientHasRole('ROLE_ADMIN')")//受保护的,需要具备相应的权限(ROLE_ADMIN)才能访问
                    .antMatchers("/consumer/**").permitAll();

        }

    }


}
