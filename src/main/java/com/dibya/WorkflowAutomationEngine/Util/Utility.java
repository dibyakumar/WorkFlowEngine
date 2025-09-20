package com.dibya.WorkflowAutomationEngine.Util;

import com.dibya.WorkflowAutomationEngine.Entity.Step;
import com.dibya.WorkflowAutomationEngine.Entity.Workflow;
import com.dibya.WorkflowAutomationEngine.Repo.StepRepository;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Executors.Context.ConditionalStepContext;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Executors.Context.EmailStepContext;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Executors.Context.StepContext;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Executors.Context.WebhookStepContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Component
public class Utility {

    @Autowired
    private StepRepository stepRepository;

    public synchronized String getEmailBody(String body){
        return "<!doctype html>\n" +
                "<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                "<head>\n" +
                "  <meta charset=\"utf-8\">\n" +
                "  <title>{{subject}}</title>\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "  <meta name=\"x-apple-disable-message-reformatting\">\n" +
                "  <style>\n" +
                "    /* Mobile tweaks */\n" +
                "    @media only screen and (max-width:600px){\n" +
                "      .container{width:100% !important; padding:0 16px !important;}\n" +
                "      .p-sm{padding:16px !important;}\n" +
                "      .center-sm{text-align:center !important;}\n" +
                "      .stack-sm{display:block !important; width:100% !important;}\n" +
                "    }\n" +
                "    /* Dark mode hint */\n" +
                "    @media (prefers-color-scheme: dark){\n" +
                "      body{background:#0f1115 !important; color:#e6e7ea !important;}\n" +
                "      .card{background:#151823 !important; border-color:#2a2f3a !important;}\n" +
                "      .muted{color:#a9afbc !important;}\n" +
                "      .btn{background:#3b82f6 !important;}\n" +
                "      a{color:#8ab4ff !important;}\n" +
                "    }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body style=\"margin:0; padding:0; background:#f4f6f8; color:#111; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Arial, sans-serif;\">\n" +
                "  <!-- Preheader (shows in inbox preview, hidden in body) -->\n" +
                "  <div style=\"display:none; max-height:0; overflow:hidden; opacity:0; mso-hide:all;\">\n" +
                "    {{preheader}} <!-- keep under ~90 chars -->\n" +
                "  </div>\n" +
                "\n" +
                "  <table role=\"presentation\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\n" +
                "    <tr>\n" +
                "      <td align=\"center\" style=\"padding:24px;\">\n" +
                "        <table role=\"presentation\" class=\"container\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"600\" style=\"width:600px; max-width:600px;\">\n" +
                "          <!-- Header -->\n" +
                "          <tr>\n" +
                "            <td class=\"p-sm\" style=\"padding:24px 24px 0 24px; text-align:left;\">\n" +
                "              <!-- Optional logo -->\n" +
                "              <!-- <img src=\"{{logo_url}}\" alt=\"{{brand_name}}\" width=\"120\" style=\"display:block; border:0; outline:none; text-decoration:none;\"> -->\n" +
                "              <h1 style=\"margin:12px 0 0; font-size:20px; line-height:1.35; font-weight:600; color:#111;\">\n" +
                "                {{subject}}\n" +
                "              </h1>\n" +
                "              <p class=\"muted\" style=\"margin:8px 0 0; font-size:13px; color:#6b7280;\">\n" +
                "              </p>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "\n" +
                "          <!-- Card -->\n" +
                "          <tr>\n" +
                "            <td style=\"padding:16px 24px 24px;\">\n" +
                "              <table role=\"presentation\" width=\"100%\" class=\"card\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" style=\"background:#ffffff; border:1px solid #e5e7eb; border-radius:12px;\">\n" +
                "                <tr>\n" +
                "                  <td class=\"p-sm\" style=\"padding:24px;\">\n" +
                "                    <!-- Greeting (optional) -->\n" +
                "                    <p style=\"margin:0 0 12px; font-size:16px;\">Hi There,</p>\n" +
                "\n" +
                "                    <!-- Body HTML (safe, sanitized) -->\n" +
                "                    <div style=\"font-size:15px; line-height:1.6;\">\n" +
                "                     "+body+" \n" +
                "                    </div>\n" +
                "\n" +
                "                  \n" +
                "\n" +
                "                    <!-- Secondary note (optional) -->\n" +
                "                    <p class=\"muted\" style=\"margin:24px 0 0; font-size:12px; color:#6b7280;\">\n" +
                "                      Thanks and Regards.\n" +
                "                    </p>\n" +
                "                  </td>\n" +
                "                </tr>\n" +
                "              </table>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "\n" +
                "          <!-- Footer -->\n" +
                "          <tr>\n" +
                "            <td class=\"p-sm center-sm\" style=\"padding:0 24px 24px; text-align:left;\">\n" +
                "              <p class=\"muted\" style=\"margin:0 0 6px; font-size:12px; color:#6b7280;\">\n" +
                "                dibya · com\n" +
                "              </p>\n" +
                "              <p class=\"muted\" style=\"margin:0; font-size:12px; color:#6b7280;\">\n" +
                "                <a href=\"{{manage_prefs_url}}\" style=\"color:#6b7280; text-decoration:underline;\">Manage preferences</a> ·\n" +
                "                <a href=\"{{unsubscribe_url}}\" style=\"color:#6b7280; text-decoration:underline;\">Unsubscribe</a>\n" +
                "              </p>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "\n" +
                "          <!-- Legal (optional) -->\n" +
                "          <tr>\n" +
                "            <td class=\"p-sm center-sm\" style=\"padding:0 24px 24px; text-align:left;\">\n" +
                "              <p class=\"muted\" style=\"margin:0; font-size:11px; color:#9ca3af;\">\n" +
                "                @Copyright act 2025\n" +
                "              </p>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "\n" +
                "        </table>\n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </table>\n" +
                "\n" +
                "  <!--[if mso]>\n" +
                "  <style type=\"text/css\">\n" +
                "    .card { border-radius: 0 !important; }\n" +
                "  </style>\n" +
                "  <![endif]-->\n" +
                "</body>\n" +
                "</html>\n";
    }


