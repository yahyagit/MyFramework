package ui.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.HtmlElement;
import ru.yandex.qatools.htmlelements.element.TextInput;
import ru.yandex.qatools.htmlelements.loader.HtmlElementLoader;

/**
 * Created by apaliy on 1/8/2015.
 */
public class GoogleMainPage extends HtmlElement{
//    @FindInMap
    @FindBy(xpath = "//input[@id='text']")
    private TextInput searchField;
//    @FindInMap
    @FindBy(xpath = "//button")
    private Button searchButton;

    public GoogleMainPage(WebDriver driver) {
        HtmlElementLoader.populatePageObject(this, driver);
    }


    public void setSearchField(String text) {
        searchField.clear();
        searchField.sendKeys(text);
    }

    public GoogleResultPage clickSearchButton() {
        searchButton.click();
        return new GoogleResultPage();
    }
}
