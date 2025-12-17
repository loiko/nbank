package models;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Builder
public class TransactionsResponseModel extends BaseModel {
    private List<TransactionsModel> transactions;

    @JsonCreator
    public TransactionsResponseModel(List<TransactionsModel> list) {
        this.transactions = list;
    }
}
