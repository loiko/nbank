package api.models;

import api.generators.GeneratingRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserNameRequestModel extends BaseModel {
    @GeneratingRule(regex = "^[A-Za-z]{1,20} [A-Za-z]{1,20}$")
    private String name;
}
