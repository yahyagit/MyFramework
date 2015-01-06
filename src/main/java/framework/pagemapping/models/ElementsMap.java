package framework.pagemapping.models;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root
public class ElementsMap {
    @ElementList(inline=true)
    private List<PageMap> pageMaps;

    public List<PageMap> getPageMaps() {
        return pageMaps;
    }
}