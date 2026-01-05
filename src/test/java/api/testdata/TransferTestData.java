package api.testdata;

public final class TransferTestData {
    private TransferTestData() {
    }

    public static final double MONEY_DELTA = 0.0001;
    public static final double MIN_VALID_TRANSFER_AMOUNT = 0.01;
    public static final double MAX_VALID_TRANSFER_AMOUNT = 10000.0;
    public static final double BELOW_MIN_AMOUNT = 0.0;
    public static final double ABOVE_MAX_AMOUNT = MAX_VALID_TRANSFER_AMOUNT + 0.01;
    public static final String ERROR_MIN_AMOUNT = "Transfer amount must be at least 0.01";
    public static final String ERROR_MAX_AMOUNT = "Transfer amount cannot exceed 10000";
    public static final String ERROR_INVALID_TRANSFER = "Invalid transfer: insufficient funds or invalid accounts";

}
