package iteration2;

import generators.RandomModelGenerator;
import models.*;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.skeleton.Endpoint;
import requests.skeleton.requesters.CrudRequester;
import requests.skeleton.requesters.ValidatedCrudRequester;
import requests.steps.AdminSteps;
import requests.steps.UserSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;
import utilities.BaseTest;

import java.util.stream.Stream;

public class UpdateUserProfileNameTest extends BaseTest {

    @Test
    public void userCanSetValidNameTest() {
        CreateUserRequestModel user = AdminSteps.createUser();
        RetrieveUserProfileResponseModel userProfileBeforeSetName = UserSteps.getProfile(user);
        softly.assertThat(userProfileBeforeSetName.getName()).isNull();

        UpdateUserNameRequestModel setUserNameRequest =
                RandomModelGenerator.generate(UpdateUserNameRequestModel.class);
        UpdateUserNameResponseModel setUserNameResponse = new ValidatedCrudRequester<UpdateUserNameResponseModel>(
                RequestSpecs.authUserSpec(user),
                Endpoint.NAME,
                ResponseSpecs.requestReturnsOk())
                .put(setUserNameRequest);
        RetrieveUserProfileResponseModel userProfileAfterSetName = UserSteps.getProfile(user);

        softly.assertThat(setUserNameRequest.getName())
                .isEqualTo(setUserNameResponse.getCustomer().getName())
                .isEqualTo(userProfileAfterSetName.getName());
    }

    @Test
    public void userCanUpdateValidNameTest() {
        CreateUserRequestModel user = AdminSteps.createUser();
        UpdateUserNameRequestModel setUserNameRequest =
                RandomModelGenerator.generate(UpdateUserNameRequestModel.class);
        UserSteps.updateUserName(user, setUserNameRequest);
        RetrieveUserProfileResponseModel userProfileAfterSetName = UserSteps.getProfile(user);

        UpdateUserNameRequestModel updateUserNameRequest =
                RandomModelGenerator.generate(UpdateUserNameRequestModel.class);
        UpdateUserNameResponseModel updateUserNameResponse = new ValidatedCrudRequester<UpdateUserNameResponseModel>(
                RequestSpecs.authUserSpec(user),
                Endpoint.NAME,
                ResponseSpecs.requestReturnsOk())
                .put(updateUserNameRequest);
        RetrieveUserProfileResponseModel userProfileAfterUpdateName = UserSteps.getProfile(user);

        softly.assertThat(updateUserNameResponse.getCustomer().getName()).isEqualTo(userProfileAfterUpdateName.getName());
        softly.assertThat(userProfileAfterUpdateName.getName()).isNotEqualTo(userProfileAfterSetName.getName());
    }

    public static Stream<Arguments> invalidUserName() {
        String errorMessage = "Name must contain two words with letters only";
        return Stream.of(
                Arguments.of("John", errorMessage),
                Arguments.of("John Smith Junior", errorMessage),
                Arguments.of("", errorMessage),
                Arguments.of("    ", errorMessage),
                Arguments.of("John  Smith", errorMessage),
                Arguments.of(" John Smith", errorMessage),
                Arguments.of("John Smith ", errorMessage),
                Arguments.of("John! Smith", errorMessage),
                Arguments.of("John Sm1th", errorMessage),
                Arguments.of("John 123", errorMessage),
                Arguments.of("John@Smith", errorMessage),
                Arguments.of("John, Smith", errorMessage),
                Arguments.of("@John Smith", errorMessage),
                Arguments.of("John  ", errorMessage)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidUserName")
    public void userCannotSetInvalidNameTest(String name, String errorMessage) {
        CreateUserRequestModel user = AdminSteps.createUser();

        UpdateUserNameRequestModel setUserNameRequest = UpdateUserNameRequestModel.builder()
                .name(name)
                .build();

        new CrudRequester(
                RequestSpecs.authUserSpec(user),
                Endpoint.NAME,
                ResponseSpecs.requestReturnsBadRequest())
                .put(setUserNameRequest)
                .body(Matchers.equalTo(errorMessage));

        RetrieveUserProfileResponseModel userProfileAfterUpdateName = UserSteps.getProfile(user);
        softly.assertThat(userProfileAfterUpdateName.getName()).isNull();
    }
}
