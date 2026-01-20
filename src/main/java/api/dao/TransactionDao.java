package api.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDao {
    private long id;
    private double amount;
    private String type;
    private String timestamp;
    private long accountId;
    private long relatedAccountId;
    private String creationTime;
}

