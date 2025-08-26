package com.bravos.steak.common.annotation;

import com.bravos.steak.exceptions.ForbiddenException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class HasAuthorityAspect {

    private final AuthorityChecker authorityChecker;

    public HasAuthorityAspect(AuthorityChecker authorityChecker) {
        this.authorityChecker = authorityChecker;
    }

    @Around("@annotation(hasAuthority)")
    public Object checkAuthority(ProceedingJoinPoint joinPoint, HasAuthority hasAuthority) throws Throwable {
        if (!authorityChecker.hasAnyAuthority(hasAuthority.value())) {
            throw new ForbiddenException("Access denied");
        }
        return joinPoint.proceed();
    }

}