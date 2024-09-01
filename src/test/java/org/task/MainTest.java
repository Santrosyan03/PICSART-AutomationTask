package org.task;

import org.openqa.selenium.*;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.*;
import java.util.NoSuchElementException;

import static org.testng.Assert.*;

public class MainTest extends RootTest {
    @Test
    void checkFilterFunctionality() {
        WebElement filterButton = driver.findElement(By.id("filter_icon"));
        filterButton.click();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (resolution.equals("1024x768")) {
            // verify that filter appears
            assertTrue(driver.findElement(By.xpath("//*[@id=\"content-height-calculator\"]/div[2]/div[1]/div/div")).isDisplayed());
        } else {
            // verify that filter disappears
            assertFalse(driver.findElement(By.xpath("//*[@id=\"content-height-calculator\"]/div[2]/div[1]/div/div")).isDisplayed());
        }

        filterButton.click();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (resolution.equals("1024x768")) {
            // verify that filter disappears
            assertFalse(driver.findElement(By.xpath("//*[@id=\"content-height-calculator\"]/div[2]/div[1]/div/div")).isDisplayed());
        } else {
            // verify that filter appears
            assertTrue(driver.findElement(By.xpath("//*[@id=\"content-height-calculator\"]/div[2]/div[1]/div/div")).isDisplayed());
        }
    }

    @Test
    void checkPersonalFilter() {
        WebElement personalFilterCheckBox = driver.findElement(By.cssSelector("#checkbox_filter_item2 input"));
        personalFilterCheckBox.click();

        Set<WebElement> allElements = new HashSet<>();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        long lastHeight = (long) js.executeScript("return document.body.scrollHeight");

        while (true) {
            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            List<WebElement> newElements = driver.findElements(By.cssSelector("[data-testid='search-card-root']"));
            allElements.addAll(newElements);

            long newHeight = (long) js.executeScript("return document.body.scrollHeight");
            if (newHeight == lastHeight) {
                break;
            }
            lastHeight = newHeight;
        }

        List<WebElement> allElementsList = new ArrayList<>(allElements);
        for (int k = 0; k < allElementsList.size(); k++) {
            assertEquals(allElementsList.get(k).findElement(By.cssSelector("a")).getAttribute("rel"), "nofollow");
            k++;
        }

        Actions actions = new Actions(driver);

//        if (!allElements.isEmpty()) {
//            WebElement firstAsset = allElements.iterator().next();
//            actions.moveToElement(firstAsset).perform();
//
//            WebElement likeButton = driver.findElement(By.cssSelector("#" + firstAsset.getAttribute("id") + " .like-button"));
//            WebElement saveButton = driver.findElement(By.cssSelector("#" + firstAsset.getAttribute("id") + " .save-button"));
//            WebElement tryNowButton = driver.findElement(By.cssSelector("#" + firstAsset.getAttribute("id") + " .try-now-button"));
//
//            assertTrue(likeButton.isDisplayed(), "Like button not displayed on hover!");
//            assertTrue(saveButton.isDisplayed(), "Save button not displayed on hover!");
//            assertTrue(tryNowButton.isDisplayed(), "Try now button not displayed on hover!");
//        }



        int i = 0;
        for (WebElement element : allElements) {
            actions.moveToElement(element).perform();
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.id("base_card_item" + i)));

            boolean isAiImage = !element.findElements(By.cssSelector("[data-testid='ai-card-root']")).isEmpty();

            boolean likeButton;
            boolean saveButton;
            boolean actionButton;
            likeButton = !driver.findElements(By.id("like_button_item" + i)).isEmpty();
            saveButton = !driver.findElements(By.id("save_button_item" + i)).isEmpty();

            if (!isAiImage) {
                actionButton = !driver.findElements(By.id("try_now_button_item" + i)).isEmpty();
            } else {
                actionButton = !driver.findElements(By.id("content_grid_regenerate_button" + i)).isEmpty();
            }

            assertTrue(likeButton);
            assertTrue(saveButton);
            assertTrue(actionButton);

            driver.findElement(By.cssSelector("button[id*='like_button']")).click();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            assertTrue(driver.findElement(By.cssSelector("div[data-testid='registration-modal-container']")).isDisplayed());
            driver.findElement(By.cssSelector("svg[data-testid='modal-close-icon']")).click();
            personalFilterCheckBox.click();
        }
    }

    @Test
    void plusAssetFunctionality() {
        WebElement assetWithPlusIcon;
        int i = 0;
        Set<WebElement> allElements = new HashSet<>();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        long lastHeight = (long) js.executeScript("return document.body.scrollHeight");
        while (driver.findElement(By.cssSelector("#base_card_item" + i + " > a")).getAttribute("rel") != "nofollow" &&
               !driver.findElement(By.cssSelector("#base_card_item" + i + " > div > svg")).isDisplayed()) {

                js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                List<WebElement> newElements = driver.findElements(By.cssSelector("[data-testid='search-card-root'], [data-testid='ai-card-root']"));

                allElements.addAll(newElements);

                long newHeight = (long) js.executeScript("return document.body.scrollHeight");

                if (newHeight == lastHeight) {
                    break;
                }

                lastHeight = newHeight;
                i++;

        }


        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.id("base_card_item" + i)));

        assetWithPlusIcon = driver.findElement(By.id("base_card_item" + i));
        Actions actions = new Actions(driver);
        actions.moveToElement(assetWithPlusIcon).perform();
        boolean tryNowButtonPresent = !assetWithPlusIcon.findElements(By.id("#base_card_item" + i + " div:nth-child(3) button")).isEmpty();
        boolean likeButtonAbsent = assetWithPlusIcon.findElements(By.id("like_button_item" + i)).isEmpty();
        boolean saveButtonAbsent = assetWithPlusIcon.findElements(By.id("save_button_item" + i)).isEmpty();

        assertTrue(tryNowButtonPresent);
        assertTrue(likeButtonAbsent);
        assertTrue(saveButtonAbsent);
    }

}
