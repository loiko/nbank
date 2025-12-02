package iteration1;

import generators.RandomData;
import models.CreateUserRequest;
import models.CreateUserResponse;
import models.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.AdminCreateUserRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.stream.Stream;

public class CreateUserTest extends BaseTest {

    @Test
    public void adminCanCreateUserWithValidDataTest() {
        CreateUserRequest userRequest = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        CreateUserResponse createUserResponse = new AdminCreateUserRequester(
                RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated())
                .post(userRequest).extract().as(CreateUserResponse.class);

        softly.assertThat(userRequest.getUsername()).isEqualTo(createUserResponse.getUsername());
        softly.assertThat(userRequest.getPassword()).isNotEqualTo(createUserResponse.getPassword());
        softly.assertThat(userRequest.getRole()).isEqualTo(createUserResponse.getRole());

    }

    public static Stream<Arguments> userInvalidData() {
        return Stream.of(
                Arguments.of(" ", "Password33$", "USER", "username", "Username must be between 3 and 15 characters"),
                Arguments.of("dfgdfg", "h", "USER", "password", "Password must contain at least one digit, one lower case, one upper case, one special character, no spaces, and be at least 8 characters long"));
    }

    @MethodSource("userInvalidData")
    @ParameterizedTest
    public void adminCannotCreateUserWithInvalidDataTest(String username, String password, String role, String errorKey, String errorValue) {
        CreateUserRequest userRequest = CreateUserRequest.builder()
                .username(username)
                .password(password)
                .role(role)
                .build();

        new AdminCreateUserRequester(
                RequestSpecs.adminSpec(),
                ResponseSpecs.requestReturnsBadRequest(errorKey, errorValue))
                .post(userRequest);
    }
}
