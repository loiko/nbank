package ui.iteration1;

import api.dao.UserDao;
import api.dao.comparison.DaoAndModelAssertions;
import api.requests.steps.AdminSteps;
import api.generators.RandomModelGenerator;
import api.models.CreateUserRequestModel;
import api.models.CreateUserResponseModel;
import api.models.comparison.ModelAssertions;
import api.requests.steps.DataBaseSteps;
import common.annotations.AdminSession;
import org.junit.jupiter.api.Test;
import ui.elements.UserBage;
import ui.utilities.BaseUiTest;
import ui.pages.AdminPanel;
import ui.pages.BankAlert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class CreateUserTest extends BaseUiTest {
    @Test
    @AdminSession
    public void adminCanCreateUserTest() {
        CreateUserRequestModel newUser = RandomModelGenerator.generate(CreateUserRequestModel.class);

        UserBage newUserBage = new AdminPanel()
                .open()
                .createUser(newUser)
                .checkAlertMessageAndAccept(BankAlert.USER_CREATED_SUCCESSFULLY.getMessage())
                .findUserByUsername(newUser.getUsername());

        assertThat(newUserBage).as("UserBage should exist on Dashboard after user creation").isNotNull();

        CreateUserResponseModel createdUser = AdminSteps.getAllUsers().stream()
                .filter(user -> user.getUsername().equals(newUser.getUsername()))
                .findFirst().get();

        ModelAssertions.assertThatModels(newUser, createdUser).match();

        UserDao userDao = DataBaseSteps.getUserByUsername(newUser.getUsername());
        DaoAndModelAssertions.assertThat(createdUser, userDao).match();
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
        assertNull(DataBaseSteps.getUserByUsername(newUser.getUsername()));
    }
}
