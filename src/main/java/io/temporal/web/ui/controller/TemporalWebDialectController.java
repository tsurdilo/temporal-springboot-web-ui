package io.temporal.web.ui.controller;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.web.ui.model.SignalInfo;
import io.temporal.web.ui.model.StartInfo;
import io.temporal.web.ui.util.DialectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
//        WorkflowStub workflowStub =
//                workflowClient.newUntypedWorkflowStub(startInfo.getWorkflowType(),
//                        WorkflowOptions.newBuilder()
//                                .setTaskQueue(startInfo.getTaskQueue())
//                                .setWorkflowId(startInfo.getWorkflowId())
//                                .build());
//
//        if(startInfo.getInputJson() != null && startInfo.getInputJson().length() > 0) {
//            try {
//                ObjectMapper mapper = new ObjectMapper();
//                JsonFactory factory = mapper.getFactory();
//                JsonParser parser = factory.createParser(startInfo.getInputJson());
//                JsonNode inputObj = mapper.readTree(parser);
//                workflowStub.start(inputObj);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
//            workflowStub.start();
//        }

        // todo finish signal code

        return "redirect:/";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String displayDemo(Model model) {
        model.addAttribute("startInfo", new StartInfo());
//        model.addAttribute("signalInfo", new SignalInfo());
        return "index";
    }
}
