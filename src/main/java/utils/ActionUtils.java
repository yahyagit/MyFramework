package utils;

import com.google.common.base.Predicate;
import framework.Service;
import framework.data.PropertiesReader;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.io.File;
import java.io.IOException;

/**
 * Created by apaliy on 1/6/2015.
 */
public class ActionUtils {
     /*
    Actions
     */

    public static void getBaseUrl() {
        Service.getInstance().getCurrentDriver().get(PropertiesReader.getInstance().getBaseURL());
        WaitUtils.waitForDOMChanges();
    }

    public static boolean isElementPresentAndVisible(WebElement element) {
        return isElementPresentAndVisibleAbstract(new Predicate<WebElement>() {
            @Override
            public boolean apply(WebElement input) {
                return input.isDisplayed();
            }
        }, element);
    }

    public static boolean isElementPresentAndVisible(final By by) {
        return isElementPresentAndVisibleAbstract(new Predicate<By>() {
            @Override
            public boolean apply(By input) {
                return Service.getInstance().getCurrentDriver().findElement(by).isDisplayed();
            }
        }, by);
    }

    public static boolean isElementPresentAndVisible(final WebElement parentElement, final By by) {
        return isElementPresentAndVisibleAbstract(new Predicate<By>() {
            @Override
            public boolean apply(By input) {
                return parentElement.findElement(by).isDisplayed();
            }
        }, by);
    }

    public static void jsExecute(String inputScript) {
        Service.getInstance().getJsExecutor().executeScript(inputScript);
    }

    public static void jsExecute(String inputScript, Object... args) {
        Service.getInstance().getJsExecutor().executeScript(inputScript, args);
    }

    public static Object jsExecuteResult(String inputScript) {
        return Service.getInstance().getJsExecutor().executeScript(inputScript);
    }

    public static Object jsExecuteResult(String inputScript, Object... args) {
        return Service.getInstance().getJsExecutor().executeScript(inputScript, args);
    }

    public static String getInnerHtmlFromElement(WebElement element) {
        return (String) jsExecuteResult("return arguments[0].innerHTML;", element);
    }

    public static void rightClick(WebElement element) {
        new Actions(Service.getInstance().getCurrentDriver()).
                contextClick(element).
                build().
                perform();
    }

    public static void multiSelectClick(WebElement element) {
        Keys multiSelectKey = Platform.getCurrent().equals(Platform.MAC) ? Keys.COMMAND : Keys.CONTROL;

        new Actions(Service.getInstance().getCurrentDriver()).
                keyDown(multiSelectKey).
                click(element).
                keyUp(multiSelectKey).
                build().
                perform();
    }

    public static void dragAndDrop(WebElement element, WebElement toElement) {
        new Actions(Service.getInstance().getCurrentDriver()).
                clickAndHold(element).
                moveToElement(toElement).
                release().
                build().
                perform();
    }

    public static boolean takeScreenshot(String fileName) {
        try {
            FileUtils.copyFile(
                    ((TakesScreenshot) Service.getInstance().getCurrentDriver()).
                            getScreenshotAs(OutputType.FILE), new File(fileName));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static <T> boolean isElementPresentAndVisibleAbstract(Predicate<T> predicate, T t) {
        Service service = Service.getInstance();
        try {
            service.disableTimeouts();
            return predicate.apply(t);
        } catch (WebDriverException e) {
            return false;
        } finally {
            service.enableTimeouts();
        }
    }
}
