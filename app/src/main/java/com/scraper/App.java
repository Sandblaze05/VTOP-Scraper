package com.scraper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;



public class App {

    static String config[] = processConfig("app\\src\\main\\java\\com\\scraper\\utilities\\config.json");

    public static void overrideCaptcha(ChromeDriver driver)throws Exception{ //until recaptcha changes to captcha
        By elementLocator = By.id("captchaStr");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(0));
        int maxRetries = 10; int retries = 0;
        while(retries < maxRetries){
            String currentPage = driver.getCurrentUrl();
            try{
                //String arr[] = processConfig("test\\src\\config.json");
                WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(elementLocator));
                WebElement textBoxElement = driver.findElement(By.id("username"));
                textBoxElement.click();
                textBoxElement.sendKeys(config[0]);
                
                WebElement textBoxElement2 = driver.findElement(By.id("password"));
                textBoxElement2.click();
                textBoxElement2.sendKeys(config[1]);
                System.out.println("found\nlogging in");
                break;
            }
            catch(Exception e){
                System.out.println("refresh");
                driver.get(currentPage);
                retries++;
            }
        }
        if(retries > maxRetries){
            System.out.println("max retries");
        }

    }

    public static int solveCaptcha(ChromeDriver driver) throws Exception{ //captcha solver
        String content = "";
        try (BufferedReader reader = new BufferedReader(new FileReader("app\\src\\main\\java\\com\\scraper\\utilities\\captchasolver.js"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content += line;
            }
        } 
        catch(IOException e) {
        }
        finally{
            overrideCaptcha(driver);
            driver.executeScript(content);
        }
        return 0;
    }

    public static String executeJavaScriptFile(ChromeDriver driver, String filePath, String storePath) throws IOException { //for executing and processing ajax
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String sId = "\'"+getID("app\\src\\main\\java\\com\\scraper\\Extracted\\data.json")+"\'";
        System.out.println(sId);
        // Execute the JavaScript code from the file
        String result =(String) ((JavascriptExecutor)driver).executeScript(content.toString().replaceAll("semId", sId));
        System.out.println(result);
        storeData(storePath, result);
        return result;
           
    }

    public static String getID(String filePath){
        try{
        ObjectMapper objectMapper = new ObjectMapper();
            File jsonFile = new File(filePath);

            if (!jsonFile.exists()) {
                System.out.println("File not found: " + filePath );
                return "VL20232401"; //default to this semester ID
            }

            JsonNode rootNode = objectMapper.readTree(jsonFile);

            // Iterate through the semesters array
            JsonNode semesters = rootNode.get("semesters");
            for (JsonNode semester : semesters) {
                String name = semester.get("name").asText();
                if (name.equals(config[2])) {
                    String id = semester.get("id").asText();
                    System.out.println(id);
                    return id;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String[] processConfig(String filePath){
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            File jsonFile = new File(filePath);

            if (!jsonFile.exists()) {
                System.out.println("File not found: " + filePath);
                return null;
            }

            JsonNode rootNode = objectMapper.readTree(jsonFile);

            // Iterate through the semesters array
            JsonNode credentials = rootNode.get("credentials");
            for (JsonNode i : credentials) {
                String username = i.get("username").asText();
                String pswd = i.get("password").asText();
                String sem = i.get("current sem").asText();
                String arr[] = {username,pswd,sem};
                return arr;
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return null;
    }


    public static void storeData(String filePath, String data) throws IOException{
        FileWriter file = new FileWriter(filePath);
        BufferedWriter buffer = new BufferedWriter(file);
        buffer.write(data);
        buffer.flush();
        buffer.close();
    }
    
    
    public static void main(String[] args) throws Exception {
        try{

            File f= new File("app\\src\\main\\java\\com\\scraper\\Extracted\\data.json");
            if(f.exists())
            {
                f.delete();
            }
            //EdgeDriver driver = new EdgeDriver();
            ChromeDriver driver = new ChromeDriver();
            
            
            driver.get("https://vtop.vit.ac.in/vtop/content"); //startup
            System.out.println("starting login");
            driver.findElement(By.id("stdForm")).click();
            
            
            solveCaptcha(driver); //onboarding
            executeJavaScriptFile(driver, "app\\src\\main\\java\\com\\scraper\\utilities\\scraper.js", "app\\src\\main\\java\\com\\scraper\\Extracted\\data.json");
            executeJavaScriptFile(driver, "app\\src\\main\\java\\com\\scraper\\utilities\\TimeTablescraper.js", "app\\src\\main\\java\\com\\scraper\\Extracted\\TimeTabledata.json");
            executeJavaScriptFile(driver, "app\\src\\main\\java\\com\\scraper\\utilities\\Attendancescraper.js", "app\\src\\main\\java\\com\\scraper\\Extracted\\AttendanceData.json");
            executeJavaScriptFile(driver, "app\\src\\main\\java\\com\\scraper\\utilities\\Coursescraper.js", "app\\src\\main\\java\\com\\scraper\\Extracted\\CourseData.json");
            executeJavaScriptFile(driver, "app\\src\\main\\java\\com\\scraper\\utilities\\Marksscraper.js", "app\\src\\main\\java\\com\\scraper\\Extracted\\MarksData.json");
            executeJavaScriptFile(driver, "app\\src\\main\\java\\com\\scraper\\utilities\\CGPAscraper.js", "app\\src\\main\\java\\com\\scraper\\Extracted\\CGPAData.json");
            executeJavaScriptFile(driver, "app\\src\\main\\java\\com\\scraper\\utilities\\ExamSchedulescraper.js", "app\\src\\main\\java\\com\\scraper\\Extracted\\ExamScheduleData.json");
            driver.quit();
        }
        catch(Exception e){
            System.err.println(e);
        }
    }
}
