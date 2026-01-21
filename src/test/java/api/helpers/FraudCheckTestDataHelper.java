package api.helpers;

import api.mock.FraudCheckTestData;
import api.models.TransferResponseWithFraudCheckModel;

public class FraudCheckTestDataHelper {
    public static TransferResponseWithFraudCheckModel buildExpectedResponse(
            FraudCheckTestData preset,
            double amount,
            long senderAccountId,
            long receiverAccountId
    ) {
        return TransferResponseWithFraudCheckModel.builder()
                .status(preset.getDecision())
                .message(preset.getExpectedMessage())
                .amount(amount)
                .senderAccountId(senderAccountId)
                .receiverAccountId(receiverAccountId)
                .fraudRiskScore(preset.getRiskScore())
                .fraudReason(preset.getReason())
                .requiresManualReview(preset.isRequiresManualReview())
                .requiresVerification(preset.isAdditionalVerificationRequired())
                .build();
    }
}
