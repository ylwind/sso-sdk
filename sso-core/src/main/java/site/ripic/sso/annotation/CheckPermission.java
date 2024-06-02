package site.ripic.sso.annotation;

import site.ripic.sso.enums.LogicalOperators;

public @interface CheckPermission {

    String[] permissions() default {};

    LogicalOperators type() default LogicalOperators.AND;
}
