package ui.elements;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import ui.pages.TransferPage;

public class TransactionItem extends BaseElement {
    public TransactionItem(SelenideElement element) {
        super(element);
    }

    public String getType() {
        String text = element.$("span").getText();
        return text.split(" - ")[0].trim();
    }

    public String getAmount() {
        String text = element.$("span").getText();
        return text
                .substring(text.indexOf("$") + 1)
                .split("\\n")[0]
                .trim();
    }

    public String getName() {
        return element.$("small strong").getText();
    }

    public TransferPage clickRepeatButton() {
        element.$("button").click();
        return Selenide.page(TransferPage.class);
    }
}
