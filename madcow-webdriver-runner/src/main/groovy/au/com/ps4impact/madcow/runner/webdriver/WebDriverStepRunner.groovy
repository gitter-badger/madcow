package au.com.ps4impact.madcow.runner.webdriver

import au.com.ps4impact.madcow.step.MadcowStepRunner
import au.com.ps4impact.madcow.MadcowTestCase
import au.com.ps4impact.madcow.step.MadcowStep
import org.openqa.selenium.WebDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.apache.commons.lang3.StringUtils

/**
 * Implementation of the WebDriver step runner.
 */
class WebDriverStepRunner extends MadcowStepRunner {

    public WebDriver driver;

    WebDriverStepRunner() {
        driver = new HtmlUnitDriver();
    }

    public void execute(MadcowTestCase testCase, MadcowStep step) {

        WebDriverBladeRunner bladeRunner = WebDriverBladeRunner.getBladeRunner(StringUtils.capitalize(step.blade.operation));
        bladeRunner.execute(this, step);
    }
}
