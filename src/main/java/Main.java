
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

//import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class Main {

  /*
   * EDIT THESE
   */
  static String username = "<enter username here>";
  static String password = "<enter password here>";

  public static void main(String[] aarwer) throws IOException, InterruptedException {

    System.setProperty("webdriver.chrome.driver", "chromedriver2_41");

//	HtmlUnitDriver driver = new HtmlUnitDriver(true);// new ChromeDriver();
//	driver.setJavascriptEnabled(true);
    WebDriver driver = new ChromeDriver();

    //	driver.get("https://alumsso.mit.edu/cas/login?service=https%3A%2F%2Falumsso.mit.edu%2Foauth%2Fj_spring_cas_security_check");
    driver.get("https://alum.mit.edu/directory/#/");

    //https://alum.mit.edu/directory/#/

    System.out.println("here");

    WebDriverWait wait = new WebDriverWait(driver, 5);
    wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));

    //log in

    driver.findElement(By.id("username")).sendKeys(username);
    driver.findElement(By.id("password")).sendKeys(password);
    driver.findElement(By.xpath("//input[@type='submit' and @value='Login']")).click();

    // profile links are already saved to linksBackup.txt
    //	getLinks(wait, driver);

    // this is the step that might get your account blocked?
    // out.txt contains the first 127 profiles
    // this reads and loads links from links.txt
    // just remove the first 127 or however many entries from links.txt before running tihs program to avoid re-downloading

    getProfile(wait, driver);

  }

  private static void getProfile(WebDriverWait wait, WebDriver driver) throws IOException, InterruptedException {

    //	last name, first name, employer, email address, Degrees, Preferred Class Year also
    //
    //	(not just email address like i said.  the other stuff is always out of date so not really useful)
    //	oh i guess ideally I'd get Degrees & Preferred Class Year also
    List<String> lines = Files.readAllLines(new File("links.txt").toPath());

    int count = 0;

    for (String line : lines) {
      System.out.println(count++);

      String url = line.split("\t")[1].trim();

      System.out.println(url);
      System.out.println(line);

      driver.get(url);

      String name = line.split("\t")[0].trim();

//	    wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(), '" + nameBeforeApostrophe + "')]")));

      wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='public-profile__location']")));

      String fullName = getFullName(driver);
      String location = getLocation(driver);

      String jobTitle = getProfileAttribute(driver, "Job Title");
      String company = getProfileAttribute(driver, "Company");
      String email = getProfileAttribute(driver, "Email");

      String degrees = getProfileAttribute(driver, "Degrees");
      String classYear = getProfileAttribute(driver, "Preferred Class Year");

      FileUtils.writeStringToFile(
          new File("out.txt"),
          String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%n", fullName, location, jobTitle, company, email, degrees, classYear),
          true);

      // this is because new page profile urls weren't loading otherwise for some reason
      driver.get("http://localhost/");
    }

  }

  private static String getLocation(WebDriver driver) {
    return driver.findElement(By.xpath("//div[@class='public-profile__location']")).getText().trim();
  }

  private static String getFullName(WebDriver driver) {
    return driver.findElement(By.xpath("//div[@class='public-profile__name']/h1")).getText().trim();
  }

  private static String getProfileAttribute(WebDriver driver, String string) {

    try {
      return driver.findElement(By.xpath("//div[contains(text(), '" + string + "')]/following-sibling::*")).getText().trim();
    } catch (Exception e) {
      return "";
    }

	/*
	 //div[contains(text(), 'Company')]/following-sibling::*
	 */

    //	return null;
  }

  private static String getFirstName(WebDriver driver) {
    // TODO Auto-generated method stub
    return null;
  }

  private static String getLastName(WebDriver driver) {
    // TODO Auto-generated method stub
    return null;
  }

  private static String getEmail(WebDriver driver) {
    // TODO Auto-generated method stub
    return null;
  }

  private static void getLinks(WebDriverWait wait, WebDriver driver) throws IOException {

    wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText("for Affinity Club event Sept 6")));

    driver.findElement(By.linkText("for Affinity Club event Sept 6")).click();

    wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@aria-label='Page 1']")));
    //directory-search-result__name

    new File("links.txt").delete();

    String oldFirstName = "";

    for (; ; ) {

      List<WebElement> links = driver.findElements(By.xpath("//a[contains(text(), 'Open link in a new tab')]"));

      List<WebElement> names = driver.findElements(By.xpath("//div[@class='directory-search-result__name']/a"));

      String firstName = names.get(0).getText().trim();

      if (firstName.equals(oldFirstName)) {
        throw new RuntimeException("names matched !");
      }

      for (int i = 0; i < links.size(); i++) {

        String href = links.get(i).getAttribute("href");

        String name = names.get(i).getText().trim();

        System.out.println(name + " \t" + href);

        FileUtils.writeStringToFile(new File("links.txt"), name + " \t" + href + "\n", true);

      }

      driver.findElement(By.xpath("//li[@class='pagination-next']/a")).click();

      wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//a[contains(text(), '" + firstName + "']")));
    }

  }

}
