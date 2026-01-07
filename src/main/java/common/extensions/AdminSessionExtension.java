package common.extensions;

import api.models.CreateUserRequestModel;
import common.annotations.AdminSession;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import ui.pages.BasePage;

public class AdminSessionExtension implements BeforeEachCallback {
    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        AdminSession annotation = extensionContext.getRequiredTestMethod().getAnnotation(AdminSession.class);
        if (annotation != null) {
            BasePage.authAsUser(CreateUserRequestModel.getAdmin());
        }
    }
}
