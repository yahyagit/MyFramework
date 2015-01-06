package tests;

import framework.Service;
import framework.asserts.SoftAssertListener;
import framework.retrylistener.RetryAnalyzer;
import framework.retrylistener.RetryListener;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;

import static framework.reporter.TestLog.initReporter;
import static utils.ActionUtils.getBaseUrl;


/**
 * Created by apaliy on 12/4/2014.
 */
@Listeners({RetryListener.class, SoftAssertListener.class})
public abstract class BaseTests {
    @BeforeSuite(alwaysRun = true)
    public void setUpSuite(ITestContext context) {
        initReporter();
    }

    @BeforeClass(alwaysRun = true)
    public void setUpClass() {
        Service.getInstance().initDriver();
        getBaseUrl();
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        Service.getInstance().terminateDriver();
    }

    @BeforeTest(alwaysRun = true)
    public void setUpTest(ITestContext context) {
        for (ITestNGMethod method : context.getAllTestMethods()) {
            if (method.getRetryAnalyzer() == null) {
                method.setRetryAnalyzer(new RetryAnalyzer());
            }
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownMethod(ITestResult result) {
        Reporter.setCurrentTestResult(result);
    }
}
