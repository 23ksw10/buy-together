package com.together.buytogether.aop;

import com.together.buytogether.member.domain.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class LoginCheckAspect {
    @Pointcut("@annotation(com.together.buytogether.annotation.LoginRequired)")
    private void loginRequiredMethod() {
    }

    @Before("loginRequiredMethod()")
    public void checkLogin(JoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String requestURI = request.getRequestURI();
        HttpSession session = request.getSession();

        if (session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            response.sendRedirect("/members/sign-in?redirectURL=" + requestURI);
        }

    }
}
