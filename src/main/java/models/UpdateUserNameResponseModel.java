package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserNameResponseModel extends BaseModel {
    private RetrieveUserProfileResponseModel customer;
    private String message;
}
