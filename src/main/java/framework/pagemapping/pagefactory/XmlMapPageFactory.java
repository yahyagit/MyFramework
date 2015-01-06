package framework.pagemapping.pagefactory;

import framework.pagemapping.models.ElementsMap;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class XmlMapPageFactory extends PageFactory {
    public static void initElements(final WebDriver driver, ElementsMap elementsMap, Object page) {
        initElements(
                new XmlMapFieldDecorator(
                        new XmlMapElementLocatorFactory(
                                driver, elementsMap, page.getClass())), page);
    }
}
