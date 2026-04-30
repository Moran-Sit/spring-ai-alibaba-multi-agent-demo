package com.alibaba.cloud.ai.demo.common.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.util.List;

/**
 * @author Wolf
 * @description DefaultConfigEnvironmentPostProcessor
 * @date 2026/4/28 14:42
 */
public class DefaultConfigEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String DEFAULT_CONFIG_LOCATION = "classpath*:default-config/application.yml";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources(DEFAULT_CONFIG_LOCATION);

            if (resources.length == 0) {
                return;
            }

            YamlPropertySourceLoader loader = new YamlPropertySourceLoader();

            for (Resource resource : resources) {
                if (!resource.exists()) {
                    continue;
                }

                // 一个 yml 可能拆成多个 PropertySource
                List<PropertySource<?>> propertySources = loader.load(resource.getFilename(), resource);

                for (PropertySource<?> ps : propertySources) {
                    // 放在最后，优先级最低（允许业务覆盖）
                    environment.getPropertySources().addLast(ps);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("加载 default-config 失败", e);
        }
    }

    /**
     * 控制执行顺序（越小越早）
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
