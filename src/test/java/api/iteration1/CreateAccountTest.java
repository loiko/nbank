package api.iteration1;

import api.models.CreateUserRequestModel;
import org.junit.jupiter.api.Test;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import api.utilities.BaseTest;

public class CreateAccountTest extends BaseTest {

    @Test
    public void userCanCreateAccountTest() {
        CreateUserRequestModel userRequest = AdminSteps.createUser();
        new CrudRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.CREATE_ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post(null);
    }
}
