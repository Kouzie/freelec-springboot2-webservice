package com.kouzie.springboot.domain;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass //jpa entity 클래스들이 해당 클래스를 상속할때 상송클래스의 필드도 칼럼으로 인식하게함
@EntityListeners(AuditingEntityListener.class) //자동으로 create, update 시간 기록
// jpaConfig에 @EnableJpaAuditing 설정후 이용가능
public abstract class BaseTimeEntity {

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;
}
