package framework.tools;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.IOException;

public class ScrollTools {
    private static final String BODY = "'body'";

    private String libraryText;
    private JavascriptExecutor jsExecutor;

    public ScrollTools(File scrollToJSLib, WebDriver webDriver) {
        try {
            libraryText = FileUtils.readFileToString(scrollToJSLib, "UTF-8");
            jsExecutor = (JavascriptExecutor) webDriver;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void scrollPage(int value, int scrollSpeed) {
        jsExecutor.executeScript(getMethod(BODY, getRelativePosition(value), scrollSpeed));
    }

    public void scrollPage(WebElement targetElement, int scrollSpeed) {
        jsExecutor.executeScript(getMethod(BODY, getArguments(0), scrollSpeed), targetElement);
    }

    public void scrollElement(WebElement scrolledElement, int value, int scrollSpeed) {
        jsExecutor.executeScript(getMethod(getArguments(0), getRelativePosition(value), scrollSpeed), scrolledElement);
    }

    public void scrollElement(WebElement scrolledElement, WebElement targetElement, int scrollSpeed) {
        jsExecutor.executeScript(getMethod(getArguments(0), getArguments(1), scrollSpeed), scrolledElement, targetElement);
    }

    private String getRelativePosition(int value) {
        String absString = "=" + Math.abs(value) + "px";
        return value < 0 ? "-" + absString : "+" + absString;
    }

    private String getArguments(int index) {
        return "arguments[" + index + "]";
    }

    private String getMethod(String firstArgument, String secondArgument, int scrollSpeed) {
        return "if (jQuery().scrollTo === undefined) {\n" +
                libraryText + "\n" +
                "};\n" +
                "jQuery(" + firstArgument + ").scrollTo('" + secondArgument + "', " + scrollSpeed + ");";
    }
}
