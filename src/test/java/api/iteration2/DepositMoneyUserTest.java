package api.iteration2;

import api.assertions.TransactionAssertions;
import api.dao.AccountDao;
import api.dao.TransactionDao;
import api.dao.comparison.DaoAndModelAssertions;
import api.generators.RandomData;
import api.models.*;
import api.models.comparison.ModelAssertions;
import api.requests.steps.DataBaseSteps;
import common.annotations.APIVersion;
import common.extensions.APIVersionExtension;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.steps.AdminSteps;
import api.requests.steps.UserSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import api.testdata.AccountTestData;
import api.testdata.DepositTestData;
import api.utilities.BaseTest;

import java.util.stream.Stream;

@ExtendWith(APIVersionExtension.class)
@APIVersion("with_database_with_fix")
public class DepositMoneyUserTest extends BaseTest {

    public static Stream<Double> validDepositAmounts() {
        return Stream.of(
                DepositTestData.MIN_VALID_DEPOSIT_AMOUNT,
                DepositTestData.MAX_VALID_DEPOSIT_AMOUNT,
                RandomData.getValidDepositAmount()
        );
    }

    @ParameterizedTest
    @MethodSource("validDepositAmounts")
    public void userCanDepositValidAmountToOwnAccountTest(double amount) {
        CreateUserRequestModel user = AdminSteps.createUser();
        AccountResponseModel account = UserSteps.createAccount(user);

        DepositRequestModel depositRequest = DepositRequestModel.builder()
                .id(account.getId())
                .balance(amount)
                .build();

        DepositResponseModel depositResponse = new ValidatedCrudRequester<DepositResponseModel>(
                RequestSpecs.authUserSpec(user),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOk())
                .post(depositRequest);

        softly.assertThat(depositResponse.getId()).isEqualTo(account.getId());
        softly.assertThat(depositResponse.getAccountNumber()).isEqualTo(account.getAccountNumber());
        softly.assertThat(depositResponse.getBalance()).isEqualTo(amount);

        TransactionsModel depositTransaction = depositResponse.getTransactions().get(0);
        TransactionAssertions.assertThat(depositTransaction)
                .hasGeneratedId()
                .hasAmount(amount)
                .hasType(TransactionType.DEPOSIT)
                .hasRelatedAccount(account.getId())
                .hasValidTimestamp();

        TransactionsResponseModel userTransactions = UserSteps.getTransactions(user, account.getId());
        TransactionsModel actualTransaction = userTransactions.getTransactions().get(0);
        ModelAssertions.assertThatModels(depositTransaction, actualTransaction).match();

        AccountResponseModel accountAfterDeposit = UserSteps.getAccount(user, account.getId());
        softly.assertThat((amount)).isEqualTo(accountAfterDeposit.getBalance());

        AccountDao accountDao = DataBaseSteps.getAccountByAccountNumber(accountAfterDeposit.getAccountNumber());
        DaoAndModelAssertions.assertThat(accountAfterDeposit, accountDao).match();

        TransactionDao transactionDao = DataBaseSteps.getTransactionById(actualTransaction.getId());
        DaoAndModelAssertions.assertThat(actualTransaction, transactionDao).match();
    }

