package utils;

import framework.Service;
import framework.data.PropertiesReader;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import ui.objects.JsAlertObject;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import static framework.reporter.TestLog.warning;
import static utils.ActionUtils.isElementPresentAndVisible;

/**
 * Created by apaliy on 1/6/2015.
 */
public class WaitUtils {
    public static void sleep(long time, TimeUnit timeUnit) {
        try {
            Thread.sleep(timeUnit.toMillis(time));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sleep(int seconds) {
        sleep(seconds, TimeUnit.SECONDS);
    }

    public static void waitForElementAppear(final WebElement element, int maxSleep) {
        new WebDriverWait(Service.getInstance().getCurrentDriver(), maxSleep).
                until(new ExpectedCondition<Boolean>() {
                    @Override
                    public Boolean apply(WebDriver webDriver) {
                        return isElementPresentAndVisible(element);
                    }
                });
    }

    public static void waitForElementDisappear(final WebElement element, int maxSleep) {
        new WebDriverWait(Service.getInstance().getCurrentDriver(), maxSleep).
                until(new ExpectedCondition<Boolean>() {
                    @Override
                    public Boolean apply(WebDriver webDriver) {
                        return !isElementPresentAndVisible(element);
                    }
                });
    }

    public static void waitForDOMChanges() {
        final int numberOfChecks = 5;
        final int waitInMillis = 250;
        int currentNumberOfChecks = 0;

        WebDriver driver = Service.getInstance().getCurrentDriver();

        try {
            while (currentNumberOfChecks < numberOfChecks) {
                String domBefore = driver.getPageSource();
                sleep(waitInMillis, TimeUnit.MILLISECONDS);
                String domAfter = driver.getPageSource();
                if (domAfter.equals(domBefore)) {
                    currentNumberOfChecks++;
                } else {
                    currentNumberOfChecks = 0;
                }
            }
        } catch (UnhandledAlertException e) {
            JsAlertObject jsAlertObject = new JsAlertObject();
            if (!"Tree load error.".equals(jsAlertObject.getText())) throw e;
            warning("Tree load error has appeared. Try to accept and refresh");

            jsAlertObject.accept();
            driver.navigate().refresh();
            waitForDOMChanges();
        }
    }

    public static void waitForLoadingDisappear() {
        int maxTime = PropertiesReader.getInstance().getMaxSleepTimeout();
        try {
            WebElement loading = Service.getInstance().getCurrentDriver().findElement(By.id("loading"));
            waitForElementAppear(loading, 2);
            for (int i = 0; i < 2; i++) {
                waitForElementDisappear(loading, maxTime);
                sleep(200, TimeUnit.MILLISECONDS);
            }
        } catch (NoSuchElementException | TimeoutException | StaleElementReferenceException ignored) {}
    }
}
