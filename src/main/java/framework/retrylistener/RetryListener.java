package framework.retrylistener;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static framework.retrylistener.RetryUtils.getResultHash;

public class RetryListener extends TestListenerAdapter {

    /*
    Created by http://stackoverflow.com/questions/13131912/testng-retrying-failed-tests-doesnt-output-the-correct-test-results
     */

    @Override
    public void onFinish(ITestContext testContext) {
        super.onFinish(testContext);

        List<ITestResult> testsToBeRemoved = new ArrayList<ITestResult>();
        Set<Integer> passedTestHashSet = new HashSet<Integer>();
        Set<Integer> failedTestHashSet = new HashSet<Integer>();

        for (ITestResult passedTest : testContext.getPassedTests().getAllResults()) {
            passedTestHashSet.add(getResultHash(passedTest));
        }

        for (ITestResult failedTest : testContext.getFailedTests().getAllResults()) {
            int failedTestHash = getResultHash(failedTest);

            if (failedTestHashSet.contains(failedTestHash) || passedTestHashSet.contains(failedTestHash)) {
                testsToBeRemoved.add(failedTest);
            } else {
                failedTestHashSet.add(failedTestHash);
            }
        }

        for (Iterator<ITestResult> iterator = testContext.getFailedTests().getAllResults().iterator(); iterator.hasNext(); ) {
            ITestResult testResult = iterator.next();
            if (testsToBeRemoved.contains(testResult)) {
                iterator.remove();
            }
        }
    }
}
