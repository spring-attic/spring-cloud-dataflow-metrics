/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.dataflow.metrics;

import java.util.Properties;

import org.springframework.boot.ApplicationPid;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.CommandLinePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;

public class PidEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private String pid;

    public PidEnvironmentPostProcessor() {
        this.pid = new ApplicationPid().toString();
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Properties properties = new Properties();
        properties.put("spring.cloud.application.pid", pid);
        MutablePropertySources propertySources = environment.getPropertySources();
        if (propertySources.contains(
                CommandLinePropertySource.COMMAND_LINE_PROPERTY_SOURCE_NAME)) {
            propertySources.addAfter(
                    CommandLinePropertySource.COMMAND_LINE_PROPERTY_SOURCE_NAME,
                    new PropertiesPropertySource("pid", properties));
        }
        else {
            propertySources
                    .addFirst(new PropertiesPropertySource("pid", properties));
        }
    }
}
