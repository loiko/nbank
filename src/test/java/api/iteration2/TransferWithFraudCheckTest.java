package api.iteration2;

import api.generators.RandomData;
import api.helpers.FraudCheckTestDataHelper;
import api.mock.FraudCheckTestData;
import api.models.TransferResponseWithFraudCheckModel;
import api.models.comparison.ModelAssertions;
import api.utilities.BaseTest;
import common.annotations.APIVersion;
import common.annotations.FraudCheckMock;
import common.annotations.UserSession;
import common.extensions.APIVersionExtension;
import common.extensions.FraudCheckWireMockExtension;
import common.extensions.UserSessionExtension;
import common.storage.SessionStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({FraudCheckWireMockExtension.class, UserSessionExtension.class, APIVersionExtension.class})
@APIVersion("with_fraud_check")
public class TransferWithFraudCheckTest extends BaseTest {

    @Test
    @UserSession(value = 2, accounts = {1, 1}, deposits = {2, 0})
    @FraudCheckMock(preset = FraudCheckTestData.APPROVED_LOW_RISK)
    public void testTransferWithFraudCheck() {
        long senderAccountId = SessionStorage.getFirstUserAccount().getId();
        long receiverAccountId = SessionStorage.getSecondUserAccount().getId();
        double transferAmount = RandomData.getValidTransferAmount();

        TransferResponseWithFraudCheckModel transferResponse = SessionStorage.getSteps().transferWithFraudCheck(
                senderAccountId,
                receiverAccountId,
                transferAmount
        );

        softly.assertThat(transferResponse).isNotNull();

        TransferResponseWithFraudCheckModel expectedResponse = FraudCheckTestDataHelper.buildExpectedResponse(
                FraudCheckTestData.APPROVED_LOW_RISK,
                transferAmount,
                senderAccountId,
                receiverAccountId);

        ModelAssertions.assertThatModels(expectedResponse, transferResponse).match();
    }
}
