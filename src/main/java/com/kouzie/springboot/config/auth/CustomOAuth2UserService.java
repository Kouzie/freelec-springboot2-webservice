package com.kouzie.springboot.config.auth;

import com.kouzie.springboot.config.auth.dto.OAuthAttributes;
import com.kouzie.springboot.config.auth.dto.SessionUser;
import com.kouzie.springboot.domain.user.User;
import com.kouzie.springboot.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;

/**
 * 로그인 성공 후 후속조치, 세션에 사용자 정보 저장, 가입정보 db저장 등
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final HttpSession httpSession;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); //로그인 진행중인 서비스 구분 구글, 네이버 등
        String userNameAttributeName = userRequest
                .getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName(); //OAuth2 로 로그인 진행시 키가되는 필드값, 구글에서만 사용함

        OAuthAttributes attributes = OAuthAttributes.of( //oauth의 attribute 데이터를 저장하는 객체 naver, google, kakao 등의 데이터를 받아 OAuthAttributes 로취합한다
                registrationId,
                userNameAttributeName,
                oAuth2User.getAttributes()); // OAuthAttributes 에 OAuth2의 attributes 를 담음

        User user = saveOrUpdate(attributes); //OAuthAttributes 를 User 로 변환
        httpSession.setAttribute("user", new SessionUser(user)); //세션에 사용자 정보를 저장하기 위한 dto 객체, 세션에 저장하려면 serialize 를 구현해야 하기에 별도의 dto 를 사용한다

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey()
        );
    }

    private User saveOrUpdate(OAuthAttributes attributes) {
        User user = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
                .orElse(attributes.toEntity());

        return userRepository.save(user);
    }
}
