package api.iteration2;

import api.assertions.TransactionAssertions;
import api.dao.AccountDao;
import api.dao.TransactionDao;
import api.dao.comparison.DaoAndModelAssertions;
import api.generators.RandomData;
import api.models.*;
import api.requests.steps.DataBaseSteps;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
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
import api.testdata.TransferTestData;
import api.utilities.BaseTest;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.within;

public class TransferMoneyUserTest extends BaseTest {

    public static Stream<Double> validTransferAmounts() {
        return Stream.of(
                TransferTestData.MIN_VALID_TRANSFER_AMOUNT,
                TransferTestData.MAX_VALID_TRANSFER_AMOUNT,
                RandomData.getValidTransferAmount()
        );
    }

    @ParameterizedTest
    @MethodSource("validTransferAmounts")
    public void userCanTransferValidAmountToOtherUserAccountTest(double amount) {
        CreateUserRequestModel firstUser = AdminSteps.createUser();
        CreateUserRequestModel secondUser = AdminSteps.createUser();

        AccountResponseModel firstUserAccount = UserSteps.createAccount(firstUser);
        AccountResponseModel secondUserAccount = UserSteps.createAccount(secondUser);

        DepositRequestModel depositRequest = DepositRequestModel.builder()
                .id(firstUserAccount.getId())
                .balance(DepositTestData.MAX_VALID_DEPOSIT_AMOUNT)
                .build();

        UserSteps.deposit(firstUser, depositRequest, 2);
        AccountResponseModel firstUserAccountAfterDeposit = UserSteps.getAccount(firstUser, firstUserAccount.getId());

        TransferRequestModel transferRequest = TransferRequestModel.builder()
                .senderAccountId(firstUserAccount.getId())
                .receiverAccountId(secondUserAccount.getId())
                .amount(amount)
                .build();
        TransferResponseModel transferResponse = new ValidatedCrudRequester<TransferResponseModel>(
                RequestSpecs.authUserSpec(firstUser),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsOk())
                .post(transferRequest);

        softly.assertThat(transferResponse.getSenderAccountId()).isEqualTo(firstUserAccount.getId());
        softly.assertThat(transferResponse.getMessage()).isEqualTo(TransferMessages.SUCCESSFUL.getMessage());
        softly.assertThat(transferResponse.getAmount()).isEqualTo(amount);
        softly.assertThat(transferResponse.getReceiverAccountId()).isEqualTo(secondUserAccount.getId());

        TransactionsResponseModel secondUserTransactions = UserSteps.getTransactions(secondUser, secondUserAccount.getId());
        TransactionsModel transaction = secondUserTransactions.getTransactions().get(0);
        TransactionAssertions.assertThat(transaction)
                .hasGeneratedId()
                .hasAmount(amount)
                .hasType(TransactionType.TRANSFER_IN)
                .hasRelatedAccount(firstUserAccount.getId())
                .hasValidTimestamp();

        AccountResponseModel firstUserAccountAfterTransfer = UserSteps.getAccount(firstUser, firstUserAccount.getId());
        AccountResponseModel secondUserAccountAfterTransfer = UserSteps.getAccount(secondUser, secondUserAccount.getId());
        softly.assertThat(firstUserAccountAfterTransfer.getBalance()).isCloseTo((firstUserAccountAfterDeposit.getBalance() - amount), within(TransferTestData.MONEY_DELTA));
        softly.assertThat(secondUserAccountAfterTransfer.getBalance()).isCloseTo(amount, within(TransferTestData.MONEY_DELTA));

        AccountDao firstUserAccountDao = DataBaseSteps.getAccountByAccountNumber(firstUserAccountAfterTransfer.getAccountNumber());
        AccountDao secondUserAccountDao = DataBaseSteps.getAccountByAccountNumber(secondUserAccountAfterTransfer.getAccountNumber());
        DaoAndModelAssertions.assertThat(firstUserAccountAfterTransfer, firstUserAccountDao).match();
        DaoAndModelAssertions.assertThat(secondUserAccountAfterTransfer, secondUserAccountDao).match();

        TransactionDao transactionDao = DataBaseSteps.getTransactionById(transaction.getId());
        DaoAndModelAssertions.assertThat(transaction, transactionDao).match();
    }

