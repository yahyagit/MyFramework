package framework.asserts;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;
import org.testng.Reporter;

import java.util.Arrays;
import java.util.List;

    /*
    Created by http://seleniumexamples.com/blog/guide/using-soft-assertions-in-testng
     */

public class SoftAssertListener implements IInvokedMethodListener {

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult result) {

        Reporter.setCurrentTestResult(result);

        if (method.isTestMethod()) {

            List<Throwable> verificationFailures = SoftAssert.getVerificationFailures();

            //if there are verification failures...
            if (verificationFailures.size() > 0) {

                //set the test to failed
                result.setStatus(ITestResult.FAILURE);

                //if there is an assertion failure add it to verificationFailures
                if (result.getThrowable() != null) {
                    verificationFailures.add(result.getThrowable());
                }

                int size = verificationFailures.size();
                //if there's only one failure just set that
                if (size == 1) {
                    result.setThrowable(verificationFailures.get(0));
                } else {
                    //create a failure message with all failures and stack traces (except last failure)
                    StringBuffer failureMessage = new StringBuffer("Multiple failures (").append(size).append("):\n\n");
                    for (int i = 0; i < size-1; i++) {
                        failureMessage.append("Failure ").append(i+1).append(" of ").append(size).append(":\n");

                        String fullStackTrace = getStackTrace(verificationFailures.get(i));

                        failureMessage.append(fullStackTrace).append("\n\n");
                    }

                    //final failure
                    Throwable last = verificationFailures.get(size-1);
                    failureMessage.append("Failure ").append(size).append(" of ").append(size).append(":\n");
                    String fullStackTrace = getStackTrace(last);
                    failureMessage.append(fullStackTrace);

                    //set merged throwable
                    Throwable merged = new Throwable(failureMessage.toString());
                    merged.setStackTrace(last.getStackTrace());

                    result.setThrowable(merged);
                }
            }
        }
    }

    public String getStackTrace(Throwable t){
        List<String> stackTraceElements = Lists.transform(Arrays.asList(t.getStackTrace()), new Function<StackTraceElement, String>() {
            @Override
            public String apply(StackTraceElement stackTraceElement) {
                return stackTraceElement.toString();
            };
        });
        int indexOfTestClassNameElement = stackTraceElements.indexOf("sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)")-1;
        String fullStackTrace = t.getLocalizedMessage() + "\n"
                + stackTraceElements.get(0) + "\n"
                + ".........................." + "\n"
                + stackTraceElements.get(indexOfTestClassNameElement) + "...";

        return fullStackTrace;
    }
}
