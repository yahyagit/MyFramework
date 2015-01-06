package framework.pagemapping.models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root
public class PageMap {
    @Attribute
    protected String name;

    @ElementList(inline=true)
    protected List<PageElement> pageElements;

    public String getName() {
        return name;
    }

    public List<PageElement> getPageElements() {
        return pageElements;
    }
}