package api.iteration1;

import api.generators.RandomModelGenerator;
import api.models.CreateUserRequestModel;
import api.models.CreateUserResponseModel;
import api.models.comparison.ModelAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import api.utilities.BaseTest;

import java.util.List;
import java.util.stream.Stream;

public class CreateUserTest extends BaseTest {

    @Test
    public void adminCanCreateUserWithValidDataTest() {
        CreateUserRequestModel userRequest = RandomModelGenerator.generate(CreateUserRequestModel.class);

        CreateUserResponseModel createUserResponse = new ValidatedCrudRequester<CreateUserResponseModel>
                (RequestSpecs.adminSpec(),
                        Endpoint.ADMIN_USER,
                        ResponseSpecs.entityWasCreated())
                .post(userRequest);

        ModelAssertions.assertThatModels(userRequest, createUserResponse).match();
    }

    public static Stream<Arguments> userInvalidData() {
        return Stream.of(
                Arguments.of(" ", "Password33$", "USER", "username", List.of("Username cannot be blank", "Username must contain only letters, digits, dashes, underscores, and dots", "Username must be between 3 and 15 characters")),
                Arguments.of("dfgdfg", "h", "USER", "password", List.of("Password must contain at least one digit, one lower case, one upper case, one special character, no spaces, and be at least 8 characters long"))
        );
    }

    @MethodSource("userInvalidData")
    @ParameterizedTest
    public void adminCannotCreateUserWithInvalidDataTest(String username, String password, String role, String errorKey, List<String> errorValues) {
        CreateUserRequestModel userRequest = CreateUserRequestModel.builder()
                .username(username)
                .password(password)
                .role(role)
                .build();

        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.requestReturnsBadRequest(errorKey, errorValues))
                .post(userRequest);
    }
}
