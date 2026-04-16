package com.sar.HospitalManagement.security;

import com.sar.HospitalManagement.entity.type.RoleType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.SecurityFilterChain;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import static com.sar.HospitalManagement.entity.type.RoleType.*;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebSecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        return httpSecurity
                .csrf(csrfConfig -> csrfConfig.disable())
                .sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers("/public/**", "/auth/**").permitAll()   /*public path can available for everyone*/
                                //.requestMatchers("admin/**").authenticated() /*but admin path can only visible for authenticated user*/
                                .anyRequest().authenticated()
                                .requestMatchers("/admin/**").hasRole(ADMIN.name())   /* only admin can see through this path*/
                                .requestMatchers("/doctor/**").hasAnyRole(ADMIN.name(), DOCTOR.name()) /*Admin and doctor can see this path */
                                .requestMatchers("/patient/**").hasAnyRole(ADMIN.name(), PATIENT.name())
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                /*.formLogin(Customizer.withDefaults()) that default form not will appear*/
                /*By applying this we don't need to login for view data.*/
                /*We can add multiple Chain in security by default there are already added by spring boot.*/
                .oauth2Login(
                    oAuth2 -> oAuth2
                            .failureHandler(
                            (request, response, exception) -> {
                                //log.error("OAuth2 Exception : ", exception.getMessage());
                                handlerExceptionResolver.resolveException(request,response,null,exception);
                            })
                            .successHandler(oAuth2SuccessHandler)
                )
                .exceptionHandling(
                    exceptionHandlingConfigurer ->
                        exceptionHandlingConfigurer.accessDeniedHandler(((request, response, accessDeniedException) -> {
                            handlerExceptionResolver.resolveException(request,response,null,accessDeniedException);
                        }))
                )
                .build();
    }
}
