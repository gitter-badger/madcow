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

import au.com.ps4impact.madcow.MadcowTestCase
import au.com.ps4impact.madcow.config.MadcowConfig
import au.com.ps4impact.madcow.grass.GrassBlade
import au.com.ps4impact.madcow.mappings.MadcowMappings
import au.com.ps4impact.madcow.runner.webdriver.WebDriverStepRunner
import au.com.ps4impact.madcow.step.MadcowStep
import au.com.ps4impact.madcow.util.ResourceFinder

/**
 * Test for the CheckValue BladeRunner.
 *
 * @author Gavin Bunney
 */
class CheckValueTest extends GroovyTestCase {

    MadcowTestCase testCase;
    def checkValue;
    String testHtmlFilePath;

    void setUp() {
        super.setUp();

        testCase = new MadcowTestCase('CheckValueTest', new MadcowConfig(), []);
        checkValue = new CheckValue();
        testHtmlFilePath = ResourceFinder.locateFileOnClasspath(this.class.classLoader, 'test.html', 'html').absolutePath;
    }

    protected verifyCheckValueContents(GrassBlade blade, boolean shouldPass) {
        (testCase.stepRunner as WebDriverStepRunner).driver.get("file://${testHtmlFilePath}");
        MadcowStep step = new MadcowStep(testCase, blade, null);
        testCase.stepRunner.execute(step);
        assertEquals(shouldPass, step.result.passed());
    }

    void testCheckValueByHtmlId() {
        // defaults to html id
        GrassBlade blade = new GrassBlade('aLinkId.checkValue = A link', testCase.grassParser);
        verifyCheckValueContents(blade, true);

        // explicit htmlid
        MadcowMappings.addMapping(testCase, 'aLinkId', ['id': 'aLinkId']);
        blade = new GrassBlade('aLinkId.checkValue = A link', testCase.grassParser);
        verifyCheckValueContents(blade, true);
    }

    void testCheckValueByCss() {
        // defaults to html id
        GrassBlade blade = new GrassBlade('aLinkId.checkValue = A link', testCase.grassParser);
        verifyCheckValueContents(blade, true);

        // explicit htmlid
        MadcowMappings.addMapping(testCase, 'cssLinkName', ['css': '#aLinkId']);
        blade = new GrassBlade('cssLinkName.checkValue = A link', testCase.grassParser);
        verifyCheckValueContents(blade, true);
    }

    void testCheckValueIncorrect() {
        GrassBlade blade = new GrassBlade('aLinkId.checkValue = A link that isn\'t a link is still a link', testCase.grassParser);
        verifyCheckValueContents(blade, false);
    }

    void testCheckValueByName() {
        MadcowMappings.addMapping(testCase, 'aLinkName', ['name': 'aLinkName']);
        GrassBlade blade = new GrassBlade('aLinkName.checkValue = A link', testCase.grassParser);
        verifyCheckValueContents(blade, true);
    }

    void testCheckValueByXPath() {
        MadcowMappings.addMapping(testCase, 'aLinkXPath', ['xpath': '//a[@id=\'aLinkId\']']);
        GrassBlade blade = new GrassBlade('aLinkXPath.checkValue = A link', testCase.grassParser);
        verifyCheckValueContents(blade, true);
    }

    void testCheckValueByText() {
        MadcowMappings.addMapping(testCase, 'aLinkText', ['text': 'A link']);
        GrassBlade blade = new GrassBlade('aLinkText.checkValue = A link', testCase.grassParser);
        verifyCheckValueContents(blade, true);
    }

    void testCheckValueForTextArea() {
        GrassBlade blade = new GrassBlade('aTextAreaId.checkValue = Text area contents', testCase.grassParser);
        verifyCheckValueContents(blade, true);
    }

    void testCheckValueEmpty() {
        GrassBlade blade = new GrassBlade('anEmptyParagraphId.checkValue = ', testCase.grassParser);
        verifyCheckValueContents(blade, true);
    }

    void testMappingSelectorInvalidRequired() {
        try {
            GrassBlade blade = new GrassBlade('testsite_menu_createAddress.checkValue = Tennis', testCase.grassParser);
            blade.mappingSelectorType = 'invalidOne';
            assertFalse(checkValue.isValidBladeToExecute(blade));
            fail('should always exception');
        } catch (e) {
            assertEquals('Unsupported mapping selector type \'invalidOne\'. Only [HTMLID, NAME, XPATH, CSS] are supported.', e.message);
        }
    }

    void testMappingSelectorRequired() {
        try {
            GrassBlade blade = new GrassBlade('testsite_menu_createAddress.checkValue = Tennis', testCase.grassParser);
            blade.mappingSelectorType = null;
            assertFalse(checkValue.isValidBladeToExecute(blade));
            fail('should always exception');
        } catch (e) {
            assertEquals('Mapping selector must be supplied. One of [HTMLID, NAME, XPATH, CSS] are supported.', e.message);
        }
    }

    void testStatementNotSupported() {
        try {
            GrassBlade blade = new GrassBlade('testsite_menu_createAddress.checkValue', testCase.grassParser);
            assertFalse(checkValue.isValidBladeToExecute(blade));
            fail('should always exception');
        } catch (e) {
            assertEquals('Unsupported grass format. Only grass blades of type \'[EQUATION]\' are supported.', e.message);
        }
    }
}
