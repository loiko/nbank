package common.setup;

import api.requests.steps.AdminSteps;

public class CleanData {
    public static void main(String[] args) {
        AdminSteps.deleteAllUsers();
    }
}
