**⚠️ Only works for VIT Vellore**
# VTOP-Scraper
Uses selenium to login to vtop and scrape all user details

# How to use?
To enter credentials go to the following filepath:`VTOP-Scraper/app/src/main/java/com/scraper/utilities/config.json`

Enter the user name and password at the mentioned location and run App.java.

If the `config.json` doesn't exist at the expected file path the code will default to using semester ID **'VL20232401'** which will not return any data for anyone who joined after Fall 2023.

# Important
To solve captcha a javascript is executed but it can't solve google reCaptcha. To avoid user intervention the login page is refreshed until the `captchStr` element can be located in the html.

If any unexpected pop up message shows up upon logging in the javascripts fail to execute. For eg: malpractice notice during examinations.

Each js file under `VTOP-Scraper/app/src/main/java/com/scraper/utilities` has a `data` variable in the first line containing `semId`, this is replaced by the current semester ID using `data.json` which saves the response given by running `scraper.js` (other than scraper.js no other javascript file can be executed alone without replacing the placeholder `semId` with the desired semID).

All javascripts are executed using a single function call which takes the filepath of the js and save location of response as arguments in the main function. Which means more scripts can be easily added without needing to touch other functions (solveCaptcha function needs to run before any js can be executed). 
