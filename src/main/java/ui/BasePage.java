package ui;

import framework.Service;
import framework.data.PropertiesReader;
import framework.pagemapping.pagefactory.XmlMapPageFactory;
import framework.tools.XmlDeserializer;

/**
 * Created by apaliy on 1/6/2015.
 */
public abstract class BasePage {

        protected static final int MAX_SLEEP = PropertiesReader.getInstance().getMaxSleepTimeout();
        private static final String ELEMENTS_MAP_PATH = "page_maps.xml";

        public BasePage() {
        XmlMapPageFactory.initElements(
                Service.getInstance().getCurrentDriver(),
                XmlDeserializer.getElementsMap(ELEMENTS_MAP_PATH), this);
    }


}
