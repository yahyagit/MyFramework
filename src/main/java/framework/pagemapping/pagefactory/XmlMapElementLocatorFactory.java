package framework.pagemapping.pagefactory;

import framework.pagemapping.models.ElementsMap;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

import java.lang.reflect.Field;

public final class XmlMapElementLocatorFactory implements ElementLocatorFactory {
    private final SearchContext searchContext;
    private final ElementsMap elementsMap;
    private Class pageClass;

    public XmlMapElementLocatorFactory(SearchContext searchContext, ElementsMap elementsMap, Class pageClass) {
        this.searchContext = searchContext;
        this.elementsMap = elementsMap;
        this.pageClass = pageClass;
    }

    @Override
    public ElementLocator createLocator(Field field) {
        return new XmlMapElementLocator(searchContext, pageClass, field, elementsMap);
    }

    public Class getPageClass() {
        return pageClass;
    }

    public ElementsMap getElementsMap() {
        return elementsMap;
    }
}