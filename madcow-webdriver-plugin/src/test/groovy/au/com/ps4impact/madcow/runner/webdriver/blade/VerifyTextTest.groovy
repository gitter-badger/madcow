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
 * Test for the VerifyTextTest BladeRunner.
 *
 * @author Gavin Bunney
 */
class VerifyTextTest extends GroovyTestCase {

    MadcowTestCase testCase;
    def verifyText;
    String testHtmlFilePath;

    void setUp() {
        super.setUp();

        testCase = new MadcowTestCase('VerifyTextTest', new MadcowConfig(), []);
        verifyText = new VerifyText();
        testHtmlFilePath = ResourceFinder.locateFileOnClasspath(this.class.classLoader, 'test.html', 'html').absolutePath;
    }

    protected verifyTextContents(GrassBlade blade, boolean shouldPass) {
        (testCase.stepRunner as WebDriverStepRunner).driver.get("file://${testHtmlFilePath}");
        MadcowStep step = new MadcowStep(testCase, blade, null);
        testCase.stepRunner.execute(step);
        assertEquals(shouldPass, step.result.passed());
    }

    void testVerifyTextByHtmlId() {
        // defaults to html id
        GrassBlade blade = new GrassBlade('aLinkId.verifyText = A link', testCase.grassParser);
        verifyTextContents(blade, true);

        // explicit htmlid
        MadcowMappings.addMapping(testCase, 'aLinkId', ['id': 'aLinkId']);
        blade = new GrassBlade('aLinkId.verifyText = A link', testCase.grassParser);
        verifyTextContents(blade, true);
    }

    void testVerifyTextIncorrect() {
        GrassBlade blade = new GrassBlade('aLinkId.verifyText = A link that isn\'t a link is still a link', testCase.grassParser);
        verifyTextContents(blade, false);
    }

    void testVerifyTextByName() {
        MadcowMappings.addMapping(testCase, 'aLinkName', ['name': 'aLinkName']);
        GrassBlade blade = new GrassBlade('aLinkName.verifyText = A link', testCase.grassParser);
        verifyTextContents(blade, true);
    }

    void testVerifyTextByXPath() {
        MadcowMappings.addMapping(testCase, 'aLinkXPath', ['xpath': '//a[@id=\'aLinkId\']']);
        GrassBlade blade = new GrassBlade('aLinkXPath.verifyText = A link', testCase.grassParser);
        verifyTextContents(blade, true);
    }

    void testVerifyTextByText() {
        MadcowMappings.addMapping(testCase, 'aLinkText', ['text': 'A link']);
        GrassBlade blade = new GrassBlade('aLinkText.verifyText = A link', testCase.grassParser);
        verifyTextContents(blade, true);
    }

    void testVerifyTextEmpty() {
        GrassBlade blade = new GrassBlade('anEmptyParagraphId.verifyText = ', testCase.grassParser);
        verifyTextContents(blade, true);
    }

    void testVerifyTextOnPage() {
        GrassBlade blade = new GrassBlade('verifyText = Madcow WebDriver Runner Test HTML', testCase.grassParser);
        verifyTextContents(blade, true);
    }

    void testVerifyTextNotOnPage() {
        GrassBlade blade = new GrassBlade('verifyText = This won\'t be on the page', testCase.grassParser);
        verifyTextContents(blade, false);
    }
}
