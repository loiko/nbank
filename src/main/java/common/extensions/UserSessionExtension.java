package common.extensions;

import api.models.AccountResponseModel;
import api.models.CreateUserRequestModel;
import api.requests.steps.AdminSteps;
import api.requests.steps.UserSteps;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import ui.pages.BasePage;

import java.util.LinkedList;
import java.util.List;

public class UserSessionExtension implements BeforeEachCallback {
    private static final double MAX_VALID_DEPOSIT_AMOUNT = 5000.0;

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        UserSession annotation = extensionContext.getRequiredTestMethod().getAnnotation(UserSession.class);
        if (annotation != null) {
            int userCount = annotation.value();
            int[] accountsPerUser = annotation.accounts();
            int[] depositsPerUser = annotation.deposits();

            SessionStorage.clear();
            List<CreateUserRequestModel> users = new LinkedList<>();

            for (int i = 0; i < userCount; i++) {
                CreateUserRequestModel user = AdminSteps.createUser();
                users.add(user);

                int accountsCount = (accountsPerUser.length > i) ? accountsPerUser[i] : 0;
                int depositMultiplier = (depositsPerUser.length > i) ? depositsPerUser[i] : 0;

                for (int j = 0; j < accountsCount; j++) {
                    AccountResponseModel account = UserSteps.createAccount(user);

                    if (depositMultiplier > 0) {
                        UserSteps userSteps = new UserSteps(user.getUsername(), user.getPassword());
                        userSteps.depositExtension(account.getId(), MAX_VALID_DEPOSIT_AMOUNT, depositMultiplier);
                    }
                    SessionStorage.addAccount(user, account);
                }
            }

            SessionStorage.addUsers(users);

            if (isUiTest(extensionContext)) {
                int authAsUser = annotation.auth();
                BasePage.authAsUser(SessionStorage.getUser(authAsUser));
            }
        }
    }

    private boolean isUiTest(ExtensionContext extensionContext) {
        Class<?> testClass = extensionContext.getRequiredTestClass();
        return testClass.getPackage().getName().contains(".ui.");
    }
}