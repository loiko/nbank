package ui.iteration2;

import api.dao.AccountDao;
import api.dao.comparison.DaoAndModelAssertions;
import api.generators.RandomData;
import api.models.AccountResponseModel;
import api.requests.steps.DataBaseSteps;
import api.requests.steps.UserSteps;
import api.testdata.AccountTestData;
import common.annotations.APIVersion;
import common.annotations.UserSession;
import common.extensions.APIVersionExtension;
import common.storage.SessionStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ui.utilities.BaseUiTest;
import ui.pages.BankAlert;
import ui.pages.UserDashboard;
import ui.utils.Utils;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(APIVersionExtension.class)
@APIVersion("with_database_with_fix")
public class DepositMoneyUserTest extends BaseUiTest {

    @Test
    @UserSession
    public void userCanDepositValidAmountTest() {
        double depositAmount = RandomData.getValidDepositAmount();
        String depositAmountUi = Utils.formatMoney(depositAmount);

        AccountResponseModel userAccount = SessionStorage.getSteps().createAccount();

        new UserDashboard()
                .open()
                .goToDepositPage()
                .selectAccount(userAccount.getAccountNumber())
                .enterAmount(depositAmountUi)
                .clickDepositButton()
                .checkAlertMessageAndAccept(BankAlert.SUCCESSFULLY_DEPOSITED.format(depositAmountUi, userAccount.getAccountNumber()));

        assertThat(SessionStorage
                .getSteps()
                .getAccount(userAccount.getId()).getBalance())
                .isEqualTo(depositAmount);


        AccountResponseModel accountAfterDeposit = UserSteps.getAccount(SessionStorage.getUser(), userAccount.getId());
        AccountDao accountDao = DataBaseSteps.getAccountByAccountNumber(userAccount.getAccountNumber());
        DaoAndModelAssertions.assertThat(accountAfterDeposit, accountDao).match();
    }

    @Test
    @UserSession
    public void userCannotDepositMoneyWithoutSelectingAccountTest() {
        double depositAmount = RandomData.getValidDepositAmount();
        String depositAmountUi = Utils.formatMoney(depositAmount);

        AccountResponseModel userAccount = SessionStorage.getSteps().createAccount();

        new UserDashboard()
                .open()
                .goToDepositPage()
                .enterAmount(depositAmountUi)
                .clickDepositButton()
                .checkAlertMessageAndAccept(BankAlert.SELECT_ACCOUNT_ERROR.getMessage());

        assertThat(SessionStorage
                .getSteps()
                .getAccount(userAccount.getId()).getBalance())
                .isZero();

        AccountResponseModel accountAfterDeposit = UserSteps.getAccount(SessionStorage.getUser(), userAccount.getId());
        AccountDao accountDao = DataBaseSteps.getAccountByAccountNumber(accountAfterDeposit.getAccountNumber());
        softly.assertThat(accountDao.getBalance()).isEqualTo(AccountTestData.INITIAL_ACCOUNT_BALANCE);
    }

    public static Stream<Arguments> invalidDepositAmount() {
        return Stream.of(
                Arguments.of(RandomData.getRandomNegativeAmount(), BankAlert.VALID_DEPOSIT_ERROR),
                Arguments.of(RandomData.getRandomInvalidDepositPositiveAmount(), BankAlert.LESS_DEPOSIT_ERROR)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidDepositAmount")
    @UserSession
    public void userCannotDepositInvalidAmountTest(double depositAmount, BankAlert alert) {
        String depositAmountUi = Utils.formatMoney(depositAmount);

        AccountResponseModel userAccount = SessionStorage.getSteps().createAccount();

        new UserDashboard()
                .open()
                .goToDepositPage()
                .selectAccount(userAccount.getAccountNumber())
                .enterAmount(depositAmountUi)
                .clickDepositButton()
                .checkAlertMessageAndAccept(alert.getMessage());

        assertThat(SessionStorage
                .getSteps()
                .getAccount(userAccount.getId()).getBalance())
                .isZero();

        AccountResponseModel accountAfterDeposit = UserSteps.getAccount(SessionStorage.getUser(), userAccount.getId());
        AccountDao accountDao = DataBaseSteps.getAccountByAccountNumber(accountAfterDeposit.getAccountNumber());
        softly.assertThat(accountDao.getBalance()).isEqualTo(AccountTestData.INITIAL_ACCOUNT_BALANCE);
    }
}

