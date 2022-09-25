package io.temporal.web.ui.controller;

import io.temporal.client.WorkflowClient;
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

    @RequestMapping("/startexec")
    public String startExec(@ModelAttribute StartInfo startInfo,
                                       Model model) {

        System.out.println("********* START: " + startInfo.getWorkflowId() + " - " +
                startInfo.getWorkflowType());
        return "redirect:/";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String displayLogin(Model model) {
        model.addAttribute("startInfo", new StartInfo());
        return "index";
    }
}
