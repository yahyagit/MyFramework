package framework.pagemapping.pagefactory;

import framework.pagemapping.models.ElementsMap;
import framework.pagemapping.models.PageElement;
import framework.pagemapping.models.PageMap;

import java.lang.reflect.Field;

public class XmlMapUtils {
    public static PageElement getPageElement(ElementsMap elementsMap, Class clazz, Field field) {
        FindInMap findInMap = field.getAnnotation(FindInMap.class);

        String pageName = findInMap.page();
        String elementName = findInMap.element();

        if (pageName.equals("")) {
            pageName = clazz.getName();
        }

        if (elementName.equals("")) {
            elementName = field.getName();
        }

        for (PageMap page : elementsMap.getPageMaps()) {
            if (page.getName().equals(pageName)) {
                for (PageElement element : page.getPageElements()) {
                    if (element.getName().equals(elementName)) {
                        return element;
                    }
                }
            }
        }

        if(!clazz.getSuperclass().equals(Object.class)){
            return getPageElement(elementsMap, clazz.getSuperclass(), field);
        }

        throw new NullPointerException("There is no element " + elementName + " in " + pageName);
    }
}
