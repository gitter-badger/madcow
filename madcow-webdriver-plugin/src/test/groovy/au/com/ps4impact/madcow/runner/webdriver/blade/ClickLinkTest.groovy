/*
 * Copyright 2012 4impact, Brisbane, Australia
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

import au.com.ps4impact.madcow.grass.GrassBlade
import au.com.ps4impact.madcow.MadcowTestCase
import au.com.ps4impact.madcow.step.MadcowStep
import au.com.ps4impact.madcow.config.MadcowConfig
import au.com.ps4impact.madcow.util.ResourceFinder
import au.com.ps4impact.madcow.runner.webdriver.WebDriverStepRunner
import au.com.ps4impact.madcow.mappings.MadcowMappings

/**
 * Test for the ClickLink BladeRunner.
 *
 * @author Gavin Bunney
 */
class ClickLinkTest extends GroovyTestCase {

    MadcowTestCase testCase;
    def clickLink;
    String testHtmlFilePath;

    void setUp() {
        super.setUp();

        testCase = new MadcowTestCase('ClickLinkTest', new MadcowConfig(), []);
        clickLink = new ClickLink();
        testHtmlFilePath = ResourceFinder.locateFileOnClasspath(this.class.classLoader, 'test.html', 'html').absolutePath;
    }

    protected verifyLinkExecution(GrassBlade blade, boolean shouldPass) {
        (testCase.stepRunner as WebDriverStepRunner).driver.get("file://${testHtmlFilePath}");
        MadcowStep step = new MadcowStep(testCase, blade, null);
        testCase.stepRunner.execute(step);
        assertEquals(shouldPass, step.result.passed());
    }

    void testLinkByHtmlId() {
        // defaults to html id
        GrassBlade blade = new GrassBlade('aLinkId.clickLink', testCase.grassParser);
        verifyLinkExecution(blade, true);

        // explicit htmlid
        MadcowMappings.addMapping(testCase, 'aLinkId', ['id': 'aLinkId']);
        blade = new GrassBlade('aLinkId.clickLink', testCase.grassParser);
        verifyLinkExecution(blade, true);
    }

    void testLinkByCss() {
        // defaults to html id
        GrassBlade blade = new GrassBlade('aLinkId.clickLink = A link', testCase.grassParser);
        verifyLinkExecution(blade, true);

        // explicit htmlid
        MadcowMappings.addMapping(testCase, 'cssLinkName', ['css': '#aLinkId']);
        blade = new GrassBlade('cssLinkName.clickLink', testCase.grassParser);
        verifyLinkExecution(blade, true);
    }

    void testLinkByName() {
        MadcowMappings.addMapping(testCase, 'aLinkName', ['name': 'aLinkName']);
        GrassBlade blade = new GrassBlade('aLinkName.clickLink', testCase.grassParser);
        verifyLinkExecution(blade, true);
    }

    void testLinkByXPath() {
        MadcowMappings.addMapping(testCase, 'aLinkXPath', ['xpath': '//a[@id=\'aLinkId\']']);
        GrassBlade blade = new GrassBlade('aLinkXPath.clickLink', testCase.grassParser);
        verifyLinkExecution(blade, true);
    }

    void testLinkByText() {
        MadcowMappings.addMapping(testCase, 'aLinkText', ['text': 'A link']);
        GrassBlade blade = new GrassBlade('aLinkText.clickLink', testCase.grassParser);
        verifyLinkExecution(blade, true);
    }

    void testLinkDoesNotExist() {
        GrassBlade blade = new GrassBlade('aLinkThatDoesntExist.clickLink', testCase.grassParser);
        verifyLinkExecution(blade, false);
    }

    void testDefaultMappingSelector() {
        GrassBlade blade = new GrassBlade('testsite_menu_createAddress.clickLink', testCase.grassParser);
        assertTrue(clickLink.isValidBladeToExecute(blade));
    }

    void testLinkByHtmlIdOffScreen() {
        // defaults to html id
        GrassBlade blade = new GrassBlade('aLinkOffScreenId.clickLink', testCase.grassParser);
        verifyLinkExecution(blade, true);

        // explicit htmlid
        MadcowMappings.addMapping(testCase, 'aLinkOffScreenId', ['id': 'aLinkOffScreenId']);
        blade = new GrassBlade('aLinkOffScreenId.clickLink', testCase.grassParser);
        verifyLinkExecution(blade, true);
    }

    void testLinkByEquationParameter() {
        GrassBlade blade = new GrassBlade('clickLink = This is a link to google that should off the bottom of the viewable screen area on most resolutions', testCase.grassParser);
        verifyLinkExecution(blade, true);
    }

    void testMappingSelectorInvalidRequired() {
        try {
            GrassBlade blade = new GrassBlade('testsite_menu_createAddress.clickLink', testCase.grassParser);
            blade.mappingSelectorType = 'invalidOne';
            assertFalse(clickLink.isValidBladeToExecute(blade));
            fail('should always exception');
        } catch (e) {
            assertEquals('Unsupported mapping selector type \'invalidOne\'. Only [HTMLID, TEXT, NAME, XPATH, CSS] are supported.', e.message);
        }
    }
}
