package api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferRequestWithFraudCheckModel extends BaseModel {
    private Long senderAccountId;
    private Long receiverAccountId;
    private double amount;
    private String description;
}
