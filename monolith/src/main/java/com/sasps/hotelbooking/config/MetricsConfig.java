package com.sasps.hotelbooking.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class MetricsConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config()
                .commonTags(
                        "application", applicationName,
                        "architecture", "monolithic",
                        "type", "single-process"
                )
                .meterFilter(MeterFilter.ignoreTags("instance"))
                .meterFilter(MeterFilter.deny(id -> {
                    String name = id.getName();
                    return name.startsWith("logback") || name.startsWith("log4j");
                }));
    }

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
}
