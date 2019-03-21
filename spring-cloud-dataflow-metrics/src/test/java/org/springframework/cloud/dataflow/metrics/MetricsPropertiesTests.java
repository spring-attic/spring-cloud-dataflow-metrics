/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.dataflow.metrics;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.springframework.boot.ApplicationPid;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class MetricsPropertiesTests {

    private Properties defaultProperties = new Properties();

    private String pid = new ApplicationPid().toString();

    @Test
    public void testDefaults() {
        ApplicationContext context = getApplicationContext();
        assertThat(getResolvedMetricPrefix(context)).isEqualTo("group.application." + pid);
    }

    @Test
    public void testSettingGroupAndAppName() {
        populateDefaultGroupNameProperties();
        ApplicationContext context = getApplicationContext();
        assertThat(getResolvedMetricPrefix(context)).isEqualTo("mygroup.myapp." + pid);
    }

    @Test
    public void testVcapSupport() {
        populateDefaultGroupNameProperties();
        defaultProperties.put("vcap.application.instance_index", 1);
        ApplicationContext context = getApplicationContext();
        assertThat(getResolvedMetricPrefix(context)).isEqualTo("mygroup.myapp.1");
    }

    @Test
    public void testAppIndexSupport() {
        populateDefaultGroupNameProperties();
        defaultProperties.put("spring.application.index",2);
        ApplicationContext context = getApplicationContext();
        assertThat(getResolvedMetricPrefix(context)).isEqualTo("mygroup.myapp.2");
    }

    @Test
    public void testOverridePrefix() {
        defaultProperties.put("spring.cloud.application.group", "mygroup2");
        SpringApplication application = new SpringApplication(NotSoSimpleConfiguration.class);
        application.setDefaultProperties(defaultProperties);
        ApplicationContext context = application.run();
        assertThat(getResolvedMetricPrefix(context)).isEqualTo("mygroup2");
    }


    private void populateDefaultGroupNameProperties() {
        defaultProperties.put("spring.cloud.application.group", "mygroup");
        defaultProperties.put("spring.application.name", "myapp");
    }


    private String getResolvedMetricPrefix(ApplicationContext context) {
        MetricsPrefixResolver metricPrefixContextInitializer = context.getBean(MetricsPrefixResolver.class);
        assertThat(metricPrefixContextInitializer.getResolvedPrefix()).isNotNull();
        return metricPrefixContextInitializer.getResolvedPrefix();
    }

    private ApplicationContext getApplicationContext() {
        SpringApplication application = new SpringApplication(SimpleConfiguration.class);
        application.setDefaultProperties(defaultProperties);
        return application.run();
    }


    @Configuration
    static class SimpleConfiguration {

        @Bean
        public MetricsProperties metricsProperties() {
            return new MetricsProperties();
        }

        @Bean
        public MetricsPrefixResolver metricPrefixResolver() {
            return new MetricsPrefixResolver(metricsProperties());
        }
    }

    @Configuration
    static class NotSoSimpleConfiguration {
        @Bean
        public MetricsProperties metricsProperties() {
            MetricsProperties metricsProperties = new MetricsProperties();
            metricsProperties.setPrefix("${spring.cloud.application.group}");
            return metricsProperties;
        }

        @Bean
        public MetricsPrefixResolver metricPrefixResolver() {
            return new MetricsPrefixResolver(metricsProperties());
        }
    }

}
