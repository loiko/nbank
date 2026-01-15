package api.database;

import lombok.Getter;

@Getter
public enum DatabaseTable {
    CUSTOMERS("customers"),
    ACCOUNTS("accounts"),
    TRANSACTIONS("transactions");

    private final String tableName;

    DatabaseTable(String tableName) {
        this.tableName = tableName;
    }
}
