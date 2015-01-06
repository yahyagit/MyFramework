package ui.objects;

import exceptions.OurRuntimeException;
import framework.Service;
import framework.data.PropertiesReader;
import framework.reporter.TestLog;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Created by apaliy on 1/6/2015.
 */
public class JsAlertObject {
    private static final int MAX_SLEEP = PropertiesReader.getInstance().getMaxSleepTimeout();
    private WebDriver driver;

    public JsAlertObject() {
        driver = Service.getInstance().getCurrentDriver();
        try {
            new WebDriverWait(driver, MAX_SLEEP).until(ExpectedConditions.alertIsPresent());
        } catch (TimeoutException e) {
            throw new OurRuntimeException("Alert was not appeared");
        }
        TestLog.comment("Alert text : \n" + getText());
    }

    public String getText() {
        return driver.switchTo().alert().getText();
    }

    public void accept() {
        TestLog.step("Accept JS alert");
        driver.switchTo().alert().accept();
    }

    public void dismiss() {
        TestLog.step("Dismiss JS alert");
        driver.switchTo().alert().dismiss();
    }
}
