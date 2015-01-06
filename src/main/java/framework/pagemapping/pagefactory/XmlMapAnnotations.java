package framework.pagemapping.pagefactory;

import framework.pagemapping.models.ElementsMap;
import framework.pagemapping.models.PageElement;
import org.openqa.selenium.By;
import org.openqa.selenium.support.pagefactory.Annotations;

import java.lang.reflect.Field;

public class XmlMapAnnotations extends Annotations {
    private Field field;
    private Class pageClass;

    public XmlMapAnnotations(Field field) {
        super(field);
        this.field = field;
    }

    public By buildWithMapThenWithBy(ElementsMap elementsMap) {
        FindInMap findInMap = field.getAnnotation(FindInMap.class);
        if (findInMap != null) {
            return buildWithMap(elementsMap);
        }
        return buildBy();
    }

    private By buildWithMap(ElementsMap elementsMap) {
        PageElement pageElement = XmlMapUtils.getPageElement(elementsMap, pageClass, field);
        String by = pageElement.getBy();
        String using = pageElement.getLocator();

        if (by.equals("class_name")) {
            return By.className(using);
        } else if(by.equals("css")) {
            return By.cssSelector(using);
        } else if(by.equals("id")) {
            return By.id(using);
        } else if(by.equals("link_text")) {
            return By.linkText(using);
        } else if(by.equals("name")) {
            return By.name(using);
        } else if(by.equals("partial_link_text")) {
            return By.partialLinkText(using);
        } else if(by.equals("tag_name")) {
            return By.tagName(using);
        } else if(by.equals("xpath")) {
            return By.xpath(using);
        } else {
            throw new IllegalArgumentException("Non correct 'By' in " + pageElement.getName());
        }
    }

    public void setPageClass(Class pageClass) {
        this.pageClass = pageClass;
    }
}
