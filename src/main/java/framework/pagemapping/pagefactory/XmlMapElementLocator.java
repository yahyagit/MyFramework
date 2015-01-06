package framework.pagemapping.pagefactory;

import framework.pagemapping.models.ElementsMap;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.lang.reflect.Field;
import java.util.List;

public class XmlMapElementLocator implements ElementLocator {
    private SearchContext searchContext;
    private final boolean shouldCache;
    private final By by;
    private WebElement cachedElement;
    private List<WebElement> cachedElementList;
    private Class pageClass;

    public XmlMapElementLocator(SearchContext searchContext, Class pageClass, Field field, ElementsMap elementsMap) {
        this.searchContext = searchContext;
        XmlMapAnnotations annotations = new XmlMapAnnotations(field);
        annotations.setPageClass(pageClass);
        shouldCache = annotations.isLookupCached();
        by = annotations.buildWithMapThenWithBy(elementsMap);
    }

    @Override
    public WebElement findElement() {
        if (cachedElement != null && shouldCache) {
            return cachedElement;
        }

        WebElement element = searchContext.findElement(by);
        if (shouldCache) {
            cachedElement = element;
        }

        return element;
    }

    @Override
    public List<WebElement> findElements() {
        if (cachedElementList != null && shouldCache) {
            return cachedElementList;
        }

        List<WebElement> elements = searchContext.findElements(by);
        if (shouldCache) {
            cachedElementList = elements;
        }

        return elements;
    }
}
