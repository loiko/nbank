package ui.iteration1;

import com.codeborne.selenide.Condition;
import api.models.CreateUserRequestModel;
import common.annotations.APIVersion;
import common.extensions.APIVersionExtension;
import org.junit.jupiter.api.Test;
import api.requests.steps.AdminSteps;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.utilities.BaseUiTest;
import ui.pages.AdminPanel;
import ui.pages.LoginPage;
import ui.pages.UserDashboard;

@ExtendWith(APIVersionExtension.class)
@APIVersion("with_database_with_fix")
public class LoginUserTest extends BaseUiTest {

    @Test
    public void adminCanLoginWithCorrectDataTest() {
        CreateUserRequestModel admin = CreateUserRequestModel.getAdmin();

        new LoginPage()
                .open()
                .login(admin.getUsername(), admin.getPassword())
                .getPage(AdminPanel.class).getAdminPanelTest().shouldBe(Condition.visible);
    }

    @Test
    public void userCanLoginWithCorrectDataTest() {
        CreateUserRequestModel user = AdminSteps.createUser();

        new LoginPage()
                .open()
                .login(user.getUsername(), user.getPassword())
                .getPage(UserDashboard.class).getWelcomeText().shouldHave(Condition.text("Welcome, noname!"));
    }
}

