package iteration2;

import assertions.TransactionAssertions;
import generators.RandomData;
import models.*;
import models.comparison.ModelAssertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.skeleton.Endpoint;
import requests.skeleton.requesters.CrudRequester;
import requests.skeleton.requesters.ValidatedCrudRequester;
import requests.steps.AdminSteps;
import requests.steps.UserSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;
import testdata.AccountTestData;
import testdata.DepositTestData;
import utilities.BaseTest;

import java.util.stream.Stream;

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
    }

    public static Stream<Arguments> invalidDepositAmounts() {
        return Stream.of(
                Arguments.of(DepositTestData.BELOW_MIN_AMOUNT, DepositTestData.ERROR_MIN_AMOUNT),
                Arguments.of(DepositTestData.ABOVE_MAX_AMOUNT, DepositTestData.ERROR_MAX_AMOUNT),
                Arguments.of(RandomData.getRandomNegativeAmount(), DepositTestData.ERROR_MIN_AMOUNT),
                Arguments.of(RandomData.getRandomInvalidDepositPositiveAmount(), DepositTestData.ERROR_MAX_AMOUNT)
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
    }
}

