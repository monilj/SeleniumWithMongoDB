import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.bson.Document;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.*;

import java.util.ArrayList;
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



    @Test
    public void webScrapTest() {
        driver.get("https//:www.amazon.com");
        String url = driver.getCurrentUrl();
        String title = driver.getTitle();
        Document document = new Document();
        document.append("URL", url);
        document.append("Title", title);
        List<Document> docsList = new ArrayList<Document>();
        docsList.add(document);
        webCollection.insertMany(docsList);
    }

    @AfterTest
    public void tearDown() {
        driver.quit();
    }
}
