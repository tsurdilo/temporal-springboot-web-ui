package io.temporal.web.ui.util;

import com.google.protobuf.ByteString;
import io.temporal.api.workflow.v1.WorkflowExecutionInfo;
import io.temporal.api.workflowservice.v1.ListWorkflowExecutionsRequest;
import io.temporal.api.workflowservice.v1.ListWorkflowExecutionsResponse;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;

import java.util.ArrayList;
import java.util.List;

public class DialectUtils {
    public static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
    public static final WorkflowClient client = WorkflowClient.newInstance(service);

    public static List<WorkflowExecutionInfo> listExecutions(ByteString token, List<WorkflowExecutionInfo> info) {
        ListWorkflowExecutionsRequest request;
        if(info == null) {
            info = new ArrayList<>();
        }

        if(token == null) {
            request = ListWorkflowExecutionsRequest.newBuilder()
                    .setNamespace(client.getOptions().getNamespace())
//                    .setQuery(query)
                    .build();
        } else {
            request = ListWorkflowExecutionsRequest.newBuilder()
                    .setNamespace(client.getOptions().getNamespace())
//                    .setQuery(query)
                    .setNextPageToken(token)
                    .build();
        }
        ListWorkflowExecutionsResponse response =
                service.blockingStub().listWorkflowExecutions(request);

        info.addAll(response.getExecutionsList());

        if(response.getNextPageToken() != null && response.getNextPageToken().size() > 0) {
            return listExecutions(response.getNextPageToken(), info);
        } else {
            return info;
        }
    }

    public static boolean isExpression(String value) {
        return value != null && value.startsWith("${") && value.endsWith("}");
    }

    public static String getFragmentName(String value,
                                         String defaulValue,
                                         IStandardExpressionParser parser,
                                         ITemplateContext templateContext) {

        if (value == null || value.trim().length() < 1) {
            return defaulValue;
        } else {
            if (isExpression(value)) {
                IStandardExpression expression = parser.parseExpression(templateContext,
                        value);

                return (String) expression.execute(templateContext);
            } else {
                return value;
            }
        }
    }
}
