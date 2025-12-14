package assertions;

import models.TransactionType;
import models.TransactionsModel;
import org.assertj.core.api.AbstractAssert;

public class TransactionAssertions extends AbstractAssert<TransactionAssertions, TransactionsModel> {

    private static final String TIMESTAMP_REGEX =
            "^[A-Za-z]{3} [A-Za-z]{3} \\d{2} \\d{2}:\\d{2}:\\d{2} UTC \\d{4}$";

    private TransactionAssertions(TransactionsModel actual) {
        super(actual, TransactionAssertions.class);
    }

    public static TransactionAssertions assertThat(TransactionsModel actual) {
        return new TransactionAssertions(actual);
    }

    public TransactionAssertions hasAmount(double amount) {
        isNotNull();

        if (Double.compare(actual.getAmount(), amount) != 0) {
            failWithMessage(
                    "Expected amount <%s> but was <%s>",
                    amount, actual.getAmount()
            );
        }
        return this;
    }

    public TransactionAssertions hasType(TransactionType type) {
        if (!type.toString().equals(actual.getType())) {
            failWithMessage(
                    "Expected type <%s> but was <%s>",
                    type, actual.getType()
            );
        }
        return this;
    }

    public TransactionAssertions hasRelatedAccount(long accountId) {
        if (actual.getRelatedAccountId() != accountId) {
            failWithMessage(
                    "Expected relatedAccountId <%s> but was <%s>",
                    accountId, actual.getRelatedAccountId()
            );
        }
        return this;
    }

    public TransactionAssertions hasValidTimestamp() {
        if (!actual.getTimestamp().matches(TIMESTAMP_REGEX)) {
            failWithMessage(
                    "Timestamp <%s> does not match expected format",
                    actual.getTimestamp()
            );
        }
        return this;
    }

    public TransactionAssertions hasGeneratedId() {
        if (actual.getId() <= 0) {
            failWithMessage("Expected generated id but was <%s>", actual.getId());
        }
        return this;
    }
}
