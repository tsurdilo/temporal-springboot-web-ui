package io.temporal.web.ui.controller;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.web.ui.model.QueryInfo;
import io.temporal.web.ui.model.SignalInfo;
import io.temporal.web.ui.model.StartInfo;
import io.temporal.web.ui.util.DialectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class TemporalWebDialectController {
    @Autowired
    private WorkflowClient workflowClient;

    @RequestMapping("/temporalshowhistory/{wfid}/{wfrunid}")
    public String showExecutionHistory(@PathVariable("wfid") String workflowId,
                                       @PathVariable("wfrunid") String workflowRunId,
                                       Model model) {
        model.addAttribute("workflowid",
                workflowId);
        model.addAttribute("workflowrunid",
                workflowRunId);

        model.addAttribute("workflowhistory",
                new DialectUtils(workflowClient).getWorkflowHistory(workflowId, workflowRunId));
        return "temporalwebdialect :: showhistorymodal";
    }

    @RequestMapping("/temporalsignalworkflow/{wfid}/{wfrunid}")
    public String showSignalWorkflow(@PathVariable("wfid") String workflowId,
                                       @PathVariable("wfrunid") String workflowRunId,
                                       Model model) {
        SignalInfo signalInfo = new SignalInfo();
        signalInfo.setWorkflowId(workflowId);
        signalInfo.setWorkflowRunId(workflowRunId);
        model.addAttribute("signalInfo", signalInfo);
        return "temporalwebdialect :: showsignalworkflowmodal";
    }

    @RequestMapping("/temporalqueryworkflow/{wfid}/{wfrunid}")
    public String showQueryWorkflow(@PathVariable("wfid") String workflowId,
                                     @PathVariable("wfrunid") String workflowRunId,
                                     Model model) {
        QueryInfo queryInfo = new QueryInfo();
        queryInfo.setWorkflowId(workflowId);
        queryInfo.setWorkflowRunId(workflowRunId);
        model.addAttribute("queryInfo", queryInfo);
        return "temporalwebdialect :: showqueryworkflowmodal";
    }


    @RequestMapping("/startexec")
    public String startExec(@ModelAttribute StartInfo startInfo,
                                       Model model) {
        WorkflowStub workflowStub =
                workflowClient.newUntypedWorkflowStub(startInfo.getWorkflowType(),
                        WorkflowOptions.newBuilder()
                                .setTaskQueue(startInfo.getTaskQueue())
                                .setWorkflowId(startInfo.getWorkflowId())
                                .build());

        if(startInfo.getInputJson() != null && startInfo.getInputJson().length() > 0) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonFactory factory = mapper.getFactory();
                JsonParser parser = factory.createParser(startInfo.getInputJson());
                JsonNode inputObj = mapper.readTree(parser);
                workflowStub.start(inputObj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            workflowStub.start();
        }

        return "redirect:/";
    }

    @RequestMapping("/signalwf")
    public String signalWf(@ModelAttribute SignalInfo signalInfo,
                            Model model) {

        WorkflowStub workflowStub =
                workflowClient.newUntypedWorkflowStub(signalInfo.getWorkflowId(),
                        Optional.of(signalInfo.getWorkflowRunId()),
                        Optional.empty());

        if(signalInfo.getSignalJson() != null && signalInfo.getSignalJson().length() > 0) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonFactory factory = mapper.getFactory();
                JsonParser parser = factory.createParser(signalInfo.getSignalJson());
                JsonNode signalObj = mapper.readTree(parser);
                workflowStub.signal(signalInfo.getSignalName(), signalObj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            workflowStub.signal(signalInfo.getSignalName());
        }

        sleep(500); // this is just for demo...
        return "redirect:/";
    }

    @ResponseBody
    @GetMapping("/querywf/{wfid}/{wfrunid}/{queryname}")
    public String queryWf(@PathVariable("wfid") String workflowId,
                           @PathVariable("wfrunid") String workflowRunId,
                           @PathVariable("queryname") String queryName) {

        WorkflowStub workflowStub =
                workflowClient.newUntypedWorkflowStub(workflowId,
                        Optional.of(workflowRunId),
                        Optional.empty());

        Object obj = workflowStub.query(queryName, Object.class);

        String out = obj.toString();
        // this be a big hack...fix
        out = out.replace("CloudEvent","");
        out = out.replace("JsonCloudEventData","");
        out = out.replace("JsonData","");
        return out;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String displayDemo(Model model) {
        model.addAttribute("startInfo", new StartInfo());
//        model.addAttribute("signalInfo", new SignalInfo());
        return "index";
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
