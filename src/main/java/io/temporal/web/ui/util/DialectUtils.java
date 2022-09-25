package io.temporal.web.ui.util;

import com.google.protobuf.ByteString;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.workflow.v1.WorkflowExecutionInfo;
import io.temporal.api.workflowservice.v1.*;
import io.temporal.client.WorkflowClient;
import io.temporal.internal.common.WorkflowExecutionHistory;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;

import java.util.ArrayList;
import java.util.List;

@Component
public class DialectUtils {
    private WorkflowClient client;

    public DialectUtils(WorkflowClient client) {
        this.client = client;
    }

    public GetClusterInfoResponse getClusterInfo() {
        GetClusterInfoResponse res = client.getWorkflowServiceStubs().blockingStub().getClusterInfo(GetClusterInfoRequest.newBuilder()
                .build());
        return res;
    }

    public List<WorkflowExecutionInfo> listExecutions(ByteString token, List<WorkflowExecutionInfo> info) {
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
                client.getWorkflowServiceStubs().blockingStub().listWorkflowExecutions(request);

        info.addAll(response.getExecutionsList());

        if(response.getNextPageToken() != null && response.getNextPageToken().size() > 0) {
            return listExecutions(response.getNextPageToken(), info);
        } else {
            return info;
        }
    }

    public String getWorkflowHistory(String workflowId, String runId) {
        GetWorkflowExecutionHistoryRequest request =
                GetWorkflowExecutionHistoryRequest.newBuilder()
                        .setNamespace("default") // hard-coded :(
                        .setExecution(WorkflowExecution.newBuilder()
                                .setWorkflowId(workflowId)
                                .setRunId(runId)
                                .build())
                        .build();
        return new WorkflowExecutionHistory(
                client.getWorkflowServiceStubs().blockingStub().getWorkflowExecutionHistory(request).getHistory()).toJson(true);
    }

    public boolean isExpression(String value) {
        return value != null && value.startsWith("${") && value.endsWith("}");
    }

    public String getFragmentName(String value,
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