    @ParameterizedTest
    @MethodSource("validTransferAmounts")
    public void userCanTransferValidAmountToOwnAccountTest(double amount) {
        CreateUserRequestModel user = AdminSteps.createUser();
        AccountResponseModel userFirstAccount = UserSteps.createAccount(user);
        AccountResponseModel userSecondAccount = UserSteps.createAccount(user);

        DepositRequestModel depositRequest = DepositRequestModel.builder()
                .id(userFirstAccount.getId())
                .balance(DepositTestData.MAX_VALID_DEPOSIT_AMOUNT)
                .build();

        UserSteps.deposit(user, depositRequest, 2);
        AccountResponseModel firstAccountAfterDeposit = UserSteps.getAccount(user, userFirstAccount.getId());

        TransferRequestModel transferRequest = TransferRequestModel.builder()
                .senderAccountId(userFirstAccount.getId())
                .receiverAccountId(userSecondAccount.getId())
                .amount(amount)
                .build();
        TransferResponseModel transferResponse = new ValidatedCrudRequester<TransferResponseModel>(
                RequestSpecs.authUserSpec(user),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsOk())
                .post(transferRequest);

        softly.assertThat(transferResponse.getSenderAccountId()).isEqualTo(userFirstAccount.getId());
        softly.assertThat(transferResponse.getMessage()).isEqualTo(TransferMessages.SUCCESSFUL.getMessage());
        softly.assertThat(transferResponse.getAmount()).isEqualTo(amount);
        softly.assertThat(transferResponse.getReceiverAccountId()).isEqualTo(userSecondAccount.getId());

        TransactionsResponseModel secondAccountTransactions = UserSteps.getTransactions(user, userSecondAccount.getId());
        TransactionsModel transaction = secondAccountTransactions.getTransactions().get(0);
        TransactionAssertions.assertThat(transaction)
                .hasGeneratedId()
                .hasAmount(amount)
                .hasType(TransactionType.TRANSFER_IN)
                .hasRelatedAccount(userFirstAccount.getId())
                .hasValidTimestamp();

        AccountResponseModel firstAccountAfterTransfer = UserSteps.getAccount(user, userFirstAccount.getId());
        AccountResponseModel secondAccountAfterTransfer = UserSteps.getAccount(user, userSecondAccount.getId());
        softly.assertThat(firstAccountAfterTransfer.getBalance()).isCloseTo((firstAccountAfterDeposit.getBalance() - amount), within(TransferTestData.MONEY_DELTA));
        softly.assertThat(secondAccountAfterTransfer.getBalance()).isCloseTo(amount, within(TransferTestData.MONEY_DELTA));

        AccountDao firstUserAccountDao = DataBaseSteps.getAccountByAccountNumber(firstAccountAfterTransfer.getAccountNumber());
        AccountDao secondUserAccountDao = DataBaseSteps.getAccountByAccountNumber(secondAccountAfterTransfer.getAccountNumber());
        DaoAndModelAssertions.assertThat(firstAccountAfterTransfer, firstUserAccountDao).match();
        DaoAndModelAssertions.assertThat(secondAccountAfterTransfer, secondUserAccountDao).match();

        TransactionDao transactionDao = DataBaseSteps.getTransactionById(transaction.getId());
        DaoAndModelAssertions.assertThat(transaction, transactionDao).match();
    }

