package io.temporal.web.ui.dialect;

import io.temporal.client.WorkflowClient;
import io.temporal.web.ui.processor.ClusterInfoProcessor;
import io.temporal.web.ui.processor.ListWorkflowsProcessor;
import io.temporal.web.ui.processor.StartWorkflowProcessor;
import org.springframework.context.ApplicationContext;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;

import java.util.HashSet;
import java.util.Set;

public class TemporalWebDialect extends AbstractProcessorDialect {
    private static final String DIALECT_NAME = "TemporalWebDialect";

    private final ApplicationContext applicationContext;
    private WorkflowClient workflowClient;

    public TemporalWebDialect(ApplicationContext applicationContext, WorkflowClient workflowClient) {
        super(DIALECT_NAME,
                "temporalweb",
                StandardDialect.PROCESSOR_PRECEDENCE);
        this.applicationContext = applicationContext;
        this.workflowClient = workflowClient;
    }

    @Override
    public Set<IProcessor> getProcessors(final String dialectPrefix) {
        final Set<IProcessor> processors = new HashSet<>();

        processors.add(new ListWorkflowsProcessor(dialectPrefix,
                applicationContext, workflowClient));
        processors.add(new ClusterInfoProcessor(dialectPrefix,
                applicationContext, workflowClient));
        processors.add(new StartWorkflowProcessor(dialectPrefix,
                applicationContext, workflowClient));
        // add more processors here...

        return processors;
    }
}
