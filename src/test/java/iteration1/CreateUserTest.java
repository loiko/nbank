package iteration1;

import generators.RandomModelGenerator;
import models.CreateUserRequestModel;
import models.CreateUserResponseModel;
import models.comparison.ModelAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.skeleton.Endpoint;
import requests.skeleton.requesters.CrudRequester;
import requests.skeleton.requesters.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;
import utilities.BaseTest;

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
                Arguments.of(" ", "Password33$", "USER", "username", "Username must be between 3 and 15 characters"),
                Arguments.of("dfgdfg", "h", "USER", "password", "Password must contain at least one digit, one lower case, one upper case, one special character, no spaces, and be at least 8 characters long"));
    }

    @MethodSource("userInvalidData")
    @ParameterizedTest
    public void adminCannotCreateUserWithInvalidDataTest(String username, String password, String role, String errorKey, String errorValue) {
        CreateUserRequestModel userRequest = CreateUserRequestModel.builder()
                .username(username)
                .password(password)
                .role(role)
                .build();

        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.requestReturnsBadRequest(errorKey, errorValue))
                .post(userRequest);
    }
}
