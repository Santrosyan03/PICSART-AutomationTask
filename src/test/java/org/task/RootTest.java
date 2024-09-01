package org.task;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.time.Duration;

public class RootTest {
    protected WebDriver driver;
    protected String resolution;

    @Parameters({"resolution"})
    @BeforeClass
    public void setup() {
        WebDriverManager.chromedriver().setup();
        resolution = "1366x768";
        driver = new ChromeDriver();

        switch (resolution) {
            case "1024x768":
                driver.manage().window().setSize(new Dimension(1024, 768));
                break;
            case "1440x900":
                driver.manage().window().setSize(new Dimension(1440, 900));
                break;
            case "1366x768":
                driver.manage().window().setSize(new Dimension(1366, 768));
                break;
        }

        driver.get("https://picsart.com/");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/search'] > svg"))).click();
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        driver.switchTo().frame(driver.findElement(By.cssSelector("iframe")));
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
