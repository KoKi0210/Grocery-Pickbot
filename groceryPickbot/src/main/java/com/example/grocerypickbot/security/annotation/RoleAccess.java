package com.example.grocerypickbot.security.annotation;

import com.example.grocerypickbot.user.models.Role;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to specify role-based access control on methods or classes.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RoleAccess {
  /**
   * Array of roles that are allowed to access the annotated method or class.
   */
  Role[] allowedRoles();
}
