package ui.iteration2;

import api.generators.RandomData;
import api.models.AccountResponseModel;
import api.testdata.DepositTestData;
import api.testdata.TransferTestData;
import common.annotations.APIVersion;
import common.annotations.UserSession;
import common.extensions.APIVersionExtension;
import common.storage.SessionStorage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ui.utilities.BaseUiTest;
import ui.pages.BankAlert;
import ui.pages.TransferPage;
import ui.pages.UserDashboard;
import ui.testdata.TestData;
import ui.utils.Utils;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.within;

@ExtendWith(APIVersionExtension.class)
@APIVersion("with_validation_fix")
public class TransferMoneyUserTest extends BaseUiTest {

    @Test
    @UserSession
    public void userCanTransferValidAmountTest() {
        double transferAmount = RandomData.getValidDepositAmount();
        String transferAmountUi = Utils.formatMoney(transferAmount);

        AccountResponseModel firstAccount = SessionStorage.getSteps().createAccount();
        AccountResponseModel secondAccount = SessionStorage.getSteps().createAccount();

        SessionStorage.getSteps().depositExtension(firstAccount.getId(), DepositTestData.MAX_VALID_DEPOSIT_AMOUNT, 2);

        double firstAccountBalanceBeforeTransfer = SessionStorage.getSteps().getAccount(firstAccount.getId()).getBalance();

        new UserDashboard()
                .open()
                .goToTransferPage()
                .selectAccount(firstAccount.getAccountNumber())
                .enterRecipientAccountNumber(secondAccount.getAccountNumber())
                .enterAmount(transferAmountUi)
                .confirmDetails()
                .clickSendTransferButton()
                .checkAlertMessageAndAccept(BankAlert.SUCCESSFULLY_TRANSFERRED.format(transferAmountUi, secondAccount.getAccountNumber()))
                .refreshPage();

        double firstAccountBalance = SessionStorage.getSteps().getAccount(firstAccount.getId()).getBalance();
        double secondAccountBalance = SessionStorage.getSteps().getAccount(secondAccount.getId()).getBalance();
        softly.assertThat(firstAccountBalance).isCloseTo((firstAccountBalanceBeforeTransfer - transferAmount), within(TransferTestData.MONEY_DELTA));
        softly.assertThat(secondAccountBalance).isCloseTo((transferAmount), within(TransferTestData.MONEY_DELTA));

        TransferPage transferPage = new TransferPage();
        softly.assertThat(transferPage.getAccountBalance(firstAccount.getAccountNumber()).getText()
        ).contains(Utils.formatMoney(firstAccountBalance));
        softly.assertThat(transferPage.getAccountBalance(secondAccount.getAccountNumber()).getText()
        ).contains(Utils.formatMoney(secondAccountBalance));
    }

    @Test
    @UserSession
    public void userCannotTransferMoneyWithoutSelectingAccountTest() {
        double transferAmount = RandomData.getValidDepositAmount();
        String transferAmountUi = Utils.formatMoney(transferAmount);

        AccountResponseModel firstAccount = SessionStorage.getSteps().createAccount();
        AccountResponseModel secondAccount = SessionStorage.getSteps().createAccount();

        SessionStorage.getSteps().depositExtension(firstAccount.getId(), DepositTestData.MAX_VALID_DEPOSIT_AMOUNT, 2);

        double firstAccountBalanceBeforeTransfer = SessionStorage.getSteps().getAccount(firstAccount.getId()).getBalance();
        double secondAccountBalanceBeforeTransfer = SessionStorage.getSteps().getAccount(secondAccount.getId()).getBalance();

        new UserDashboard()
                .open()
                .goToTransferPage()
                .enterRecipientAccountNumber(secondAccount.getAccountNumber())
                .enterAmount(transferAmountUi)
                .confirmDetails()
                .clickSendTransferButton()
                .checkAlertMessageAndAccept(BankAlert.ALL_FIELDS_ERROR.getMessage())
                .refreshPage();

        double firstAccountBalance = SessionStorage.getSteps().getAccount(firstAccount.getId()).getBalance();
        double secondAccountBalance = SessionStorage.getSteps().getAccount(secondAccount.getId()).getBalance();
        softly.assertThat(firstAccountBalance).isEqualTo(firstAccountBalanceBeforeTransfer);
        softly.assertThat(secondAccountBalance).isEqualTo(secondAccountBalanceBeforeTransfer);

        TransferPage transferPage = new TransferPage();
        softly.assertThat(transferPage.getAccountBalance(firstAccount.getAccountNumber()).getText()
        ).contains(Utils.formatMoney(firstAccountBalance));
        softly.assertThat(transferPage.getAccountBalance(secondAccount.getAccountNumber()).getText()
        ).contains(Utils.formatMoney(secondAccountBalance));
    }

