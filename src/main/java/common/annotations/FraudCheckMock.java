package common.annotations;

import api.mock.FraudCheckTestData;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface FraudCheckMock {

    FraudCheckTestData preset() default FraudCheckTestData.APPROVED_LOW_RISK;

    String status() default "";

    String decision() default "";

    double riskScore() default -1;

    String reason() default "";

    boolean requiresManualReview() default false;

    boolean additionalVerificationRequired() default false;

    int port() default 8087;

    String endpoint() default "/fraud-check";
}