    public static Stream<Arguments> invalidTransferAmounts() {
        return Stream.of(
                Arguments.of(TransferTestData.BELOW_MIN_AMOUNT, TransferTestData.ERROR_INVALID_TRANSFER),
                Arguments.of(TransferTestData.ABOVE_MAX_AMOUNT, TransferTestData.ERROR_MAX_AMOUNT),
                Arguments.of(RandomData.getRandomNegativeAmount(), TransferTestData.ERROR_INVALID_TRANSFER),
                Arguments.of(RandomData.getRandomInvalidTransferPositiveAmount(), TransferTestData.ERROR_MAX_AMOUNT)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidTransferAmounts")
    public void userCannotTransferInValidAmountTest(double amount, String errorMessage) {
        CreateUserRequestModel firstUser = AdminSteps.createUser();
        CreateUserRequestModel secondUser = AdminSteps.createUser();

        AccountResponseModel firstUserAccount = UserSteps.createAccount(firstUser);
        AccountResponseModel secondUserAccount = UserSteps.createAccount(secondUser);

        DepositRequestModel depositRequest = DepositRequestModel.builder()
                .id(firstUserAccount.getId())
                .balance(DepositTestData.MAX_VALID_DEPOSIT_AMOUNT)
                .build();

        UserSteps.deposit(firstUser, depositRequest, 3);
        AccountResponseModel firstUserAccountAfterDeposit = UserSteps.getAccount(firstUser, firstUserAccount.getId());

        TransferRequestModel transferRequest = TransferRequestModel.builder()
                .senderAccountId(firstUserAccount.getId())
                .receiverAccountId(secondUserAccount.getId())
                .amount(amount)
                .build();

        new CrudRequester(
                RequestSpecs.authUserSpec(firstUser),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsBadRequest())
                .post(transferRequest)
                .body(Matchers.equalTo(errorMessage));

        TransactionsResponseModel transactionsResponse = UserSteps.getTransactions(secondUser, secondUserAccount.getId());
        softly.assertThat(transactionsResponse.getTransactions().isEmpty());

        AccountResponseModel firstUserAccountAfterTransfer = UserSteps.getAccount(firstUser, firstUserAccount.getId());
        AccountResponseModel secondUserAccountAfterTransfer = UserSteps.getAccount(secondUser, secondUserAccount.getId());
        softly.assertThat(firstUserAccountAfterTransfer.getBalance()).isEqualTo(firstUserAccountAfterDeposit.getBalance());
        softly.assertThat(secondUserAccountAfterTransfer.getBalance()).isEqualTo(AccountTestData.INITIAL_ACCOUNT_BALANCE);

        AccountDao firtsUserAccountDao = DataBaseSteps.getAccountByAccountNumber(firstUserAccountAfterTransfer.getAccountNumber());
        AccountDao secondUserAccountDao = DataBaseSteps.getAccountByAccountNumber(secondUserAccountAfterTransfer.getAccountNumber());
        softly.assertThat(firtsUserAccountDao.getBalance()).isEqualTo(firstUserAccountAfterDeposit.getBalance());
        softly.assertThat(secondUserAccountDao.getBalance()).isEqualTo(AccountTestData.INITIAL_ACCOUNT_BALANCE);

        TransactionDao transferTransaction = DataBaseSteps.getTransactionByAccountIdAndType(
                secondUserAccount.getId(),
                "TRANSFER"
        );
        softly.assertThat(transferTransaction).isNull();
    }

    @Test
    public void userCannotTransferMoreThanBalanceTest() {
        CreateUserRequestModel firstUser = AdminSteps.createUser();
        CreateUserRequestModel secondUser = AdminSteps.createUser();

        AccountResponseModel firstUserAccount = UserSteps.createAccount(firstUser);
        AccountResponseModel secondUserAccount = UserSteps.createAccount(secondUser);

        DepositRequestModel depositRequest = DepositRequestModel.builder()
                .id(firstUserAccount.getId())
                .balance(RandomData.getValidDepositAmount())
                .build();

        UserSteps.deposit(firstUser, depositRequest);
        AccountResponseModel firstUserAccountAfterDeposit = UserSteps.getAccount(firstUser, firstUserAccount.getId());

        TransferRequestModel transferRequest = TransferRequestModel.builder()
                .senderAccountId(firstUserAccount.getId())
                .receiverAccountId(secondUserAccount.getId())
                .amount(depositRequest.getBalance() + RandomData.getValidDepositAmount())
                .build();

        new CrudRequester(
                RequestSpecs.authUserSpec(firstUser),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsBadRequest())
                .post(transferRequest)
                .body(Matchers.equalTo(TransferTestData.ERROR_INVALID_TRANSFER));

        TransactionsResponseModel transactionsResponse = UserSteps.getTransactions(secondUser, secondUserAccount.getId());
        softly.assertThat(transactionsResponse.getTransactions().isEmpty());

        AccountResponseModel firstUserAccountAfterTransfer = UserSteps.getAccount(firstUser, firstUserAccount.getId());
        AccountResponseModel secondUserAccountAfterTransfer = UserSteps.getAccount(secondUser, secondUserAccount.getId());
        softly.assertThat(firstUserAccountAfterTransfer.getBalance()).isEqualTo(firstUserAccountAfterDeposit.getBalance());
        softly.assertThat(secondUserAccountAfterTransfer.getBalance()).isEqualTo(AccountTestData.INITIAL_ACCOUNT_BALANCE);

        AccountDao firtsUserAccountDao = DataBaseSteps.getAccountByAccountNumber(firstUserAccountAfterTransfer.getAccountNumber());
        AccountDao secondUserAccountDao = DataBaseSteps.getAccountByAccountNumber(secondUserAccountAfterTransfer.getAccountNumber());
        softly.assertThat(firtsUserAccountDao.getBalance()).isEqualTo(firstUserAccountAfterDeposit.getBalance());
        softly.assertThat(secondUserAccountDao.getBalance()).isEqualTo(AccountTestData.INITIAL_ACCOUNT_BALANCE);

        TransactionDao transferTransaction = DataBaseSteps.getTransactionByAccountIdAndType(
                secondUserAccount.getId(),
                "TRANSFER"
        );
        softly.assertThat(transferTransaction).isNull();
    }

    @Test
    public void userCannotTransferToNonExistentAccountTest() {
        CreateUserRequestModel user = AdminSteps.createUser();
        AccountResponseModel userAccount = UserSteps.createAccount(user);

        DepositRequestModel depositRequest = DepositRequestModel.builder()
                .id(userAccount.getId())
                .balance(RandomData.getValidDepositAmount())
                .build();

        UserSteps.deposit(user, depositRequest);
        AccountResponseModel firstUserAccountAfterDeposit = UserSteps.getAccount(user, userAccount.getId());

        TransferRequestModel transferRequest = TransferRequestModel.builder()
                .senderAccountId(userAccount.getId())
                .receiverAccountId(RandomData.getNonExistingAccountId())
                .amount(depositRequest.getBalance())
                .build();

        new CrudRequester(
                RequestSpecs.authUserSpec(user),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsBadRequest())
                .post(transferRequest)
                .body(Matchers.equalTo(TransferTestData.ERROR_INVALID_TRANSFER));

        AccountResponseModel userAccountAfterTransfer = UserSteps.getAccount(user, userAccount.getId());
        softly.assertThat(userAccountAfterTransfer.getBalance()).isEqualTo(firstUserAccountAfterDeposit.getBalance());

        AccountDao userAccountDao = DataBaseSteps.getAccountByAccountNumber(userAccountAfterTransfer.getAccountNumber());
        softly.assertThat(userAccountDao.getBalance()).isEqualTo(firstUserAccountAfterDeposit.getBalance());

        TransactionDao transferTransaction = DataBaseSteps.getTransactionByAccountIdAndType(
                userAccount.getId(),
                "TRANSFER"
        );
        softly.assertThat(transferTransaction).isNull();
    }

    @Disabled
    @Test
    public void userCannotTransferSameAccountTest() {
        CreateUserRequestModel user = AdminSteps.createUser();
        AccountResponseModel userAccount = UserSteps.createAccount(user);

        DepositRequestModel depositRequest = DepositRequestModel.builder()
                .id(userAccount.getId())
                .balance(RandomData.getValidDepositAmount())
                .build();

        UserSteps.deposit(user, depositRequest);

        TransferRequestModel transferRequest = TransferRequestModel.builder()
                .senderAccountId(userAccount.getId())
                .receiverAccountId(userAccount.getId())
                .amount(depositRequest.getBalance())
                .build();

        new CrudRequester(
                RequestSpecs.authUserSpec(user),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsBadRequest())
                .post(transferRequest)
                .body(Matchers.equalTo(TransferTestData.ERROR_INVALID_TRANSFER));
    }
}
