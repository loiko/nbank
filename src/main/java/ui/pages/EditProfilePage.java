package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import common.utils.RetryUtils;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.executeJavaScript;

public class EditProfilePage extends BasePage<EditProfilePage> {
    protected SelenideElement saveChangesButton = $(Selectors.byText("💾 Save Changes"));

    private SelenideElement newNameInput() {
        return $(Selectors.byAttribute("placeholder", "Enter new name"));
    }

    @Override
    public String url() {
        return "/edit-profile";
    }


    public EditProfilePage updateName(String newName) {
        RetryUtils.retry(
                () -> {
                    SelenideElement input = newNameInput();
                    input.shouldBe(Condition.visible);
                    input.shouldBe(Condition.enabled);
                    input.click();
                    input.clear();
                    input.sendKeys(newName);
                    return input.getValue();
                },
                value -> newName.equals(value),
                3,
                1000
        );

        saveChangesButton.click();
        return this;
    }
}