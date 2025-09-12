package com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic;

import com.dibya.WorkflowAutomationEngine.Exception.ServiceException;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Executors.StepExecutor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Service
public class StepExecutorFactory {

    private final ApplicationContext ctx;

    private final Map<String, Supplier<StepExecutor>> providers = new ConcurrentHashMap<>();
    public StepExecutorFactory(ApplicationContext ctx) {
        this.ctx = ctx;
        refreshProviders();
    }

    private void refreshProviders() {
        // do not call getBean() — use bean names & lazy ObjectProvider creation
        String[] names = ctx.getBeanNamesForType(StepExecutor.class, true, false);
        for (String name : names) {
            providers.put(name, () -> ctx.getBean(name, StepExecutor.class));
        }
    }

    public  StepExecutor getStepExecutor(String stepType) {
        Supplier<StepExecutor> supplier = providers.get(stepType);
        if (supplier != null) {
            return supplier.get(); // resolves the specific named bean
        }
        throw new ServiceException("No StepExecutor found for step type: " + stepType,500);
    }
}
