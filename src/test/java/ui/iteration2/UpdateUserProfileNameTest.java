package ui.iteration2;

import api.generators.RandomData;
import api.models.RetrieveUserProfileResponseModel;
import com.codeborne.selenide.Condition;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ui.utilities.BaseUiTest;
import ui.pages.BankAlert;
import ui.pages.UserDashboard;
import ui.testdata.TestData;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateUserProfileNameTest extends BaseUiTest {

    @Test
    @UserSession
    public void userCanUpdateNameWithValidDataTest() {
        String newFullName = RandomData.getValidUserName();

        new UserDashboard()
                .open()
                .userHeader()
                .click()
                .updateName(newFullName)
                .checkAlertMessageAndAccept(BankAlert.NAME_UPDATED.getMessage())
                .clickHomeButton()
                .refreshPage()
                .getWelcomeText().shouldHave(Condition.text(newFullName));

        assertThat(new UserDashboard().userHeader().getFullName()).isEqualTo(newFullName);

        RetrieveUserProfileResponseModel userProfile = SessionStorage.getSteps().getProfile();
        assertThat(userProfile.getName()).isEqualTo(newFullName);
    }

    public static Stream<Arguments> invalidUserName() {
        return Stream.of(
                Arguments.of("John", BankAlert.NAME_REQUIREMENTS_ERROR),
                Arguments.of("", BankAlert.NAME_VALID_ERROR)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidUserName")
    @UserSession()
    public void userCannotSetInvalidNameTest(String name, BankAlert alert) {
        new UserDashboard()
                .open()
                .userHeader()
                .click()
                .updateName(name)
                .checkAlertMessageAndAccept(alert.getMessage())
                .clickHomeButton()
                .refreshPage()
                .getWelcomeText().shouldHave(Condition.text(TestData.DEFAULT_WELCOME_TEXT));

        assertThat(new UserDashboard().userHeader().getFullName()).isEqualTo(TestData.DEFAULT_NAME);

        RetrieveUserProfileResponseModel userProfile = SessionStorage.getSteps().getProfile();
        assertThat(userProfile.getName()).isNull();
    }
}
