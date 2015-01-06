package framework.tools;

import framework.pagemapping.models.ElementsMap;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.net.URISyntaxException;

public class XmlDeserializer {
    public static ElementsMap getElementsMap(String mapFileName) {
        return new XmlDeserializer().getObject(ElementsMap.class, mapFileName);
    }

    private <T> T getObject(Class<? extends T> type, String fileName) {
        try {
            return new Persister().read(type, new File(ClassLoader.getSystemResource(fileName).toURI()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}