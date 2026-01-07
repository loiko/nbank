package ui.pages;

import lombok.Getter;

@Getter
public enum BankAlert {
    USER_CREATED_SUCCESSFULLY("✅ User created successfully"),
    USER_CREATION_ERROR_CHARACTERS("Username must be between 3 and 15 characters"),
    NEW_ACCOUNT_CREATED("✅ New Account Created! Account Number: "),
    NAME_UPDATED("✅ Name updated successfully!"),
    NAME_VALID_ERROR("❌ Please enter a valid name."),
    NAME_REQUIREMENTS_ERROR("Name must contain two words with letters only"),
    SUCCESSFULLY_DEPOSITED("✅ Successfully deposited $%s to account %s!"),
    SELECT_ACCOUNT_ERROR("❌ Please select an account."),
    VALID_DEPOSIT_ERROR("❌ Please enter a valid amount."),
    LESS_DEPOSIT_ERROR("❌ Please deposit less or equal to 5000$."),
    ALL_FIELDS_ERROR("❌ Please fill all fields and confirm."),
    SUCCESSFULLY_TRANSFERRED("✅ Successfully transferred $%s to account %s!"),
    SUCCESSFULLY_REPEAT_TRANSFERRED("✅ Transfer of $%s successful from Account %s to %s!"),
    INVALID_ACCOUNT_ERROR("❌ No user found with this account number."),
    MIN_AMOUNT_ERROR("❌ Error: Transfer amount must be at least 0.01"),
    MAX_AMOUNT_ERROR("❌ Error: Transfer amount cannot exceed 10000"),
    INVALID_TRANSFER_ERROR("❌ Error: Invalid transfer: insufficient funds or invalid accounts");


    private final String message;

    BankAlert(String message) {
        this.message = message;
    }

    public String format(Object... args) {
        return String.format(message, args);
    }
}
