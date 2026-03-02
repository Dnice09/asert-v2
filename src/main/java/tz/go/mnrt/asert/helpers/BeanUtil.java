package tz.go.mnrt.asert.helpers;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public final class BeanUtil {

    private static ApplicationContext context;

    private BeanUtil(ApplicationContext context) {
        BeanUtil.context = context;
    }

    public static <T> T getBean(Class<T> clazz) throws BeansException {

        Assert.state(context != null, "Spring context in the BeanUtil is not been initialized yet!");
        return context.getBean(clazz);
    }
}
