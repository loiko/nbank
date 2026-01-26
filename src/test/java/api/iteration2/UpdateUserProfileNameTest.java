package api.iteration2;

import api.dao.UserDao;
import api.generators.RandomModelGenerator;
import api.models.CreateUserRequestModel;
import api.models.RetrieveUserProfileResponseModel;
import api.models.UpdateUserNameRequestModel;
import api.models.UpdateUserNameResponseModel;
import api.requests.steps.DataBaseSteps;
import common.annotations.APIVersion;
import common.extensions.APIVersionExtension;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.steps.AdminSteps;
import api.requests.steps.UserSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import api.utilities.BaseTest;

import java.util.stream.Stream;

@ExtendWith(APIVersionExtension.class)
@APIVersion("with_database_with_fix")
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

        UserDao userDao = DataBaseSteps.getUserByUsername(user.getUsername());
        softly.assertThat(setUserNameRequest.getName()).isEqualTo(userDao.getName());
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
        UserDao userDao = DataBaseSteps.getUserByUsername(user.getUsername());

        softly.assertThat(updateUserNameResponse.getCustomer().getName()).isEqualTo(userProfileAfterUpdateName.getName());
        softly.assertThat(userProfileAfterUpdateName.getName()).isNotEqualTo(userProfileAfterSetName.getName());
        softly.assertThat(userProfileAfterUpdateName.getName()).isEqualTo(userDao.getName());
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

        UserDao userDao = DataBaseSteps.getUserByUsername(user.getUsername());
        softly.assertThat(userDao.getName()).isNull();
    }
}
