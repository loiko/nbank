package ui.pages;

import api.models.AccountResponseModel;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import ui.elements.TransactionItem;

import java.util.List;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Selenide.$;

public class TransferPage extends BasePage<TransferPage> {
    private SelenideElement recipientAccountNumberInput = $(Selectors.byAttribute("placeholder", "Enter recipient account number"));
    private SelenideElement confirmDetailsCheckbox = $(Selectors.byId("confirmCheck"));
    private SelenideElement sendTransferButton = $(Selectors.byText("\uD83D\uDE80 Send Transfer"));
    private SelenideElement transferAgainButton = $(Selectors.byText("\uD83D\uDD01 Transfer Again"));

    @Override
    public String url() {
        return "/transfer";
    }

    public TransferPage enterRecipientAccountNumber(String accountNumber) {
        recipientAccountNumberInput.sendKeys(accountNumber);
        return this;
    }

    public TransferPage confirmDetails() {
        confirmDetailsCheckbox.setSelected(true);
        return this;
    }

    public TransferPage clickSendTransferButton() {
        sendTransferButton.click();
        return this;
    }

    public SelenideElement getAccountBalance(String accountNumber) {
        return accountSelector
                .findAll("option")
                .findBy(Condition.text(accountNumber));
    }

    public TransferPage goToTransferAgainBlock() {
        transferAgainButton.click();
        return this;
    }

    public List<TransactionItem> getAllTransactions() {
        ElementsCollection items = $("ul.list-group")
                .findAll("li.list-group-item")
                .shouldHave(sizeGreaterThan(0));
        return generatePageElements(items, TransactionItem::new);
    }

    public TransactionItem checkTransaction(String type, String amount) {
        return getAllTransactions().stream()
                .filter(tx ->
                        tx.getType().equals(type) &&
                                tx.getAmount().equals(amount)
                )
                .findFirst()
                .orElseThrow(() ->
                        new AssertionError("Transaction not found: " + type + " " + amount)
                );
    }
}
