package framework.retrylistener;

import framework.data.PropertiesReader;
import framework.reporter.TestLog;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import static framework.retrylistener.RetryUtils.getResultHash;

public class RetryAnalyzer implements IRetryAnalyzer {
    private static final int MAX_RETRY_COUNT = PropertiesReader.getInstance().getRetryCount();

    private int resultHash;
    private int retryCount;

    @Override
    public boolean retry(ITestResult result) {
        int currentResultHash = getResultHash(result);

        if (resultHash != currentResultHash) {
            resultHash = currentResultHash;
            retryCount = 0;
        }

        if (retryCount < MAX_RETRY_COUNT) {
            TestLog.step("Retry : " + result.getTestContext().getName() + " => " + result.getName());
            retryCount++;
            return true;
        }

        return false;
    }
}
