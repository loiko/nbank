package api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferResponseModel extends BaseModel {
    private long senderAccountId;
    private String message;
    private double amount;
    private long receiverAccountId;
}
