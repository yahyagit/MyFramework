package framework.pagemapping.pagefactory;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
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
import ui.elements.common.ERPCommonElement;

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
                ERPCommonElement.class.isAssignableFrom(field.getType()) ||
                isDecoratableList(field))) {
            return null;
        }

        ElementLocator locator = factory.createLocator(field);
        if (locator == null) {
            return null;
        }

        if (WebElement.class.isAssignableFrom(field.getType())) {
            return proxyForLocator(loader, locator);
        } else if (ERPCommonElement.class.isAssignableFrom(field.getType())) {
            return transformToERPElement(proxyForLocator(loader, locator), field);
        } else if (List.class.isAssignableFrom(field.getType())) {
            if (WebElement.class.equals(getTypeOfGenericField(field))) {
                return proxyForListLocator(loader, locator);
            } else {
                return transformToERPElementList(proxyForListLocator(loader, locator), field);
            }
        } else {
            return null;
        }
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
        if (!(WebElement.class.equals(listType) ||
                ERPCommonElement.class.equals(getClassFromGenericField(listType).getSuperclass()))) {
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

    private ERPCommonElement transformToERPElement(WebElement wrappedElement, Field field) {
        return getERPElement(getFullNameFromMap(field), field.getType().asSubclass(ERPCommonElement.class), wrappedElement);
    }

    private List<ERPCommonElement> transformToERPElementList(List<WebElement> wrappedElementList, Field field) {
        final String fullname = getFullNameFromMap(field);
        final Class clazz = getClassFromGenericField(field);
        return Lists.transform(wrappedElementList, new Function<WebElement, ERPCommonElement>() {
            @Override
            public ERPCommonElement apply(WebElement input) {
                return getERPElement(fullname, clazz, input);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private ERPCommonElement getERPElement(String fullname, Class clazz, WebElement wrappedElement) {
        Object result;
        try {
            if (fullname == null) {
                result = clazz.getConstructor(WebElement.class).newInstance(wrappedElement);
            } else {
                result = clazz.getConstructor(WebElement.class, String.class).newInstance(wrappedElement, fullname);
            }
            return (ERPCommonElement) result;
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Something wrong in decorator");
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
