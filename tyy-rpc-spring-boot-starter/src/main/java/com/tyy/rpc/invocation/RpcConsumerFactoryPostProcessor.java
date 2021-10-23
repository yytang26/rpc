package com.tyy.rpc.invocation;

import com.fasterxml.jackson.databind.util.ClassUtil;
import com.tyy.rpc.annotation.RpcConsumer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author:tyy
 * @date:2021/7/11
 */
public class RpcConsumerFactoryPostProcessor implements BeanClassLoaderAware, EnvironmentAware, BeanFactoryPostProcessor, ApplicationContextAware {

    private ClassLoader classLoader;
    private ConfigurableEnvironment environment;
    private ApplicationContext context;
    private ConfigurableListableBeanFactory beanFactory;

    private Map<String, BeanDefinition> beanDefinitions = new LinkedHashMap<>();

    public RpcConsumerFactoryPostProcessor() {
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        postProcessBeanFactory(beanFactory, (BeanDefinitionRegistry) beanFactory);
    }

    private void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory, BeanDefinitionRegistry beanDefinitionRegistry) {
        String[] beanDefinitionNames = beanDefinitionRegistry.getBeanDefinitionNames();
        int len = beanDefinitionNames.length;
        for (int i = 0; i < len; i++) {
            String beanDefinitionName = beanDefinitionNames[i];
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);

            String beanClassName = beanDefinition.getBeanClassName();
            if (beanClassName != null) {
                Class<?> clazz = ClassUtils.resolveClassName(beanClassName, classLoader);
                ReflectionUtils.doWithFields(clazz, new ReflectionUtils.FieldCallback() {
                    @Override
                    public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                        parseFiled(field);
                    }
                });
            }
        }

        Iterator<Map.Entry<String, BeanDefinition>> it = beanDefinitions.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, BeanDefinition> entry = it.next();
            if (context.containsBean(entry.getKey())) {
                throw new IllegalArgumentException("Spring context already has a" +
                        "ben named" + entry.getKey());
            }
            beanDefinitionRegistry.registerBeanDefinition(entry.getKey(), entry.getValue());
        }
    }

    private void parseFiled(Field field) {
        RpcConsumer rpcConsumer = field.getAnnotation(RpcConsumer.class);
        if (rpcConsumer != null) {

            RpcConsumerBeanDefinitionBuilder beanDefinitionBuilder = new RpcConsumerBeanDefinitionBuilder(field.getType(), rpcConsumer);
            BeanDefinition beanDefinition = beanDefinitionBuilder.build();
            beanDefinitions.put(field.getName(), beanDefinition);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }
}
