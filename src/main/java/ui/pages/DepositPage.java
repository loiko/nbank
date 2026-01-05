package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class DepositPage extends BasePage<DepositPage> {
    @Override
    public String url() {
        return "/deposit";
    }

    private SelenideElement depositButton = $(Selectors.byText("\uD83D\uDCB5 Deposit"));

    public DepositPage clickDepositButton() {
        depositButton.click();
        return this;
    }
}
