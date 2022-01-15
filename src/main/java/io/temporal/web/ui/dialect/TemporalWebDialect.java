package io.temporal.web.ui.dialect;

import io.temporal.web.ui.processor.ListWorkflowsProcessor;
import org.springframework.context.ApplicationContext;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;

import java.util.HashSet;
import java.util.Set;

public class TemporalWebDialect extends AbstractProcessorDialect {
    private static final String DIALECT_NAME = "TemporalWebDialect";

    private final ApplicationContext applicationContext;

    public TemporalWebDialect(ApplicationContext applicationContext) {
        super(DIALECT_NAME,
                "temporalweb",
                StandardDialect.PROCESSOR_PRECEDENCE);
        this.applicationContext = applicationContext;
    }

    @Override
    public Set<IProcessor> getProcessors(final String dialectPrefix) {
        final Set<IProcessor> processors = new HashSet<>();
        processors.add(new ListWorkflowsProcessor(dialectPrefix,
                applicationContext));
        // add more processors here...

        return processors;
    }
}
