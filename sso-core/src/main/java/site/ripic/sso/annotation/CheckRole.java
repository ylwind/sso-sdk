package site.ripic.sso.annotation;

import site.ripic.sso.enums.LogicalOperators;

public @interface CheckRole {

    String[] role() default {};

    LogicalOperators type() default LogicalOperators.AND;
}
