package site.ripic.sso.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import site.ripic.sso.spring.aop.AnnoAspect;

@Configuration
@Import({AnnoAspect.class})
public class AutoConfiguration {
}
