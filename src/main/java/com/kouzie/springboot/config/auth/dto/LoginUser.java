package com.kouzie.springboot.config.auth.dto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 세션에서 login 정보를 받기 위한 커스텀 어노테이션 정의
 */
@Target(ElementType.PARAMETER) //파라미터에서만 사용할 수 있는 어노테이션
@Retention(RetentionPolicy.RUNTIME) //어노테이션 영향을 끼치는 범위, 컴파일 이후에 JVM에 의해 참조 가능
public @interface LoginUser {

}
