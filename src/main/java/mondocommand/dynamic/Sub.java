package mondocommand.dynamic;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Sub {
    String name() default "";
    String description() default "";
    String usage() default "";
    String permission() default "";
    int minArgs() default 0;
    boolean allowConsole() default true;
}
