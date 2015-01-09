package ui.pages;

import framework.pagemapping.pagefactory.FindInMap;
import org.openqa.selenium.WebElement;
import ui.BasePage;

/**
 * Created by apaliy on 1/8/2015.
 */
public class GoogleMainPage extends BasePage{
    @FindInMap private WebElement searchField;
    @FindInMap private WebElement searchButton;

    public void setSearchField(String text) {
        searchField.clear();
        searchField.sendKeys(text);
    }

    public GoogleResultPage clickSearchButton() {
        searchButton.click();
        return new GoogleResultPage();
    }
}
