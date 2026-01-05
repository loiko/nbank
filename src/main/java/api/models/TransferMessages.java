package api.models;

public enum TransferMessages {
    SUCCESSFUL("Transfer successful");

    private final String message;

    TransferMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

