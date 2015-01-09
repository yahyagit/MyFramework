package framework.pagemapping.pagefactory;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.FieldDecorator;
import org.openqa.selenium.support.pagefactory.internal.LocatingElementHandler;
import org.openqa.selenium.support.pagefactory.internal.LocatingElementListHandler;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.List;

public class XmlMapFieldDecorator implements FieldDecorator {
    private XmlMapElementLocatorFactory factory;

    public XmlMapFieldDecorator(XmlMapElementLocatorFactory factory) {
        this.factory = factory;
    }

    public Object decorate(ClassLoader loader, Field field) {
        if (!(WebElement.class.isAssignableFrom(field.getType()) ||
                isDecoratableList(field))) {
            return null;
        }

        ElementLocator locator = factory.createLocator(field);
        if (locator == null) {
            return null;
        }

        if (WebElement.class.isAssignableFrom(field.getType())) {
            return proxyForLocator(loader, locator);
        }else if (List.class.isAssignableFrom(field.getType())) {
            if (WebElement.class.equals(getTypeOfGenericField(field))) {
                return proxyForListLocator(loader, locator);
            }
        }
        return null;
    }

    private boolean isDecoratableList(Field field) {
        if (!List.class.isAssignableFrom(field.getType())) {
            return false;
        }

        Type genericType = field.getGenericType();
        if (!(genericType instanceof ParameterizedType)) {
            return false;
        }

        Type listType = getTypeOfGenericField(field);
        if (!(WebElement.class.equals(listType))) {
            return false;
        }

        return !(field.getAnnotation(FindBy.class) == null &&
                field.getAnnotation(FindBys.class) == null &&
                field.getAnnotation(FindAll.class) == null &&
                field.getAnnotation(FindInMap.class) == null);
    }

    private WebElement proxyForLocator(ClassLoader loader, ElementLocator locator) {
        InvocationHandler handler = new LocatingElementHandler(locator);

        WebElement proxy;
        proxy = (WebElement) Proxy.newProxyInstance(
                loader, new Class[]{WebElement.class, WrapsElement.class, Locatable.class}, handler);
        return proxy;
    }

    @SuppressWarnings("unchecked")
    private List<WebElement> proxyForListLocator(ClassLoader loader, ElementLocator locator) {
        InvocationHandler handler = new LocatingElementListHandler(locator);

        List<WebElement> proxy;
        proxy = (List<WebElement>) Proxy.newProxyInstance(
                loader, new Class[] {List.class}, handler);
        return proxy;
    }

    private String getFullNameFromMap(Field field) {
        return XmlMapUtils.getPageElement(factory.getElementsMap(), factory.getPageClass(), field).getFullname();
    }

    private Type getTypeOfGenericField(Field field) {
        return ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
    }

    private Class getClassFromGenericField(Type type) {
        try {
            String typeString = type.toString();
            return Class.forName(typeString.substring(6, typeString.length()));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Class getClassFromGenericField(Field field) {
        return getClassFromGenericField(getTypeOfGenericField(field));
    }
}
