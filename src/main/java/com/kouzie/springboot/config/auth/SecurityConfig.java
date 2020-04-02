package com.kouzie.springboot.config.auth;

import com.kouzie.springboot.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                    .authorizeRequests() //url 별 권한관리 시작점
                    .antMatchers("/", "/login/**", "/css/**", "/images/**", "/js/**", "/h2-console/**", "/profile").permitAll()
                    .antMatchers("/api/v1/**").hasRole(Role.USER.name())
                    .anyRequest().authenticated() // 위의 url 외에는 모두 인증된 사용자는 가능
                .and()
                    .logout()
                    .logoutSuccessUrl("/")
                .and()
                    .oauth2Login() //oAuth 설정 진입점
                    .userInfoEndpoint() // oAuth 로그인 성공후 사용자 정보 가져오는 설정
                    .userService(customOAuth2UserService); //로그인 성공후 후속조치를 위한 OAuth2UserService 구현체, 리소스 서버로부터 사용자 정보를 가져온 후 추가진행
    }
}