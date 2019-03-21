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
package org.springframework.cloud.dataflow.actuate.metrics.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.actuate.metrics.writer.Delta;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.cloud.dataflow.metrics.MetricsPrefixResolver;

public class LogMetricWriter implements MetricWriter {

    private static Logger log = LoggerFactory.getLogger(LogMetricWriter.class);

    private String prefix;

    public LogMetricWriter(MetricsPrefixResolver metricsPrefixResolver) {
        this.prefix = metricsPrefixResolver.getResolvedPrefix();
    }

    @Override
    public void increment(Delta<?> delta) {
        log.info("Incremented " + getLogMessage(delta));
    }

    @Override
    public void reset(String metricName) {
        // Not implemented
    }

    @Override
    public void set(Metric<?> metric) {
        log.info(getLogMessage(metric));
    }

    private String getLogMessage(Metric<?> metric) {
        return "Metric [name=" + prefix + "." + metric.getName() + ", value=" +
                metric.getValue() + ", timestamp=" + metric.getTimestamp() + "]";
    }
}
