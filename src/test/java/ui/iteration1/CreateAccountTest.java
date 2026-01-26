package ui.iteration1;

import api.dao.AccountDao;
import api.dao.comparison.DaoAndModelAssertions;
import api.models.AccountResponseModel;
import api.requests.steps.DataBaseSteps;
import common.annotations.APIVersion;
import common.annotations.UserSession;
import common.extensions.APIVersionExtension;
import common.storage.SessionStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.utilities.BaseUiTest;
import ui.pages.BankAlert;
import ui.pages.UserDashboard;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(APIVersionExtension.class)
@APIVersion("with_database_with_fix")
public class CreateAccountTest extends BaseUiTest {

    @Test
    @UserSession
    public void userCanCreateAccountTest() {
        new UserDashboard().open().createNewAccount();

        List<AccountResponseModel> createdAccounts = SessionStorage.getSteps().getAllAccounts();
        assertThat(createdAccounts).hasSize(1);

        new UserDashboard().checkAlertMessageAndAccept(BankAlert.NEW_ACCOUNT_CREATED.getMessage() + createdAccounts.get(0).getAccountNumber());

        assertThat(createdAccounts.get(0).getBalance()).isZero();

        AccountDao accountDao = DataBaseSteps.getAccountByAccountNumber(createdAccounts.get(0).getAccountNumber());
        DaoAndModelAssertions.assertThat(createdAccounts.get(0), accountDao).match();
    }
}
