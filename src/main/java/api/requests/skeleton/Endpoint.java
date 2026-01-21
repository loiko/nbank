package api.requests.skeleton;

import api.models.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Endpoint {
    ADMIN_USER(
            "/admin/users",
            CreateUserRequestModel.class,
            CreateUserResponseModel.class
    ),

    LOGIN(
            "/auth/login",
            LoginUserRequestModel.class,
            LoginUserResponseModel.class
    ),

    CREATE_ACCOUNTS(
            "/accounts",
            BaseModel.class,
            AccountResponseModel.class
    ),

    GET_ACCOUNTS(
            "/customer/accounts",
            BaseModel.class,
            AccountResponseModel.class
    ),

    DEPOSIT(
            "/accounts/deposit",
            DepositRequestModel.class,
            DepositResponseModel.class
    ),

    TRANSFER(
            "/accounts/transfer",
            TransferRequestModel.class,
            TransferResponseModel.class
    ),

    PROFILE(
            "customer/profile",
            BaseModel.class,
            RetrieveUserProfileResponseModel.class
    ),

    NAME(
            "customer/profile",
            UpdateUserNameRequestModel.class,
            UpdateUserNameResponseModel.class
    ),

    TRANSACTIONS(
            "/accounts/{accountId}/transactions",
            BaseModel.class,
            TransactionsResponseModel.class
    ),

    DELETE_USER(
            "/admin/users/{id}",
            BaseModel.class,
            DeleteUserResponseModel.class
    ),

    TRANSFER_WITH_FRAUD_CHECK(
            "/accounts/transfer-with-fraud-check",
            TransferRequestWithFraudCheckModel.class,
            TransferResponseWithFraudCheckModel.class
    );

    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;
}
