package api.requests.steps;

import api.dao.TransactionDao;
import api.database.Condition;
import api.database.DBRequest;
import api.dao.UserDao;
import api.dao.AccountDao;
import api.configs.Config;
import api.database.DatabaseTable;
import common.helpers.StepLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataBaseSteps {

    public static UserDao getUserByUsername(String username) {
        return StepLogger.log("Get user from database by username: " + username, () -> {
            return DBRequest.builder()
                    .requestType(DBRequest.RequestType.SELECT)
                    .table(DatabaseTable.CUSTOMERS.getTableName())
                    .where(Condition.equalTo("username", username))
                    .extractAs(UserDao.class);
        });
    }

    public static UserDao getUserById(Long id) {
        return StepLogger.log("Get user from database by ID: " + id, () -> {
            return DBRequest.builder()
                    .requestType(DBRequest.RequestType.SELECT)
                    .table(DatabaseTable.CUSTOMERS.getTableName())
                    .where(Condition.equalTo("id", id))
                    .extractAs(UserDao.class);
        });
    }

    public static UserDao getUserByRole(String role) {
        return StepLogger.log("Get user from database by role: " + role, () -> {
            return DBRequest.builder()
                    .requestType(DBRequest.RequestType.SELECT)
                    .table(DatabaseTable.CUSTOMERS.getTableName())
                    .where(Condition.equalTo("role", role))
                    .extractAs(UserDao.class);
        });
    }

    public static AccountDao getAccountByAccountNumber(String accountNumber) {
        return StepLogger.log("Get account from database by account number: " + accountNumber, () -> {
            return DBRequest.builder()
                    .requestType(DBRequest.RequestType.SELECT)
                    .table(DatabaseTable.ACCOUNTS.getTableName())
                    .where(Condition.equalTo("account_number", accountNumber))
                    .extractAs(AccountDao.class);
        });
    }

    public static AccountDao getAccountById(Long id) {
        return StepLogger.log("Get account from database by ID: " + id, () -> {
            return DBRequest.builder()
                    .requestType(DBRequest.RequestType.SELECT)
                    .table(DatabaseTable.ACCOUNTS.getTableName())
                    .where(Condition.equalTo("id", id))
                    .extractAs(AccountDao.class);
        });
    }

    public static AccountDao getAccountByCustomerId(Long customerId) {
        return StepLogger.log("Get account from database by customer ID: " + customerId, () -> {
            return DBRequest.builder()
                    .requestType(DBRequest.RequestType.SELECT)
                    .table(DatabaseTable.CUSTOMERS.getTableName())
                    .where(Condition.equalTo("customer_id", customerId))
                    .extractAs(AccountDao.class);
        });
    }

    public static TransactionDao getTransactionById(long transactionId) {
        return StepLogger.log("Get transaction from database by transaction ID: " + transactionId, () -> {
            return DBRequest.builder()
                    .requestType(DBRequest.RequestType.SELECT)
                    .table(DatabaseTable.TRANSACTIONS.getTableName())
                    .where(Condition.equalTo("id", transactionId))
                    .extractAs(TransactionDao.class);
        });
    }

    public static TransactionDao getTransactionByAccountId(long accountId) {
        return StepLogger.log("Get transaction from database by account ID: " + accountId, () -> {
            return DBRequest.builder()
                    .requestType(DBRequest.RequestType.SELECT)
                    .table(DatabaseTable.TRANSACTIONS.getTableName())
                    .where(Condition.equalTo("account_id", accountId))
                    .extractAs(TransactionDao.class);
        });
    }

    public static TransactionDao getTransactionByAccountIdAndType(long accountId, String type) {
        return StepLogger.log("Get transaction by account ID and type: " + type, () -> {
            return DBRequest.builder()
                    .requestType(DBRequest.RequestType.SELECT)
                    .table(DatabaseTable.TRANSACTIONS.getTableName())
                    .where(Condition.equalTo("account_id", accountId))
                    .where(Condition.equalTo("type", type))
                    .extractAs(TransactionDao.class);
        });
    }

    public static void updateAccountBalance(Long accountId, Double newBalance) {
        StepLogger.log("Update account balance in database for account ID: " + accountId + " to: " + newBalance, () -> {
            try (Connection connection = DriverManager.getConnection(
                    Config.getProperty("db.url"),
                    Config.getProperty("db.username"),
                    Config.getProperty("db.password"))) {

                String sql = "UPDATE accounts SET balance = ? WHERE id = ?";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setDouble(1, newBalance);
                    statement.setLong(2, accountId);
                    int rowsAffected = statement.executeUpdate();

                    if (rowsAffected == 0) {
                        throw new RuntimeException("No account found with ID: " + accountId);
                    }

                    return rowsAffected;
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to update account balance", e);
            }
        });
    }
}
