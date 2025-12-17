package requests.steps;

import models.*;
import requests.skeleton.Endpoint;
import requests.skeleton.requesters.CrudRequester;
import requests.skeleton.requesters.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.List;

public class UserSteps {
    public static RetrieveUserProfileResponseModel getProfile(CreateUserRequestModel user) {
        return new ValidatedCrudRequester<RetrieveUserProfileResponseModel>(
                RequestSpecs.authUserSpec(user),
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

    public static DepositResponseModel deposit(CreateUserRequestModel user, DepositRequestModel depositRequest) {
        return deposit(user, depositRequest, 1);
    }
}
