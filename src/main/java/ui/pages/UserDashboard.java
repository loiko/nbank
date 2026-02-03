package ui.pages;


import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import common.storage.SessionStorage;
import common.utils.RetryUtils;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class UserDashboard extends BasePage<UserDashboard> {
    private SelenideElement welcomeText = $(Selectors.byClassName("welcome-text"));
    private SelenideElement createNewAccountButton = $(Selectors.byText("➕ Create New Account"));
    private SelenideElement depositMoneyButton = $(Selectors.byText("\uD83D\uDCB0 Deposit Money"));
    private SelenideElement makeTransferButton = $(Selectors.byText("\uD83D\uDD04 Make a Transfer"));

    @Override
    public String url() {
        return "/dashboard";
    }

    public UserDashboard createNewAccount() {
        createNewAccountButton.click();
        RetryUtils.retry("Create new account in User Dashboard",
                () -> SessionStorage.getSteps().getAllAccounts(),
                accounts -> accounts.size() > 0,
                3,
                3000
        );
        return this;
    }

    public DepositPage goToDepositPage() {
        depositMoneyButton.click();
        return Selenide.page(DepositPage.class);
    }

    public TransferPage goToTransferPage() {
        makeTransferButton.click();
        return Selenide.page(TransferPage.class);
    }
}
