package framework.mail;

import framework.data.PropertiesReader;
import microsoft.exchange.webservices.data.*;

import java.net.URI;

/**
 * Created by p570341 on 9/17/2014.
 */
public class ExchangeMail {

    private String exchangeURL = PropertiesReader.getInstance().getExchangeURL();
    private String exchangeUserName = PropertiesReader.getInstance().getExchangeUserName();
    private String exchangePassword = PropertiesReader.getInstance().getExchangePassword();
    private ExchangeService exchangeService;

    public ExchangeMail() {
        exchangeService = new ExchangeService(ExchangeVersion.Exchange2007_SP1);
        ExchangeCredentials credentials = new WebCredentials(exchangeUserName, exchangePassword);
        exchangeService.setCredentials(credentials);
        //exchangeService.setTraceEnabled(true);
        exchangeService.setUrl(URI.create(exchangeURL));
    }

    public ExchangeService getExchangeService() {
        return exchangeService;
    }

    public FindItemsResults<Item> getLastMailsFromFolder(WellKnownFolderName folder, int mailsAmount) {
        PropertySet itemPropertySet = new PropertySet(BasePropertySet.FirstClassProperties);
        itemPropertySet.setRequestedBodyType(BodyType.Text);

        FindItemsResults<Item> results = null;
        ItemView itemView = new ItemView(mailsAmount);
        itemView.setPropertySet(itemPropertySet);

        try {
            results = exchangeService.findItems(folder, itemView);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            exchangeService.loadPropertiesForItems(results, itemPropertySet);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }
}