    @Test
    @UserSession()
    public void userCannotTransferToNonExistentAccountTest() {
        double transferAmount = RandomData.getValidDepositAmount();
        String transferAmountUi = Utils.formatMoney(transferAmount);

        AccountResponseModel userAccount = SessionStorage.getSteps().createAccount();

        SessionStorage.getSteps().depositExtension(userAccount.getId(), DepositTestData.MAX_VALID_DEPOSIT_AMOUNT, 2);

        double userAccountBalanceBeforeTransfer = SessionStorage.getSteps().getAccount(userAccount.getId()).getBalance();

        new UserDashboard()
                .open()
                .goToTransferPage()
                .selectAccount(userAccount.getAccountNumber())
                .enterRecipientAccountNumber(RandomData.getInvalidAccountNumber())
                .enterAmount(transferAmountUi)
                .confirmDetails()
                .clickSendTransferButton()
                .checkAlertMessageAndAccept(BankAlert.INVALID_ACCOUNT_ERROR.getMessage())
                .refreshPage();

        double userAccountBalance = SessionStorage.getSteps().getAccount(userAccount.getId()).getBalance();
        softly.assertThat(userAccountBalance).isEqualTo(userAccountBalanceBeforeTransfer);

        TransferPage transferPage = new TransferPage();
        softly.assertThat(transferPage.getAccountBalance(userAccount.getAccountNumber()).getText()
        ).contains(Utils.formatMoney(userAccountBalance));
    }

