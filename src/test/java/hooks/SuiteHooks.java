package hooks;

import io.github.sentinel.configurations.Configuration;
import io.github.sentinel.system.SentinelScreenRecorder;
import io.github.sentinel.webdrivers.Driver;
import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SuiteHooks {

    private static final Logger log = LogManager.getLogger(SuiteHooks.class);

    @BeforeAll
    public static void setUpSuite() throws Exception {
        if (Configuration.toBoolean("recordTests")) {
            SentinelScreenRecorder.startRecording();
        }
    }

    @AfterAll
    public static void tearDownSuite() throws Exception {
        String totalWaitTime = Configuration.toString("totalWaitTime");
        if (totalWaitTime != null) {
            log.warn("This test took {} total seconds longer due to explicit waits. "
                    + "Sentinel handles dynamic waits automatically.", totalWaitTime);
        }
        if (Configuration.toBoolean("recordTests")) {
            SentinelScreenRecorder.stopRecording();
        }
        if (!Configuration.toBoolean("leaveBrowserOpen")) {
            Driver.quitAllDrivers();
        }
    }
}
