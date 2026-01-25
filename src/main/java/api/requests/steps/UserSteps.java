package api.requests.steps;

import api.models.*;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.helpers.StepLogger;

import java.util.List;

public class UserSteps {
    private String username;
    private String password;

    public UserSteps(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static RetrieveUserProfileResponseModel getProfile(CreateUserRequestModel user) {
        return new ValidatedCrudRequester<RetrieveUserProfileResponseModel>(
                RequestSpecs.authUserSpec(user),
                Endpoint.PROFILE,
                ResponseSpecs.requestReturnsOk())
                .get();
    }

    public RetrieveUserProfileResponseModel getProfile() {
        return new ValidatedCrudRequester<RetrieveUserProfileResponseModel>(
                RequestSpecs.authAsUser(username, password),
                Endpoint.PROFILE,
                ResponseSpecs.requestReturnsOk())
                .get();
    }

    public static UpdateUserNameResponseModel updateUserName(CreateUserRequestModel user, UpdateUserNameRequestModel name) {
        return new ValidatedCrudRequester<UpdateUserNameResponseModel>(
                RequestSpecs.authUserSpec(user),
                Endpoint.NAME,
                ResponseSpecs.requestReturnsOk())
                .put(name);
    }

    public AccountResponseModel createAccount() {
        return new ValidatedCrudRequester<AccountResponseModel>(
                RequestSpecs.authAsUser(username, password),
                Endpoint.CREATE_ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post(null);
    }

    public static AccountResponseModel createAccount(CreateUserRequestModel user) {
        return new ValidatedCrudRequester<AccountResponseModel>(
                RequestSpecs.authUserSpec(user),
                Endpoint.CREATE_ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post(null);
    }

    public static AccountResponseModel getAccount(CreateUserRequestModel user, long accountId) {
        List<AccountResponseModel> accounts =
                new CrudRequester(
                        RequestSpecs.authUserSpec(user),
                        Endpoint.GET_ACCOUNTS,
                        ResponseSpecs.requestReturnsOk()
                )
                        .get()
                        .extract()
                        .jsonPath()
                        .getList("", AccountResponseModel.class);

        return accounts.stream()
                .filter(acc -> acc.getId() == accountId)
                .findFirst()
                .orElseThrow(() ->
                        new AssertionError("Account with id " + accountId + " not found"));
    }

    public AccountResponseModel getAccount(long accountId) {
        List<AccountResponseModel> accounts =
                new CrudRequester(
                        RequestSpecs.authAsUser(username, password),
                        Endpoint.GET_ACCOUNTS,
                        ResponseSpecs.requestReturnsOk()
                )
                        .get()
                        .extract()
                        .jsonPath()
                        .getList("", AccountResponseModel.class);

        return accounts.stream()
                .filter(acc -> acc.getId() == accountId)
                .findFirst()
                .orElseThrow(() ->
                        new AssertionError("Account with id " + accountId + " not found"));
    }

    public static TransactionsResponseModel getTransactions(CreateUserRequestModel user, long accountId) {
        return new ValidatedCrudRequester<TransactionsResponseModel>(
                RequestSpecs.authUserSpec(user),
                Endpoint.TRANSACTIONS,
                ResponseSpecs.requestReturnsOk())
                .get(accountId);
    }

    public static DepositResponseModel deposit(CreateUserRequestModel user, DepositRequestModel depositRequest, int times) {
        DepositResponseModel lastResponse = null;
        for (int i = 0; i < times; i++) {
            lastResponse = new ValidatedCrudRequester<DepositResponseModel>(
                    RequestSpecs.authUserSpec(user),
                    Endpoint.DEPOSIT,
                    ResponseSpecs.requestReturnsOk())
                    .post(depositRequest);
        }
        return lastResponse;
    }

    public void depositExtension(long accountId, double amount, int times) {
        for (int i = 0; i < times; i++) {
        DepositRequestModel request = DepositRequestModel.builder()
                .id(accountId)
                .balance(amount)
                .build();
            new ValidatedCrudRequester<DepositResponseModel>(
                    RequestSpecs.authAsUser(username, password),
                    Endpoint.DEPOSIT,
                    ResponseSpecs.requestReturnsOk()
            ).post(request);
        }
    }

    public static DepositResponseModel deposit(CreateUserRequestModel user, DepositRequestModel depositRequest) {
        return deposit(user, depositRequest, 1);
    }

    public TransferResponseModel transfer(
            long fromAccountId,
            long toAccountId,
            double amount
    ) {
        TransferRequestModel request = TransferRequestModel.builder()
                .senderAccountId(fromAccountId)
                .receiverAccountId(toAccountId)
                .amount(amount)
                .build();

        return new ValidatedCrudRequester<TransferResponseModel>(
                RequestSpecs.authAsUser(username, password),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsOk()
        ).post(request);
    }

    public List<AccountResponseModel> getAllAccounts() {
        return new ValidatedCrudRequester<AccountResponseModel>(
                RequestSpecs.authAsUser(username, password),
                Endpoint.GET_ACCOUNTS,
                ResponseSpecs.requestReturnsOk()).getAll(AccountResponseModel[].class);
    }

    public TransferResponseWithFraudCheckModel transferWithFraudCheck(Long senderAccountId, Long receiverAccountId, double amount) {
        return StepLogger.log("User " + username + " transfers " + amount + " to " + receiverAccountId + " with fraud check", () -> {
            TransferRequestWithFraudCheckModel transferRequest = TransferRequestWithFraudCheckModel.builder()
                    .senderAccountId(senderAccountId)
                    .receiverAccountId(receiverAccountId)
                    .amount(amount)
                    .description("Test transfer with fraud check")
                    .build();

            return new ValidatedCrudRequester<TransferResponseWithFraudCheckModel>(
                    RequestSpecs.authAsUser(username, password),
                    Endpoint.TRANSFER_WITH_FRAUD_CHECK,
                    ResponseSpecs.requestReturnsOk()).post(transferRequest);
        });
    }
}
