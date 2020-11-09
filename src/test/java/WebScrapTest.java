import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.bson.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class WebScrapTest {
    WebDriver driver;
    MongoCollection<Document> webCollection;

    @BeforeSuite
    public void connectMongoDB() {
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("autoDB");
        webCollection = database.getCollection("web");
    }

    @BeforeTest
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        driver = new ChromeDriver(chromeOptions);
    }

    @DataProvider
    public Object[][] getWebData(){
        return new Object[][]{
                {"https://www.amazon.com/"},
                {"https://www.flipkart.com/"},
                {"https://www.snapdeal.com/"}
        };
    }

    @Test(dataProvider = "getWebData")
    public void webScrapTest(String appUrl) {
        driver.get(appUrl);
        String url = driver.getCurrentUrl();
        String title = driver.getTitle();

        int linksCount = driver.findElements(By.tagName("a")).size();
        int imagesCount = driver.findElements(By.tagName("img")).size();

        List<WebElement> linksList = driver.findElements(By.tagName("a"));
        List<String> linksAttributeList = new ArrayList<String>();
        Document document = new Document();
        document.append("URL", url);
        document.append("Title", title);
        document.append("totalLinks",linksCount);
        document.append("totalImages",imagesCount);
        for (WebElement ele : linksList){
            String hrefValue = ele.getAttribute("href");
            linksAttributeList.add(hrefValue);

        }
        document.append("linkAttriubte",linksAttributeList);
        List<Document> docsList = new ArrayList<Document>();
        docsList.add(document);
        webCollection.insertMany(docsList);
    }

    @AfterTest
    public void tearDown() {
        driver.quit();
    }
}
