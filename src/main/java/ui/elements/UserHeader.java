package ui.elements;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import ui.pages.EditProfilePage;

import static com.codeborne.selenide.Condition.visible;

@Getter
public class UserHeader extends BaseElement {
    private final String fullName;
    private final String username;

    public UserHeader(SelenideElement element) {
        super(element);
        this.fullName = find(".user-name").text();
        this.username = find(".user-username").text();
    }

    public EditProfilePage click() {
        element.shouldBe(visible).click();
        return Selenide.page(EditProfilePage.class);
    }
}
