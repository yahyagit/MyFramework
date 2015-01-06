package framework.retrylistener;

import org.testng.ITestResult;

import java.util.Arrays;

public class RetryUtils {
    public static int getResultHash(ITestResult result) {
        int id = result.getTestClass().getName().hashCode();
        id = 31 * id + result.getMethod().getMethodName().hashCode();
        id = 31 * id + (result.getParameters() != null ? Arrays.hashCode(result.getParameters()) : 0);
        return id;
    }
}
