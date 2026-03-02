package tz.go.mnrt.asert.annotations;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.Field;
import java.util.Arrays;

@Aspect
public class TitleCaseAspect {
    /**
     * Returns a titleCased {@link String} in title case.
     *
     * @param String input The the string we went to convert to titlecase
     * @return a {@String string}
     */
    private static String toTitleCase(String input) {
        if (input == null || input.length() == 0) {
            return "";
        }
        StringBuilder result = new StringBuilder(input.length());
        boolean capitalizeNext = true;
        for (char c : input.toCharArray()) {
            if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                c = Character.toTitleCase(c);
                capitalizeNext = false;
            } else {
                c = Character.toLowerCase(c);
            }
            result.append(c);
        }
        return result.toString();
    }

    @Pointcut("get(@tz.go.mnrt.asert.annotations.TitleCase * *)")
    public void getTitleCaseField() {
    }

    @AfterReturning(pointcut = "getTitleCaseField()", returning = "returnValue")
    public void toTitleCase(JoinPoint joinPoint, Object returnValue) throws IllegalAccessException {
        if (returnValue == null) {
            return;
        }

        if (returnValue instanceof String) {
            Field field = getField(joinPoint);
            field.setAccessible(true);
            field.set(joinPoint.getTarget(), toTitleCase((String) returnValue));
        }
    }

    /**
     * get the specified field
     */
    private Field getField(JoinPoint joinPoint) {
        String fieldName = joinPoint.getSignature().getName().substring(3);
        return Arrays.stream(joinPoint.getTarget().getClass().getDeclaredFields())
                .filter(field -> field.getName().equalsIgnoreCase(fieldName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Field not found: " + fieldName));
    }
}
