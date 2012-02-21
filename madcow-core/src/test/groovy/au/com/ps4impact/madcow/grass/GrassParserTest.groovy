package au.com.ps4impact.madcow.grass

import au.com.ps4impact.madcow.MadcowTestCase
import au.com.ps4impact.madcow.mock.MockMadcowConfig

/**
 * Test for the GrassParser.
 */
class GrassParserTest extends GroovyTestCase {

    public void testClearDataParameters() {
        
        GrassParser parser = new GrassParser(null, null);
        parser.setDataParameter("some", "param");
        assertEquals(parser.getDataParameter("some"), "param");
        
        parser.clearDataParameters();
        shouldFail { assertEquals(parser.getDataParameter("some"), "param"); };
    }

    public void testSetAndGetDataParameter() {

        GrassParser parser = new GrassParser(null, null);
        parser.setDataParameter("some", "param");
        assertEquals(parser.getDataParameter("some"), "param");

        parser.setDataParameter("some", "paramAgain");
        assertEquals(parser.getDataParameter("some"), "paramAgain");
    }

    public void testSetDataParameterNoName() {
        GrassParser parser = new GrassParser(null, null);
        try {
            parser.setDataParameter("", "paramAgain")
            fail("Should always exception");
        } catch (GrassParseException e) {
            assertEquals(e.message, 'Unable to set a data parameter without a name!');
        } catch (e) {
            fail("Unexpected exception");
        }

        try {
            parser.setDataParameter(null, "paramAgain")
            fail("Should always exception");
        } catch (GrassParseException e) {
            assertEquals(e.message, 'Unable to set a data parameter without a name!');
        } catch (e) {
            fail("Unexpected exception");
        }
    }
    
    public void testHasDataParameter() {
        GrassParser parser = new GrassParser(null, null);

        assertFalse(parser.hasDataParameter("some"));

        parser.setDataParameter("some", "param");
        assertTrue(parser.hasDataParameter("some"));

        assertFalse(parser.hasDataParameter(null));
        assertFalse(parser.hasDataParameter(''));
    }

    public void testSetAndGetDefaultDataParameter() {

        GrassParser parser = new GrassParser(null, null);
        parser.setDataParameter("some.default", "param");
        assertEquals("param", parser.getDataParameter("some"));
        assertEquals("param", parser.getDataParameter("some.default"));
    }

    public void testGrassParsingScript() {
        
        MadcowTestCase testCase = new MadcowTestCase('testGrassParsingScript', MockMadcowConfig.getMadcowConfig());
        ArrayList<String> grassScript = new ArrayList<String>();
        String grassScriptString = """
            @expectedValue = Australia

            # verify the expected country
            addressbook_search_country.verifyText = @expectedValue
            addressbook_search_country.verifySelectFieldOptions = ['One', 'Two']

            # perform a search and check the field options
            addressbook_search_button.clickLink
            addressbook_search_country.verifySelectFieldOptions = [options : ['@expectedValue', 'New Zealand']]
        """;
        grassScriptString.eachLine { line -> grassScript.add(line) }

        GrassParser parser = new GrassParser(testCase, grassScript);
        assertNotNull(parser);
        assertEquals("Verify number of steps, ignoring comments and blank lines", 5, testCase.steps.size());
    }

    public void testGrassParsingInvalidOp() {

        MadcowTestCase testCase = new MadcowTestCase('testGrassParsingInvalidOp', MockMadcowConfig.getMadcowConfig());
        ArrayList<String> grassScript = new ArrayList<String>();
        String grassScriptString = """
            @expectedValue = Australia

            # verify the expected country
            addressbook_search_country.verifyText = @expectedValue
            addressbook_search_country.notAValidOperation = this will fail!
        """;
        grassScriptString.eachLine { line -> grassScript.add(line) }

        try {
            GrassParser parser = new GrassParser(testCase, grassScript);
            fail('should always expection');
        } catch (GrassParseException gpe) {
            assertEquals("Unsupported operation 'notAValidOperation'", gpe.message);
        }
    }

    public void testGlobalDataParameters() {
        MadcowTestCase testCase = new MadcowTestCase('testGlobalDataParameters', MockMadcowConfig.getMadcowConfig());

        // check retrieval of global params
        GrassParser parser = new GrassParser(testCase, null);
        assertEquals("madcow.eval({ new Date().format('dd/MM/yyyy')})", parser.getDataParameter("@global.currentDate"));

        // check global are parsed in script
        parser.processScript(testCase, ['addressbook_currentDate.verifyText = @global.currentDate']);
        assertEquals("[testCase: testGlobalDataParameters, blade: operation: {verifyText} | selector: {null} | params: {${new Date().format('dd/MM/yyyy')}} | line: {addressbook_currentDate.verifyText = @global.currentDate} | mapping: {addressbook_currentDate}, parent: null, children: []]", testCase.steps.first().toString());
    }
}
