package utils;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;

/**
 * Created by apaliy on 12/11/2014.
 */
public class FirefoxBrowserHelpers {
    public static WebDriver openNewTabWithLink(WebDriver driver, String link) throws InterruptedException {
        driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL + "t");
        Thread.sleep(2000);
        ArrayList<String> tabs = new ArrayList<String> (driver.getWindowHandles());
        driver.switchTo().window(tabs.get(0)).get(link);
        return driver;
    }
}
