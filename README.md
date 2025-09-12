Workflow Automation Engine

A lightweight, extensible workflow engine built with Spring (Java).
It executes workflows composed of typed steps where each step is handled by a StepExecutor. The project uses a StepExecutorFactory to lazily resolve executors by name and avoid circular bean-creation issues.

Key ideas / features

StepExecutor (interface) — unit of work executed for a step.

Concrete executors (examples in repo): EmailStepExecutor, WebHookStepExecutor, ConditionalStepExecutor.

StepExecutorFactory — resolves executors lazily (by bean name) to avoid circular dependency problems.

Execution context objects: WorkflowContext, StepContext, StepResult, and a Listener for lifecycle callbacks.

Designed to be easily extended — add new executors by implementing StepExecutor and registering them as Spring beans.

Quick start
Prerequisites

Java 17+ (or your project’s configured JDK)

Maven or Gradle (project uses typical Spring Boot layout — use whichever your repo contains)

(Optional) Docker, if you containerize the app

Build & run (Maven)
# build
mvn clean package

# run
java -jar target/workflow-engine-<version>.jar

Build & run (Gradle)
./gradlew build
java -jar build/libs/workflow-engine-<version>.jar

Project structure (high-level)

src/main/java/.../executors — concrete StepExecutor implementations (Email, Webhook, Conditional, etc.)

src/main/java/.../factory — StepExecutorFactory and related helpers

src/main/java/.../context — WorkflowContext, StepContext, StepResult, Listener interfaces

src/main/resources — application.properties / application.yml for configuration

How to add a new StepExecutor

Implement the StepExecutor interface:

@Component("myCustom")
public class MyCustomExecutor implements StepExecutor {
    @Override
    public StepResult execute(WorkflowContext wfCtx, StepContext stepCtx, Listener listener) {
        // ... do work
    }
}


Use the same bean name when you refer to the executor in workflows (e.g., myCustom).

If your executor needs other beans, @Autowired them as usual. Avoid resolving other executors during construction to prevent cyclic creation.

Example: Lazy resolution in factory (pattern used in repo)

The factory resolves executors by name on demand:

@Service
public class StepExecutorFactory {
    private final ApplicationContext ctx;
    private final Map<String, Supplier<StepExecutor>> providers = new ConcurrentHashMap<>();

    public StepExecutorFactory(ApplicationContext ctx) {
        this.ctx = ctx;
        refreshProviders();
    }

    private void refreshProviders() {
        String[] names = ctx.getBeanNamesForType(StepExecutor.class, true, false);
        for (String name : names) {
            providers.put(name.toUpperCase(), () -> ctx.getBean(name, StepExecutor.class));
        }
    }

    public StepExecutor getStepExecutor(String stepType) {
        Supplier<StepExecutor> s = providers.get(stepType == null ? null : stepType.toUpperCase());
        if (s == null) throw new ServiceException("No StepExecutor found for: " + stepType, 500);
        return s.get();
    }
}


This guarantees:

Lazy resolution (executor instances created only when needed).

Unambiguous selection when multiple beans implement StepExecutor.

Avoiding circular dependency — practical guidance

Common cause: ConditionalStepExecutor needs the factory, and factory eagerly collects dependencies. Use these patterns:

Preferred: Keep the factory holding providers or name-based Suppliers and resolve executors only at runtime (inside execute(...)) — not during construction.

Use ObjectProvider if you wish, but don’t call provider.getObject() in constructors or @PostConstruct.

Alternative: @Lazy injection on the factory parameter in ConditionalStepExecutor — quick workaround.

If proxies are involved and StepExecutor was a class, either convert it to an interface OR enable CGLIB proxies:

spring.aop.proxy-target-class=true

Bean naming / conventions

Bean name keys used by the factory are the Spring bean names (default: camelCase class name) or explicit @Component("NAME").

Choose a stable naming convention (e.g., lowercase email, webhook, conditional) or normalize stepType values (factory example uses toUpperCase()).

Be consistent between workflow definitions and registered bean names.

Troubleshooting (common issues)

Map injected into factory empty

Check component scanning & package layout. Ensure executor classes are detected by Spring.

Check that StepExecutor type is the same (same package/FQN) used by both executors and factory.

expected single matching bean but found N error

You called getObject() on a type-based provider when multiple beans exist. Use name-based lookup (ctx.getBean(name, type)).

Circular dependency error

Ensure lazy resolution (factory does not call provider during bean creation). Use ObjectProvider, @Lazy, or ApplicationContext-based suppliers.

Proxy type mismatch

If executors were classes and AOP created JDK proxies, make StepExecutor an interface or enable CGLIB proxies (spring.aop.proxy-target-class=true).

Testing

Unit test StepExecutorFactory by creating a mock ApplicationContext (Mockito) and a set of named StepExecutor beans.

Integration test workflows end-to-end using @SpringBootTest — assert that steps run in the expected order and produce expected StepResult.

Add coverage tests for failure branches (missing executor, invalid StepContext for an executor, etc.).

Logging & Observability

Use Listener implementations (e.g., LoggingListeners) to record step start/end/failure events with correlation IDs (workflowId, executionId).

Add metrics around execution time per executor for performance tracking.

Contributing

Fork the repo.

Create a feature branch: git checkout -b feature/<name>.

Write tests for new behavior.

Submit a PR with description & test evidence.
