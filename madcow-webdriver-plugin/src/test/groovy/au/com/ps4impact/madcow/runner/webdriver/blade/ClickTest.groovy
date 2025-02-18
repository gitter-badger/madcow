/*
 * Copyright 2015 4impact, Brisbane, Australia
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package au.com.ps4impact.madcow.runner.webdriver.blade

import au.com.ps4impact.madcow.MadcowTestCase
import au.com.ps4impact.madcow.config.MadcowConfig
import au.com.ps4impact.madcow.grass.GrassBlade
import au.com.ps4impact.madcow.mappings.MadcowMappings
import au.com.ps4impact.madcow.runner.webdriver.WebDriverStepRunner
import au.com.ps4impact.madcow.step.MadcowStep
import au.com.ps4impact.madcow.util.ResourceFinder

/**
 * Test for the Click BladeRunner.
 *
 * @author Gavin Bunney
 */
class ClickTest extends GroovyTestCase {

    MadcowTestCase testCase;
    def click;
    String testHtmlFilePath;

    void setUp() {
        super.setUp();

        testCase = new MadcowTestCase('ClickTest', new MadcowConfig(), []);
        click = new Click();
        testHtmlFilePath = ResourceFinder.locateFileOnClasspath(this.class.classLoader, 'test.html', 'html').absolutePath;
    }

    protected verifyLinkExecution(GrassBlade blade, boolean shouldPass) {
        (testCase.stepRunner as WebDriverStepRunner).driver.get("file://${testHtmlFilePath}");
        MadcowStep step = new MadcowStep(testCase, blade, null);
        testCase.stepRunner.execute(step);
        assertEquals(shouldPass, step.result.passed());
    }

    void testClickByHtmlId() {
        // defaults to html id
        GrassBlade blade = new GrassBlade('aLinkId.click', testCase.grassParser);
        verifyLinkExecution(blade, true);

        // explicit htmlid
        MadcowMappings.addMapping(testCase, 'aLinkId', ['id': 'aLinkId']);
        blade = new GrassBlade('aLinkId.click', testCase.grassParser);
        verifyLinkExecution(blade, true);
    }

    void testClickByCss() {
        // defaults to html id
        GrassBlade blade = new GrassBlade('aLinkId.click = A link', testCase.grassParser);
        verifyLinkExecution(blade, true);

        // explicit htmlid
        MadcowMappings.addMapping(testCase, 'cssLinkName', ['css': '#aLinkId']);
        blade = new GrassBlade('cssLinkName.click', testCase.grassParser);
        verifyLinkExecution(blade, true);
    }

    void testClickByName() {
        MadcowMappings.addMapping(testCase, 'aLinkName', ['name': 'aLinkName']);
        GrassBlade blade = new GrassBlade('aLinkName.click', testCase.grassParser);
        verifyLinkExecution(blade, true);
    }

    void testClickByXPath() {
        MadcowMappings.addMapping(testCase, 'aLinkXPath', ['xpath': '//a[@id=\'aLinkId\']']);
        GrassBlade blade = new GrassBlade('aLinkXPath.click', testCase.grassParser);
        verifyLinkExecution(blade, true);
    }

    void testClickByText() {
        MadcowMappings.addMapping(testCase, 'aLinkText', ['text': 'A link']);
        GrassBlade blade = new GrassBlade('aLinkText.click', testCase.grassParser);
        verifyLinkExecution(blade, true);
    }

    void testClickDoesNotExist() {
        GrassBlade blade = new GrassBlade('aLinkThatDoesntExist.click', testCase.grassParser);
        verifyLinkExecution(blade, false);
    }

    void testDefaultMappingSelector() {
        GrassBlade blade = new GrassBlade('testsite_menu_createAddress.click', testCase.grassParser);
        assertTrue(click.isValidBladeToExecute(blade));
    }

    void testClickByHtmlIdOffScreen() {
        // defaults to html id
        GrassBlade blade = new GrassBlade('aLinkOffScreenId.click', testCase.grassParser);
        verifyLinkExecution(blade, true);

        // explicit htmlid
        MadcowMappings.addMapping(testCase, 'aLinkOffScreenId', ['id': 'aLinkOffScreenId']);
        blade = new GrassBlade('aLinkOffScreenId.click', testCase.grassParser);
        verifyLinkExecution(blade, true);
    }

    void testClickByEquationParameter() {
        GrassBlade blade = new GrassBlade('click = This is a link to google that should off the bottom of the viewable screen area on most resolutions', testCase.grassParser);
        verifyLinkExecution(blade, true);
    }

    void testMappingSelectorInvalidRequired() {
        try {
            GrassBlade blade = new GrassBlade('testsite_menu_createAddress.click', testCase.grassParser);
            blade.mappingSelectorType = 'invalidOne';
            assertFalse(click.isValidBladeToExecute(blade));
            fail('should always exception');
        } catch (e) {
            assertEquals('Unsupported mapping selector type \'invalidOne\'. Only [HTMLID, TEXT, NAME, XPATH, CSS] are supported.', e.message);
        }
    }
}
