package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

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
        newNameInput().shouldBe(Condition.visible, Condition.enabled);
        newNameInput().click();
        newNameInput().clear();
        newNameInput().sendKeys(newName);
        saveChangesButton.click();
        return this;
    }
}