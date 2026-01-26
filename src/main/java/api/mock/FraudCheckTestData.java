package api.mock;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FraudCheckTestData  {
    APPROVED_LOW_RISK(
            "SUCCESS",
            "APPROVED",
            0.2,
            "Low risk transaction",
            false,
            false,
            "Transfer approved and processed immediately"
    );

    private final String status;
    private final String decision;
    private final double riskScore;
    private final String reason;
    private final boolean requiresManualReview;
    private final boolean additionalVerificationRequired;
    private final String expectedMessage;
}
