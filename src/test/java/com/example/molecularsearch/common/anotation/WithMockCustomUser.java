package com.example.molecularsearch.common.anotation;

import com.example.molecularsearch.common.mock.WithMockCustomUserSecurityContextFactory;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* Mock 사용자를 만드는 어노테이션 */
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {

    String userName() default "1";
    String roleType() default "RULE_USER";
}
