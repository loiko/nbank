package ui.utilities;

import api.configs.Config;
import api.utilities.BaseTest;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import common.extensions.AdminSessionExtension;
import common.extensions.BrowserMatchExtension;
import common.extensions.UserSessionExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

@ExtendWith(AdminSessionExtension.class)
@ExtendWith(UserSessionExtension.class)
@ExtendWith(BrowserMatchExtension.class)
public class BaseUiTest extends BaseTest {

    @BeforeAll
    public static void setupSelenoid() {
        System.out.println("=== Environment Check ===");
        System.out.println("UIBASEURL env: " + System.getenv("UIBASEURL"));
        System.out.println("UIREMOTE env: " + System.getenv("UIREMOTE"));
        System.out.println("uiBaseUrl from Config: " + Config.getProperty("uiBaseUrl"));
        System.out.println("uiRemote from Config: " + Config.getProperty("uiRemote"));

        Configuration.remote = Config.getProperty("uiRemote");
        Configuration.baseUrl = Config.getProperty("uiBaseUrl");
        Configuration.browser = Config.getProperty("browser");
        Configuration.browserSize = Config.getProperty("browserSize");
        Configuration.headless = true;

        System.out.println("=== Final Configuration ===");
        System.out.println("Configuration.remote: " + Configuration.remote);
        System.out.println("Configuration.baseUrl: " + Configuration.baseUrl);

        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true, "enableLog", true)
        );
    }

    @AfterEach
    void tearDown() {
        WebDriverRunner.closeWebDriver();
    }
}
