package com.example.grocerypickbot.security.aspect;

import com.example.grocerypickbot.exceptions.UnauthorizedException;
import com.example.grocerypickbot.security.annotation.RoleAccess;
import com.example.grocerypickbot.user.models.Role;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Aspect for enforcing role-based access using the @RoleAccess annotation.
 */
@Aspect
@Component
public class RoleAccessAspect {

  /**
   * Advice that checks if the authenticated user has one of the allowed roles before
   * executing methods or classes annotated with @RoleAccess.
   *
   * @param joinPoint the join point representing the method execution
   * @throws UnauthorizedException if the user does not have the required role
   */
  @Before("@annotation(com.example.grocerypickbot.security.annotation.RoleAccess)"
      + " || @within(com.example.grocerypickbot.security.annotation.RoleAccess)")
  public void checkRole(JoinPoint joinPoint)throws UnauthorizedException {
    Map<String, String> errors = new HashMap<>();

    RoleAccess roleAccess = getRoleAccessAnnotation(joinPoint);
    if (roleAccess == null) {
      return;
    }
    Role[] allowedRoles = roleAccess.allowedRoles();

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      errors.put("authentication", "User is not authenticated");
    }
    if (!errors.isEmpty()) {
      throw new UnauthorizedException(errors);
    }
    // Get user's authorities (roles)
    boolean hasRole = authentication.getAuthorities().stream()
        .anyMatch(auth -> Arrays.stream(allowedRoles)
            .anyMatch(role -> auth.getAuthority().equals("ROLE_" + role)));
    if (!hasRole) {
      errors.put("authentication", "User does not have required role");
    }

    if (!errors.isEmpty()) {
      throw new UnauthorizedException(errors);
    }
  }

  private RoleAccess getRoleAccessAnnotation(JoinPoint joinPoint) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    RoleAccess annotation = method.getAnnotation(RoleAccess.class);
    if (annotation == null) {
      annotation = joinPoint.getTarget().getClass().getAnnotation(RoleAccess.class);
    }
    return annotation;
  }
}
