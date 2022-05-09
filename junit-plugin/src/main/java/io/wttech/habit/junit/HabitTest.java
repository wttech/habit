package io.wttech.habit.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(HabitRunner.class)
@ExtendWith(RequestDSLParameterResolver.class)
@ExtendWith(HabitEnvironmentParameterResolver.class)
public @interface HabitTest {

}
