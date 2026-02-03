package api.requests.steps;

import api.generators.RandomModelGenerator;
import api.models.CreateUserRequestModel;
import api.models.CreateUserResponseModel;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.helpers.StepLogger;

import java.util.Arrays;
import java.util.List;

public class AdminSteps {
    public static CreateUserRequestModel createUser() {
        CreateUserRequestModel userRequest = RandomModelGenerator.generate(CreateUserRequestModel.class);

        return StepLogger.log("Admin creates user " + userRequest.getUsername(), () -> {

            new ValidatedCrudRequester<CreateUserResponseModel>(
                    RequestSpecs.adminSpec(),
                    Endpoint.ADMIN_USER,
                    ResponseSpecs.entityWasCreated())
                    .post(userRequest);

            return userRequest;
        });
    }

    public static List<CreateUserResponseModel> getAllUsers() {
        return StepLogger.log("Admin gets all users", () -> {
            return new ValidatedCrudRequester<CreateUserResponseModel>(
                    RequestSpecs.adminSpec(),
                    Endpoint.ADMIN_USER,
                    ResponseSpecs.requestReturnsOk()).getAll(CreateUserResponseModel[].class);
        });
    }

    public static void deleteUser(long id) {
        StepLogger.log("Admin deletes user with id " + id, () -> {
            new CrudRequester(
                    RequestSpecs.adminSpec(),
                    Endpoint.DELETE_USER,
                    ResponseSpecs.requestReturnsOk()
            ).delete(id);
        });
    }

    public static void deleteAllUsers() {
        StepLogger.log("Admin deletes all users", () -> {
            List<CreateUserResponseModel> users = getAllUsers();
            List<String> protectedUsernames = Arrays.asList("admin", "john_doe", "jane_smith", "bob_wilson");

            for (CreateUserResponseModel user : users) {
                if (!protectedUsernames.contains(user.getUsername())) {
                    deleteUser(user.getId());
                }
            }
        });
    }
}