    public static Stream<Arguments> invalidTransferAmount() {
        return Stream.of(
                Arguments.of(TransferTestData.BELOW_MIN_AMOUNT, BankAlert.MIN_AMOUNT_ERROR, 3),
                Arguments.of(TransferTestData.ABOVE_MAX_AMOUNT, BankAlert.MAX_AMOUNT_ERROR, 3),
                Arguments.of(DepositTestData.ABOVE_MAX_AMOUNT, BankAlert.INVALID_TRANSFER_ERROR, 1)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidTransferAmount")
    @UserSession
    public void userCannotTransferInvalidAmountTest(double transferAmount, BankAlert alert, int timesDeposit) {
        String transferAmountUi = Utils.formatMoney(transferAmount);

        AccountResponseModel firstAccount = SessionStorage.getSteps().createAccount();
        AccountResponseModel secondAccount = SessionStorage.getSteps().createAccount();

        SessionStorage.getSteps().depositExtension(firstAccount.getId(), DepositTestData.MAX_VALID_DEPOSIT_AMOUNT, timesDeposit);

        double firstAccountBalanceBeforeTransfer = SessionStorage.getSteps().getAccount(firstAccount.getId()).getBalance();
        double secondAccountBalanceBeforeTransfer = SessionStorage.getSteps().getAccount(secondAccount.getId()).getBalance();

        new UserDashboard()
                .open()
                .goToTransferPage()
                .selectAccount(firstAccount.getAccountNumber())
                .enterRecipientAccountNumber(secondAccount.getAccountNumber())
                .enterAmount(transferAmountUi)
                .confirmDetails()
                .clickSendTransferButton()
                .checkAlertMessageAndAccept(alert.getMessage())
                .refreshPage();

        double firstAccountBalance = SessionStorage.getSteps().getAccount(firstAccount.getId()).getBalance();
        double secondAccountBalance = SessionStorage.getSteps().getAccount(secondAccount.getId()).getBalance();
        softly.assertThat(firstAccountBalance).isEqualTo(firstAccountBalanceBeforeTransfer);
        softly.assertThat(secondAccountBalance).isEqualTo(secondAccountBalanceBeforeTransfer);

        TransferPage transferPage = new TransferPage();
        softly.assertThat(transferPage.getAccountBalance(firstAccount.getAccountNumber()).getText()
        ).contains(Utils.formatMoney(firstAccountBalance));
        softly.assertThat(transferPage.getAccountBalance(secondAccount.getAccountNumber()).getText()
        ).contains(Utils.formatMoney(secondAccountBalance));
    }

    @Test
    @UserSession
    public void userCannotTransferMoneyWithoutConfirmTest() {
        double transferAmount = RandomData.getValidDepositAmount();
        String transferAmountUi = Utils.formatMoney(transferAmount);

        AccountResponseModel firstAccount = SessionStorage.getSteps().createAccount();
        AccountResponseModel secondAccount = SessionStorage.getSteps().createAccount();

        SessionStorage.getSteps().depositExtension(firstAccount.getId(), DepositTestData.MAX_VALID_DEPOSIT_AMOUNT, 2);

        double firstAccountBalanceBeforeTransfer = SessionStorage.getSteps().getAccount(firstAccount.getId()).getBalance();
        double secondAccountBalanceBeforeTransfer = SessionStorage.getSteps().getAccount(secondAccount.getId()).getBalance();

        new UserDashboard()
                .open()
                .goToTransferPage()
                .selectAccount(firstAccount.getAccountNumber())
                .enterRecipientAccountNumber(secondAccount.getAccountNumber())
                .enterAmount(transferAmountUi)
                .clickSendTransferButton()
                .checkAlertMessageAndAccept(BankAlert.ALL_FIELDS_ERROR.getMessage())
                .refreshPage();

        double firstAccountBalance = SessionStorage.getSteps().getAccount(firstAccount.getId()).getBalance();
        double secondAccountBalance = SessionStorage.getSteps().getAccount(secondAccount.getId()).getBalance();
        softly.assertThat(firstAccountBalance).isEqualTo(firstAccountBalanceBeforeTransfer);
        softly.assertThat(secondAccountBalance).isEqualTo(secondAccountBalanceBeforeTransfer);

        TransferPage transferPage = new TransferPage();
        softly.assertThat(transferPage.getAccountBalance(firstAccount.getAccountNumber()).getText()
        ).contains(Utils.formatMoney(firstAccountBalance));
        softly.assertThat(transferPage.getAccountBalance(secondAccount.getAccountNumber()).getText()
        ).contains(Utils.formatMoney(secondAccountBalance));
    }

    @Disabled
    @Test
    @UserSession
    public void userCanTransferAgainTest() {
        double transferAmount = RandomData.getValidDepositAmount();
        String transferAmountUi = Utils.formatMoney(transferAmount);

        AccountResponseModel firstAccount = SessionStorage.getSteps().createAccount();
        AccountResponseModel secondAccount = SessionStorage.getSteps().createAccount();

        SessionStorage.getSteps().depositExtension(firstAccount.getId(), DepositTestData.MAX_VALID_DEPOSIT_AMOUNT, 2);

        double firstAccountBalanceBeforeTransfer = SessionStorage.getSteps().getAccount(firstAccount.getId()).getBalance();

        SessionStorage.getSteps().transfer(firstAccount.getId(), secondAccount.getId(), transferAmount);

        new TransferPage()
                .open()
                .goToTransferAgainBlock()
                .checkTransaction(TestData.TRANSACTION_TYPE_TRANSFER_OUT, transferAmountUi)
                .clickRepeatButton()
                .selectAccount(firstAccount.getAccountNumber())
                .confirmDetails()
                .clickSendTransferButton()
                .checkAlertMessageAndAccept(BankAlert.SUCCESSFULLY_REPEAT_TRANSFERRED.format(transferAmountUi, firstAccount.getId(), secondAccount.getId()));

        double firstAccountBalance = SessionStorage.getSteps().getAccount(firstAccount.getId()).getBalance();
        double secondAccountBalance = SessionStorage.getSteps().getAccount(secondAccount.getId()).getBalance();
        softly.assertThat(firstAccountBalance).isEqualTo(firstAccountBalanceBeforeTransfer - (transferAmount * 2));
        softly.assertThat(secondAccountBalance).isEqualTo(transferAmount * 2);
    }
}
