package testdata;

public final class DepositTestData {
    private DepositTestData() {
    }

    public static final double MIN_VALID_DEPOSIT_AMOUNT = 0.01;
    public static final double MAX_VALID_DEPOSIT_AMOUNT = 5000.0;
    public static final double BELOW_MIN_AMOUNT = 0.0;
    public static final double ABOVE_MAX_AMOUNT = MAX_VALID_DEPOSIT_AMOUNT + 0.01;
    public static final String ERROR_MIN_AMOUNT = "Deposit amount must be at least 0.01";
    public static final String ERROR_MAX_AMOUNT = "Deposit amount cannot exceed 5000";
}
