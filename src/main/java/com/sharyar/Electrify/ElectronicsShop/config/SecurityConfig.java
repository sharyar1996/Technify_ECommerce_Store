package com.sharyar.Electrify.ElectronicsShop.config;

import com.sharyar.Electrify.ElectronicsShop.Security.JwtAuthenticationEntryPoint;
import com.sharyar.Electrify.ElectronicsShop.Security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true )
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private final String[] PUBLIC_URLS = {
            "/v3/api-docs",
            "/api/v1/auth/**",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-resources/**",
            "/v2/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html"
    } ;

    //ONLY FOR GETTING IN-MEMORY USERS, DOESNT WORK FOR USERS IN DATABASE
//    @Bean
//    public UserDetailsService userDetailsService()
//    {
//        UserDetails user1 = User.builder().username("shary")
//                .password(getPasswordEncoder().encode("1"))
//                .roles("USER").build();
//        UserDetails user2 = User.builder().username("mama")
//                .password(getPasswordEncoder().encode("1"))
//                .roles("ADMIN").build();
//        // create users
//        // InMemoryUserDetailsManager = implementation class of interface UserDetailsService
//
//        return new InMemoryUserDetailsManager(user1,user2);
//    }


    // FORM LOGIN AUTHENTICATION:
    // But in our project we have a separate frontend , so we
    // will not do form-based authentication in our project!
//    @Bean
//    SecurityFilterChain getFilterChain(HttpSecurity httpSecurity) throws Exception {
//
//        httpSecurity.authorizeHttpRequests((req)-> req.anyRequest().authenticated())
//                .formLogin((form)-> form.loginPage("login.html")
//                .loginProcessingUrl("/login").defaultSuccessUrl("/home")
//                .failureUrl("/error"))
//                .logout((form)-> form.logoutUrl("/logout"));
//
//        return httpSecurity.build();
//    }


    // BASIC AUTHENTICATION:
//    @Bean
//    SecurityFilterChain getFilterChain(HttpSecurity httpSecurity) throws Exception {
//
//        httpSecurity
//                .csrf((csrf) -> csrf.disable())
//                .cors(cors->cors.disable())
//                .authorizeHttpRequests((request) ->
//                request.anyRequest().authenticated())
//                .httpBasic(Customizer.withDefaults());
//
//        return httpSecurity.build();
//    }

    // We use Jwt Authentication in our project
    // JWT AUTHENTICATION:
    @Bean
    SecurityFilterChain getFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .csrf((csrf) -> csrf.disable())
                .cors(cors->cors.disable())
                .authorizeHttpRequests((requests) ->
                requests.requestMatchers("/auth/login").permitAll()
                        .requestMatchers( HttpMethod.POST, "/users/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/categories/**").permitAll()
                        .requestMatchers( HttpMethod.GET,"/products/**").permitAll()
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(e-> e.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(sessionManagement->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        httpSecurity.addFilterBefore(jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager getAuthenticationManager(
            AuthenticationConfiguration configuration) throws Exception {

       return configuration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider getAuthenticationProvider()
    {
       DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
       daoAuthenticationProvider.setUserDetailsService(this.userDetailsService);
       daoAuthenticationProvider.setPasswordEncoder(this.getPasswordEncoder());

       return daoAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder getPasswordEncoder()
    {
        return  PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


}
