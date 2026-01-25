package api.iteration1;

import api.dao.AccountDao;
import api.dao.comparison.DaoAndModelAssertions;
import api.models.AccountResponseModel;
import api.models.CreateUserRequestModel;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.steps.DataBaseSteps;
import common.annotations.APIVersion;
import common.extensions.APIVersionExtension;
import org.junit.jupiter.api.Test;
import api.requests.skeleton.Endpoint;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import api.utilities.BaseTest;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(APIVersionExtension.class)
@APIVersion("with_database_with_fix")
public class CreateAccountTest extends BaseTest {

    @Test
    public void userCanCreateAccountTest() {
        CreateUserRequestModel userRequest = AdminSteps.createUser();
        AccountResponseModel accountResponse = new ValidatedCrudRequester<AccountResponseModel>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.CREATE_ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post(null);

        AccountDao accountDao = DataBaseSteps.getAccountByAccountNumber(accountResponse.getAccountNumber());
        DaoAndModelAssertions.assertThat(accountResponse, accountDao).match();
    }
}
