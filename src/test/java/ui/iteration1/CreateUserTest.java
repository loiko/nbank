package ui.iteration1;

import api.requests.steps.AdminSteps;
import api.generators.RandomModelGenerator;
import api.models.CreateUserRequestModel;
import api.models.CreateUserResponseModel;
import api.models.comparison.ModelAssertions;
import common.annotations.AdminSession;
import org.junit.jupiter.api.Test;
import ui.utilities.BaseUiTest;
import ui.pages.AdminPanel;
import ui.pages.BankAlert;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class CreateUserTest extends BaseUiTest {
    @Test
    @AdminSession
    public void adminCanCreateUserTest() {
        CreateUserRequestModel newUser = RandomModelGenerator.generate(CreateUserRequestModel.class);
        assertTrue(new AdminPanel()
                .open()
                .createUser(newUser)
                .checkAlertMessageAndAccept(BankAlert.USER_CREATED_SUCCESSFULLY.getMessage())
                .getAllUsers()
                .stream().anyMatch(userBage -> userBage.getUsername().equals(newUser.getUsername())));

        CreateUserResponseModel createdUser = AdminSteps.getAllUsers().stream()
                .filter(user -> user.getUsername().equals(newUser.getUsername()))
                .findFirst().get();

        ModelAssertions.assertThatModels(newUser, createdUser).match();
    }

    @Test
    @AdminSession
    public void adminCannotCreateUserWithInvalidDataTest() {
        CreateUserRequestModel newUser = RandomModelGenerator.generate(CreateUserRequestModel.class);
        newUser.setUsername("f");

        assertTrue(new AdminPanel()
                .open()
                .createUser(newUser)
                .checkAlertMessageAndAccept(BankAlert.USER_CREATION_ERROR_CHARACTERS.getMessage())
                .getAllUsers().stream().noneMatch(userBage -> userBage.getUsername().equals(newUser.getUsername())));

        long notCreatedUser = AdminSteps.getAllUsers().stream()
                .filter(user -> user.getUsername().equals(newUser.getUsername()))
                .count();

        assertThat(notCreatedUser).isZero();
    }
}
