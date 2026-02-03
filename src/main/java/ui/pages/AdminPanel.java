package ui.pages;

import api.models.CreateUserRequestModel;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import common.helpers.StepLogger;
import common.utils.RetryUtils;
import lombok.Getter;
import ui.elements.UserBage;

import java.util.List;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Selenide.$;

@Getter
public class AdminPanel extends BasePage<AdminPanel> {
    private SelenideElement adminPanelTest = $(Selectors.byText("Admin Panel"));
    private SelenideElement addUserButton = $(Selectors.byText("Add User"));

    @Override
    public String url() {
        return "/admin";
    }

    public AdminPanel createUser(String username, String password) {
       return StepLogger.log("Create user with username: " + username, () -> {
            usernameInput.sendKeys(username);
            passwordInput.sendKeys(password);
            addUserButton.click();
            return this;
        });
    }

    public AdminPanel createUser(CreateUserRequestModel newUser) {
        return StepLogger.log("Create user with username: " + newUser.getUsername(), () -> {
            usernameInput.sendKeys(newUser.getUsername());
            passwordInput.sendKeys(newUser.getPassword());
            addUserButton.click();
            return this;
        });
    }

    public List<UserBage> getAllUsers() {
        return StepLogger.log("Get all users from Dashboard", () -> {
            ElementsCollection elementsCollection = $(Selectors.byText("All Users")).parent().findAll("li").shouldHave(sizeGreaterThan(0));
            return generatePageElements(elementsCollection, UserBage::new);
        });
    }

    public UserBage findUserByUsername(String username) {
        return RetryUtils.retry("Find user by username " + username + " in Admin Panel",
                () -> getAllUsers().stream().filter(it -> it.getUsername().equals(username)).findAny().orElse(null),
                result -> result != null,
                3,
                1000
        );
    }
}