    public static Stream<Arguments> invalidDepositAmounts() {
        return Stream.of(
                Arguments.of(DepositTestData.BELOW_MIN_AMOUNT, DepositTestData.ERROR_COMMON),
                Arguments.of(DepositTestData.ABOVE_MAX_AMOUNT, DepositTestData.ERROR_MAX_AMOUNT_LIMIT),
                Arguments.of(RandomData.getRandomNegativeAmount(), DepositTestData.ERROR_COMMON),
                Arguments.of(RandomData.getRandomInvalidDepositPositiveAmount(), DepositTestData.ERROR_MAX_AMOUNT_LIMIT)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidDepositAmounts")
    public void userCannotDepositInvalidAmountToOwnAccountTest(double amount, String errorMessage) {
        CreateUserRequestModel user = AdminSteps.createUser();
        AccountResponseModel account = UserSteps.createAccount(user);

        DepositRequestModel depositRequest = DepositRequestModel.builder()
                .id(account.getId())
                .balance(amount)
                .build();

        new CrudRequester(
                RequestSpecs.authUserSpec(user),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsBadRequest())
                .post(depositRequest)
                .body(Matchers.equalTo(errorMessage));

        AccountResponseModel accountAfterDeposit = UserSteps.getAccount(user, account.getId());
        softly.assertThat(accountAfterDeposit.getBalance()).isEqualTo(AccountTestData.INITIAL_ACCOUNT_BALANCE);

        TransactionsResponseModel userTransactions = UserSteps.getTransactions(user, account.getId());
        softly.assertThat(userTransactions.getTransactions().isEmpty());

        AccountDao accountDao = DataBaseSteps.getAccountByAccountNumber(accountAfterDeposit.getAccountNumber());
        softly.assertThat(accountDao.getBalance()).isEqualTo(AccountTestData.INITIAL_ACCOUNT_BALANCE);

        TransactionDao transactionDao = DataBaseSteps.getTransactionById(account.getId());
        softly.assertThat(transactionDao).isNull();
    }

    @Test
    public void userCannotDepositToAnotherUserAccountTest() {
        CreateUserRequestModel user = AdminSteps.createUser();
        AccountResponseModel account = UserSteps.createAccount(user);

        DepositRequestModel depositRequest = DepositRequestModel.builder()
                .id(account.getId() - 1)
                .balance(RandomData.getValidDepositAmount())
                .build();

        new CrudRequester(
                RequestSpecs.authUserSpec(user),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsForbidden())
                .post(depositRequest)
                .body(Matchers.equalTo(AccountTestData.UNAUTHORIZED_ACCOUNT_ACCESS_MESSAGE));

        AccountResponseModel accountAfterDeposit = UserSteps.getAccount(user, account.getId());
        softly.assertThat(accountAfterDeposit.getBalance()).isEqualTo(AccountTestData.INITIAL_ACCOUNT_BALANCE);

        TransactionsResponseModel userTransactions = UserSteps.getTransactions(user, account.getId());
        softly.assertThat(userTransactions.getTransactions().isEmpty());

        AccountDao accountDao = DataBaseSteps.getAccountByAccountNumber(accountAfterDeposit.getAccountNumber());
        softly.assertThat(accountDao.getBalance()).isEqualTo(AccountTestData.INITIAL_ACCOUNT_BALANCE);

        TransactionDao transactionDao = DataBaseSteps.getTransactionById(account.getId());
        softly.assertThat(transactionDao).isNull();
    }

    @Test
    public void userCannotDepositToNonExistentAccountTest() {
        CreateUserRequestModel user = AdminSteps.createUser();
        AccountResponseModel account = UserSteps.createAccount(user);

        DepositRequestModel depositRequest = DepositRequestModel.builder()
                .id(RandomData.getNonExistingAccountId())
                .balance(RandomData.getValidDepositAmount())
                .build();

        new CrudRequester(
                RequestSpecs.authUserSpec(user),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsForbidden())
                .post(depositRequest)
                .body(Matchers.equalTo(AccountTestData.UNAUTHORIZED_ACCOUNT_ACCESS_MESSAGE));

        AccountResponseModel accountAfterDeposit = UserSteps.getAccount(user, account.getId());
        softly.assertThat(accountAfterDeposit.getBalance()).isEqualTo(AccountTestData.INITIAL_ACCOUNT_BALANCE);

        TransactionsResponseModel userTransactions = UserSteps.getTransactions(user, account.getId());
        softly.assertThat(userTransactions.getTransactions().isEmpty());

        AccountDao accountDao = DataBaseSteps.getAccountByAccountNumber(accountAfterDeposit.getAccountNumber());
        softly.assertThat(accountDao.getBalance()).isEqualTo(AccountTestData.INITIAL_ACCOUNT_BALANCE);

        TransactionDao transactionDao = DataBaseSteps.getTransactionById(account.getId());
        softly.assertThat(transactionDao).isNull();
    }
}