    public String getCurrentLoggedInUser(){
        String username = Objects.requireNonNull(SecurityContextHolder.getContext()
                .getAuthentication()).getName();
        return null != username ? username : "";
    }

    public Step getStepFromStepContext(StepContext stepContext, Workflow workflowSaved) {
      Step step = new Step();
      if("COND".equalsIgnoreCase(stepContext.getStepType())){
          ConditionalStepContext conditionalStepContext = (ConditionalStepContext) stepContext;
          step.setType(stepContext.getStepType());
          step.setWorkflowId(workflowSaved);
          StepContext trueStepContext = conditionalStepContext.getTrueStepContext();
          StepContext falseStepContext = conditionalStepContext.getFalseStepContext();
            if(null != trueStepContext && null != falseStepContext){
                Step trueStep = stepRepository.save(getStepFromStepContext(trueStepContext, workflowSaved));
                Step falseStep = stepRepository.save(getStepFromStepContext(falseStepContext, workflowSaved));
                step.setTrueStep(trueStep);
                step.setFalseStep(falseStep);
            }else{
                throw new IllegalArgumentException("Both trueStep and falseStep must be provided for a conditional step.");
            }
          step.setCreatedAt(LocalDate.now());
            // TODO Need to change the ConditionalStepContext to store only store its own config without nested trueStep and falseStep
            // as they are already stored in DB as separate steps and linked via trueStep and false
          step.setConfig(conditionalStepContext);
      }else{
        step.setType(stepContext.getStepType());
          StepContext context = ("WEBHOOK".equalsIgnoreCase(step.getType()) ? (WebhookStepContext)stepContext :
                  "EMAIL".equalsIgnoreCase(step.getType()) ? (EmailStepContext) stepContext : null);
        if(null != context)
          step.setConfig(context);
        step.setWorkflowId(workflowSaved);
        step.setCreatedAt(LocalDate.now());
    }
      return step;
    }


    public StepContext stepToStepContextMapper(Step step,Map<String,Object> input){
        String type = step.getType();
        switch (type){
            case "WEBHOOK":
                WebhookStepContext apiWebContext = (WebhookStepContext) step.getConfig();
                Map<String, String> responseMappings = apiWebContext.getResponseMappings();
                for(String key: responseMappings.keySet()){
                    if("headers".equalsIgnoreCase(key)) {
                        if(null != input.get(key) && input.get(key) instanceof Map)
                            apiWebContext.setHeaders((Map<String, String>) input.get(key));
                    }
                    }
                apiWebContext.setStepId(step.getId());
                return  apiWebContext;
            case  "EMAIL":
                EmailStepContext emailStepContext = (EmailStepContext) step.getConfig();
                emailStepContext.setSubject(input.getOrDefault("subject",emailStepContext.getSubject()).toString());
                emailStepContext.setBody(input.getOrDefault("body",emailStepContext.getBody()).toString());
                emailStepContext.setEmailAddress(input.getOrDefault("email",emailStepContext.getEmailAddress()).toString());
                emailStepContext.setStepId(step.getId());
                return emailStepContext;
            case "COND":
                ConditionalStepContext conditionalStepContext = (ConditionalStepContext) step.getConfig();
                Optional<Step> trueStep = stepRepository.findById(step.getTrueStep().getId());
                Optional<Step> falseStep = stepRepository.findById(step.getFalseStep().getId());
                if(trueStep.isPresent() && falseStep.isPresent()){
                    conditionalStepContext.setTrueStepContext(stepToStepContextMapper(trueStep.get(), input));
                    conditionalStepContext.setFalseStepContext(stepToStepContextMapper(falseStep.get(), input));
                }
                conditionalStepContext.setStepId(step.getId());
                return conditionalStepContext;
            default:
                return null;
        }
    }

    /*
        This method finds the root step from a list of steps.
        The root step is defined as the step which is not referenced as a next step by any other step.
     */
    public Step findRootStep(List<Step> allSteps){

        // filter out all steps which are present in false or true step of any other step

        Map<Step,Boolean> map = new HashMap<>();
        for(Step step: allSteps){
            map.put(step,false);
        }
        for(Step step: allSteps){
            if(null != step.getNextStep()){
                map.put(step.getNextStep(),true);
            }
        }
        for(Step step: map.keySet()){
            if(!map.get(step)){
                return step;
            }
        }
        return null;
    }

  public List<Step> filterSubSteps(List<Step> allSteps){
        // steps in true_step_id or false_step_id column considered as sub-steps
        List<Step> subSteps = new ArrayList<>();
        for(Step step : allSteps){
            if(null != step.getTrueStep()){
                subSteps.add(step.getTrueStep());
            }
            if(null != step.getFalseStep()){
                subSteps.add(step.getFalseStep());
            }
        }

        return allSteps.stream().filter(step->!subSteps.contains(step)).toList();
  }
}
