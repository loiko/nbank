package common.extensions;

import common.annotations.APIVersion;
import api.configs.Config;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Method;

public class APIVersionExtension implements ExecutionCondition {

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        String currentVersion = Config.getProperty("api.backend.version");

        if (currentVersion == null || currentVersion.isEmpty()) {
            return ConditionEvaluationResult.enabled("No API version configured, running all tests");
        }

        Method testMethod = context.getTestMethod().orElse(null);
        if (testMethod != null && testMethod.isAnnotationPresent(APIVersion.class)) {
            APIVersion annotation = testMethod.getAnnotation(APIVersion.class);
            return checkVersion(annotation.value(), currentVersion, "method");
        }

        Class<?> testClass = context.getTestClass().orElse(null);
        if (testClass != null && testClass.isAnnotationPresent(APIVersion.class)) {
            APIVersion annotation = testClass.getAnnotation(APIVersion.class);
            return checkVersion(annotation.value(), currentVersion, "class");
        }

        return ConditionEvaluationResult.enabled("No @APIVersion annotation found");
    }

    private ConditionEvaluationResult checkVersion(String requiredVersion, String currentVersion, String level) {
        if (requiredVersion.equals(currentVersion)) {
            return ConditionEvaluationResult.enabled(
                String.format("Test matches current API version '%s' (%s level)", currentVersion, level)
            );
        } else {
            return ConditionEvaluationResult.disabled(
                String.format("Test requires API version '%s' but current version is '%s' (%s level)",
                    requiredVersion, currentVersion, level)
            );
        }
    }
}
