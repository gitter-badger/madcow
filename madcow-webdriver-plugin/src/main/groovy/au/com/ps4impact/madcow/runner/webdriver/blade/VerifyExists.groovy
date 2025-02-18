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

import au.com.ps4impact.madcow.runner.webdriver.WebDriverBladeRunner
import au.com.ps4impact.madcow.runner.webdriver.WebDriverStepRunner
import au.com.ps4impact.madcow.step.MadcowStep
import org.openqa.selenium.WebElement
import au.com.ps4impact.madcow.step.MadcowStepResult
import au.com.ps4impact.madcow.grass.GrassBlade

/**
 * This class is the VerifyExists madcow operation which checks that an element exists on the page
 *
 * @author Tom Romano
 */
class VerifyExists extends WebDriverBladeRunner {

    public void execute(WebDriverStepRunner stepRunner, MadcowStep step) {

        try{

            WebElement element = findElement(stepRunner, step)

            if (element!=null){

                if (element.isDisplayed()){
                    step.result = MadcowStepResult.PASS();
                }else{
                    step.result = MadcowStepResult.FAIL("The element exits on the page but it is not currently displayed.");
                }

            }else{
                step.result = MadcowStepResult.FAIL("No match could be found for the expected element")
            }
        }catch (Exception e){
            step.result = MadcowStepResult.FAIL("No match could be found for the expected element")
        }

    }

    /**
     * Allow verifying the element is empty.
     */
    protected boolean allowEmptyParameterValue() {
        return true;
    }

    /**
     * Supported types of blades.
     */
    protected Collection<GrassBlade.GrassBladeType> getSupportedBladeTypes() {
        return [GrassBlade.GrassBladeType.STATEMENT];
    }

    /**
     * Types of supported selectors.
     */
    protected Collection<WebDriverBladeRunner.BLADE_MAPPING_SELECTOR_TYPE> getSupportedSelectorTypes() {
        return [WebDriverBladeRunner.BLADE_MAPPING_SELECTOR_TYPE.HTMLID,
                WebDriverBladeRunner.BLADE_MAPPING_SELECTOR_TYPE.TEXT,
                WebDriverBladeRunner.BLADE_MAPPING_SELECTOR_TYPE.NAME,
                WebDriverBladeRunner.BLADE_MAPPING_SELECTOR_TYPE.XPATH,
                WebDriverBladeRunner.BLADE_MAPPING_SELECTOR_TYPE.CSS];
    }
}
