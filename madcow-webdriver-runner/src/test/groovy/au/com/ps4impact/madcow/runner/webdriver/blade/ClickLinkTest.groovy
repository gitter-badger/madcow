package au.com.ps4impact.madcow.runner.webdriver.blade

import au.com.ps4impact.madcow.grass.GrassBlade
import au.com.ps4impact.madcow.MadcowTestCase
import au.com.ps4impact.madcow.grass.GrassParser
import au.com.ps4impact.madcow.step.MadcowStep
import au.com.ps4impact.madcow.config.MadcowConfig
import au.com.ps4impact.madcow.util.ResourceFinder
import au.com.ps4impact.madcow.runner.webdriver.WebDriverStepRunner
import au.com.ps4impact.madcow.mappings.MadcowMappings

/**
 * Test for the ClickLink BladeRunner.
 */
class ClickLinkTest extends GroovyTestCase {

    MadcowTestCase testCase = new MadcowTestCase('ClickLinkTest', new MadcowConfig(), []);
    GrassParser grassParser = testCase.grassParser;
    def clickLink = new ClickLink();
    String testHtmlFilePath = ResourceFinder.locateFileOnClasspath(this.class.classLoader, 'test.html', 'html').absolutePath;

    protected verifyLinkExecution(GrassBlade blade, boolean shouldPass) {
        (testCase.stepRunner as WebDriverStepRunner).driver.get("file://${testHtmlFilePath}");
        MadcowStep step = new MadcowStep(testCase, blade, null);
        testCase.stepRunner.execute(step);
        assertEquals(shouldPass, step.result.passed());
    }

    void testLinkByHtmlId() {
        // defaults to html id
        GrassBlade blade = new GrassBlade('aLinkId.clickLink', grassParser);
        verifyLinkExecution(blade, true);

        // explicit htmlid
        MadcowMappings.addMapping(testCase, 'aLinkId', ['id': 'aLinkId']);
        blade = new GrassBlade('aLinkId.clickLink', grassParser);
        verifyLinkExecution(blade, true);
    }

    void testLinkByName() {
        MadcowMappings.addMapping(testCase, 'aLinkName', ['name': 'aLinkName']);
        GrassBlade blade = new GrassBlade('aLinkName.clickLink', grassParser);
        verifyLinkExecution(blade, true);
    }

    void testLinkByXPath() {
        MadcowMappings.addMapping(testCase, 'aLinkXPath', ['xpath': '//a[@id=\'aLinkId\']']);
        GrassBlade blade = new GrassBlade('aLinkXPath.clickLink', grassParser);
        verifyLinkExecution(blade, true);
    }

    void testLinkByText() {
        MadcowMappings.addMapping(testCase, 'aLinkText', ['text': 'A link']);
        GrassBlade blade = new GrassBlade('aLinkText.clickLink', grassParser);
        verifyLinkExecution(blade, true);
    }

    void testLinkDoesNotExist() {
        GrassBlade blade = new GrassBlade('aLinkThatDoesntExist.clickLink', grassParser);
        verifyLinkExecution(blade, false);
    }

    void testDefaultMappingSelector() {
        GrassBlade blade = new GrassBlade('testsite_menu_createAddress.clickLink', grassParser);
        assertTrue(clickLink.isValidBladeToExecute(blade));
    }

    void testMappingSelectorInvalidRequired() {
        try {
            GrassBlade blade = new GrassBlade('testsite_menu_createAddress.clickLink', grassParser);
            blade.mappingSelectorType = 'invalidOne';
            assertFalse(clickLink.isValidBladeToExecute(blade));
            fail('should always exception');
        } catch (e) {
            assertEquals(e.message, 'Unsupported mapping selector type \'invalidOne\'. Only [HTMLID, TEXT, NAME, XPATH] are supported.', );
        }
    }

    void testMappingSelectorRequired() {
        try {
            GrassBlade blade = new GrassBlade('testsite_menu_createAddress.clickLink', grassParser);
            blade.mappingSelectorType = null;
            assertFalse(clickLink.isValidBladeToExecute(blade));
            fail('should always exception');
        } catch (e) {
            assertEquals(e.message, 'Mapping selector must be supplied. One of [HTMLID, TEXT, NAME, XPATH] are supported.', );
        }
    }
}
