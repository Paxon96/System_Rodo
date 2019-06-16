package pl.p.lodz.system.rodo.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.*;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    DataSource dataSource;

    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
            .dataSource(dataSource)
            .usersByUsernameQuery("select login as principal, pass as credentials, true from user where login=?").passwordEncoder(dPasswordEncoder())
            .authoritiesByUsernameQuery("select user.login as principal, user.permission as role from user where login=?");
    }



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/", "/password").permitAll()
                .antMatchers("/marks").access("hasRole('ROLE_USER')")
                .antMatchers("/fileUpload").access("hasRole('ROLE_ADMIN')")
                .and()
                .formLogin()
                .loginPage("/password")
                .defaultSuccessUrl("/settings",true)
                .permitAll()
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/")
                .permitAll();
    }


//    @Bean
//    public RoleHierarchy roleHierarchy() {
//        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
//        roleHierarchy.setHierarchy(WorkerPermission.ROLE_ADMIN
//                                   + ">" + WorkerPermission.ROLE_MOD
//                                   + ">" + WorkerPermission.ROLE_ADVANCED
//                                   + ">" + WorkerPermission.ROLE_NORMAL
//                                   + ">" + WorkerPermission.ROLE_CLIENT
//                                   + ">" + WorkerPermission.ROLE_NONE);
//        return roleHierarchy;
//
//    }

//    @Bean
//    public RoleVoter roleVoter() {
//        return new RoleHierarchyVoter(roleHierarchy());
//    }
//
//    //    @Bean
//    //    public UserDetailsService userDetailsService() {
//    //        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//    //        manager.createUser(User.withUsername("user").password("{noop}user").roles("USER").build());
//    //        return manager;
//    //    }
//
    @Bean
    public JdbcUserDetailsManager jdbcUserDetailsManager() throws Exception {
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager();
        jdbcUserDetailsManager.setDataSource(dataSource);
        return jdbcUserDetailsManager;
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler();
        handler.setUseReferer(true);
        return handler;
    }
//
//    //    @Override
//    //    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//    //        auth.userDetailsService(jdbcUserDetailsManager());
//    //    }
//
//    //    @Bean
//    //    public JdbcUserDetailsManager jdbcUserDetailsManager() {
//    //        final Properties user = new Properties();
//    //        return new JdbcUserDetailsManager(user);
//    //    }
//
    @Bean
    public PasswordEncoder dPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
