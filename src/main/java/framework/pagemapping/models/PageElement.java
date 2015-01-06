package framework.pagemapping.models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root
public class PageElement {
    @Attribute
    protected String name;

    @Attribute
    protected String by;

    @Attribute
    protected String locator;

    @Attribute(required = false)
    protected String fullname;

    public String getName() {
        return name;
    }

    public String getBy() {
        return by;
    }

    public String getLocator() {
        return locator;
    }

    public String getFullname() {
        return fullname;
    }
}
