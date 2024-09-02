package org.task;

import org.openqa.selenium.*;

import org.openqa.selenium.interactions.Actions;

import org.testng.annotations.Test;

import java.util.*;

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

            List<WebElement> newElements = driver.findElements(By.cssSelector("div[data-automation='search-item-fte']"));
            allElements.addAll(newElements);

            long newHeight = (long) js.executeScript("return document.body.scrollHeight");
            if (newHeight == lastHeight) break;
            lastHeight = newHeight;
        }


        List<WebElement> allElementsList = new ArrayList<>(allElements);
        WebElement asset;
        int k;
         for (k = 0; k < allElementsList.size(); k++) {
            assertFalse(driver.findElement(By.cssSelector("#base_card_item" + allElementsList.get(k).getAttribute("id").substring(14) + " > a")).getAttribute("rel") == "nofollow");
            k++;
        }

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.id("base_card_item" +  allElementsList.get(k-1).getAttribute("id").substring(14))));

        asset = driver.findElement(By.id("base_card_item" +  allElementsList.get(k-1).getAttribute("id").substring(14)));
        Actions actions = new Actions(driver);
        actions.moveToElement(asset).perform();
        WebElement tryNowButtonPresent = asset.findElement(By.cssSelector("#base_card_item" +  allElementsList.get(k-1).getAttribute("id").substring(14) + " div > button"));
        WebElement likeButtonAbsent = asset.findElement(By.cssSelector("#base_card_item" +  allElementsList.get(k-1).getAttribute("id").substring(14) + " button:nth-child(1)"));
        WebElement saveButtonAbsent = asset.findElement(By.cssSelector("#base_card_item" +  allElementsList.get(k-1).getAttribute("id").substring(14) + " button:nth-child(2)"));

        assertNotNull(tryNowButtonPresent);
        assertNotNull(likeButtonAbsent);
        assertNotNull(saveButtonAbsent);
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