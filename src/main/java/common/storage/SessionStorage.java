package common.storage;

import api.models.AccountResponseModel;
import api.models.CreateUserRequestModel;
import api.requests.steps.UserSteps;

import java.util.*;

public class SessionStorage {
    private static final ThreadLocal<SessionStorage> INSTANCE = ThreadLocal.withInitial(SessionStorage::new);
    private final LinkedHashMap<CreateUserRequestModel, UserSteps> userStepsMap = new LinkedHashMap<>();
    private final Map<CreateUserRequestModel, List<AccountResponseModel>> userAccounts = new HashMap<>();

    private SessionStorage() {
    }

    public static void addUsers(List<CreateUserRequestModel> users) {
        for (CreateUserRequestModel user : users) {
            INSTANCE.get().userStepsMap.put(user, new UserSteps(user.getUsername(), user.getPassword()));
        }
    }

    public static CreateUserRequestModel getUser(int number) {
        return new ArrayList<>(INSTANCE.get().userStepsMap.keySet()).get(number - 1);
    }

    public static CreateUserRequestModel getUser() {
        return getUser(1);
    }

    public static UserSteps getSteps(int number) {
        return new ArrayList<>(INSTANCE.get().userStepsMap.values()).get(number - 1);
    }

    public static UserSteps getSteps() {
        return getSteps(1);
    }

    public static void addAccount(CreateUserRequestModel user, AccountResponseModel account) {
        INSTANCE.get().userAccounts.computeIfAbsent(user, k -> new ArrayList<>()).add(account);
    }

    public static AccountResponseModel getUserAccount(int userIndex, int accountIndex) {
        CreateUserRequestModel user = getUser(userIndex);
        List<AccountResponseModel> accounts = INSTANCE.get().userAccounts.get(user);
        if (accounts == null || accounts.size() < accountIndex) {
            return null;
        }
        return accounts.get(accountIndex - 1);
    }

    public static AccountResponseModel getFirstUserAccount() {
        return getUserAccount(1, 1);
    }

    public static AccountResponseModel getSecondUserAccount() {
        return getUserAccount(2, 1);
    }

    public static List<AccountResponseModel> getUserAccounts(int userIndex) {
        CreateUserRequestModel user = getUser(userIndex);
        return INSTANCE.get().userAccounts.getOrDefault(user, new ArrayList<>());
    }

    public static void clear() {
        INSTANCE.get().userStepsMap.clear();
        INSTANCE.get().userAccounts.clear();
    }
}
