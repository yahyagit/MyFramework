package framework.pagemapping.pagefactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FindInMap {
    String page() default "";

    String element() default "";

    String fullname() default "";
}
