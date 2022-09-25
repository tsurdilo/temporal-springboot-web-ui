package io.temporal.web.ui.config;

import io.temporal.client.WorkflowClient;
import io.temporal.web.ui.dialect.
        TemporalWebDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:temporaldialect.properties")
public class TemporalWebDialectConfig {
    @Autowired
    private ApplicationContext context;

    @Autowired
    private WorkflowClient workflowClient;

    @Bean
    public TemporalWebDialect temporalWebDialect() {
        return new TemporalWebDialect(context, workflowClient);
    }
}
