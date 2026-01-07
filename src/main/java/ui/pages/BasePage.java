package ui.pages;

import api.models.CreateUserRequestModel;
import api.specs.RequestSpecs;
import com.codeborne.selenide.*;
import org.openqa.selenium.Alert;
import ui.elements.BaseElement;
import ui.elements.UserHeader;

import java.util.List;
import java.util.function.Function;

import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class BasePage<T extends BasePage> {
    protected SelenideElement usernameInput = $(Selectors.byAttribute("placeholder", "Username"));
    protected SelenideElement passwordInput = $(Selectors.byAttribute("placeholder", "Password"));
    protected SelenideElement accountSelector = $(Selectors.byText("-- Choose an account --")).parent();
    protected SelenideElement amountInput = $(Selectors.byAttribute("placeholder", "Enter amount"));
    protected SelenideElement homeButton = $(Selectors.byText("\uD83C\uDFE0 Home"));

    public abstract String url();


    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    public T open() {
        return Selenide.open(url(), (Class<T>) this.getClass());
    }

    public T refreshPage() {
        refresh();
        return (T) this;
    }

    public <T extends BasePage> T getPage(Class<T> pageClass) {
        return Selenide.page(pageClass);
    }

    public T checkAlertMessageAndAccept(String bankAlert) {
        Alert alert = switchTo().alert();
        assertThat(alert.getText()).contains(bankAlert);
        alert.accept();
        return (T) this;
    }

    public static void authAsUser(String username, String password) {
        Selenide.open("/");
        String userAuthHeader = RequestSpecs.getUserAuthHeader(username, password);
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);
    }

    public static void authAsUser(CreateUserRequestModel createUserRequest) {
        authAsUser(createUserRequest.getUsername(), createUserRequest.getPassword());
    }

    // ElementCollection -> List<BaseElement>
    protected <T extends BaseElement> List<T> generatePageElements(ElementsCollection elementsCollection, Function<SelenideElement, T> constructor) {
        return elementsCollection.stream().map(constructor).toList();
    }

    public UserHeader userHeader() {
        return new UserHeader($(".user-info").parent());
    }

    public UserDashboard clickHomeButton() {
        homeButton.click();
        return Selenide.page(UserDashboard.class);
    }

    public T selectAccount(String accountNumber) {
        accountSelector
                .shouldBe(Condition.visible, Condition.enabled)
                .selectOptionContainingText(accountNumber);
        return self();
    }

    public T enterAmount(String amount) {
        amountInput.sendKeys(amount);
        return self();
    }

    public SelenideElement getBalance(String accountNumber) {
        return accountSelector
                .findAll("option")
                .findBy(Condition.text(accountNumber));
    }
}

