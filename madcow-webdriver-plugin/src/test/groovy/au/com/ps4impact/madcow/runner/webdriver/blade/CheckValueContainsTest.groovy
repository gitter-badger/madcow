/*
 * Copyright 2013 4impact, Brisbane, Australia
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
 * This class is the testcase for check value contains
 *
 * @author Tom Romano
 */
class CheckValueContainsTest extends GroovyTestCase {

    MadcowTestCase testCase;
    def checkValueContains;
    String testHtmlFilePath;

    void setUp() {
        super.setUp();

        testCase = new MadcowTestCase('CheckValueContainsTest', new MadcowConfig(), []);
        checkValueContains = new CheckValueContains();
        testHtmlFilePath = ResourceFinder.locateFileOnClasspath(this.class.classLoader, 'test.html', 'html').absolutePath;
    }

    protected verifyCheckValueContainsContents(GrassBlade blade, boolean shouldPass) {
        (testCase.stepRunner as WebDriverStepRunner).driver.get("file://${testHtmlFilePath}");
        MadcowStep step = new MadcowStep(testCase, blade, null);
        testCase.stepRunner.execute(step);
        assertEquals(shouldPass, step.result.passed());
    }

    void testCheckValueContainsByHtmlId() {
        // defaults to html id
        GrassBlade blade = new GrassBlade('aLinkId.checkValueContains = link', testCase.grassParser);
        verifyCheckValueContainsContents(blade, true);

        // explicit htmlid
        MadcowMappings.addMapping(testCase, 'aLinkId', ['id': 'aLinkId']);
        blade = new GrassBlade('aLinkId.checkValueContains = lin', testCase.grassParser);
        verifyCheckValueContainsContents(blade, true);
    }

    void testCheckValueContainsByCss() {
        // defaults to html id
        GrassBlade blade = new GrassBlade('aLinkId.checkValueContains = link', testCase.grassParser);
        verifyCheckValueContainsContents(blade, true);

        // explicit htmlid
        MadcowMappings.addMapping(testCase, 'cssLinkName', ['css': '#aLinkId']);
        blade = new GrassBlade('cssLinkName.checkValueContains = ink', testCase.grassParser);
        verifyCheckValueContainsContents(blade, true);
    }    

    void testCheckValueContainsIncorrect() {
        GrassBlade blade = new GrassBlade('aLinkId.checkValueContains = is still a link', testCase.grassParser);
        verifyCheckValueContainsContents(blade, false);
    }

    void testCheckValueContainsByName() {
        MadcowMappings.addMapping(testCase, 'aLinkName', ['name': 'aLinkName']);
        GrassBlade blade = new GrassBlade('aLinkName.checkValueContains = A li', testCase.grassParser);
        verifyCheckValueContainsContents(blade, true);
    }

    void testCheckValueContainsByXPath() {
        MadcowMappings.addMapping(testCase, 'aLinkXPath', ['xpath': '//a[@id=\'aLinkId\']']);
        GrassBlade blade = new GrassBlade('aLinkXPath.checkValueContains = nk', testCase.grassParser);
        verifyCheckValueContainsContents(blade, true);
    }

    void testCheckValueContainsByText() {
        MadcowMappings.addMapping(testCase, 'aLinkText', ['text': 'A link']);
        GrassBlade blade = new GrassBlade('aLinkText.checkValueContains = A l', testCase.grassParser);
        verifyCheckValueContainsContents(blade, true);
    }

    void testCheckValueContainsForTextArea() {
        GrassBlade blade = new GrassBlade('aTextAreaId.checkValueContains = contents', testCase.grassParser);
        verifyCheckValueContainsContents(blade, true);
    }

    void testCheckValueContainsEmpty() {
        GrassBlade blade = new GrassBlade('anEmptyParagraphId.checkValueContains = ', testCase.grassParser);
        verifyCheckValueContainsContents(blade, true);
    }

    void testMappingSelectorInvalidRequired() {
        try {
            GrassBlade blade = new GrassBlade('testsite_menu_createAddress.checkValueContains = Tennis', testCase.grassParser);
            blade.mappingSelectorType = 'invalidOne';
            assertFalse(checkValueContains.isValidBladeToExecute(blade));
            fail('should always exception');
        } catch (e) {
            assertEquals('Unsupported mapping selector type \'invalidOne\'. Only [HTMLID, NAME, XPATH, CSS] are supported.', e.message);
        }
    }

    void testMappingSelectorRequired() {
        try {
            GrassBlade blade = new GrassBlade('testsite_menu_createAddress.checkValueContains = Tennis', testCase.grassParser);
            blade.mappingSelectorType = null;
            assertFalse(checkValueContains.isValidBladeToExecute(blade));
            fail('should always exception');
        } catch (e) {
            assertEquals('Mapping selector must be supplied. One of [HTMLID, NAME, XPATH, CSS] are supported.', e.message);
        }
    }

    void testStatementNotSupported() {
        try {
            GrassBlade blade = new GrassBlade('testsite_menu_createAddress.checkValueContains', testCase.grassParser);
            assertFalse(checkValueContains.isValidBladeToExecute(blade));
            fail('should always exception');
        } catch (e) {
            assertEquals('Unsupported grass format. Only grass blades of type \'[EQUATION]\' are supported.', e.message);
        }
    }
}