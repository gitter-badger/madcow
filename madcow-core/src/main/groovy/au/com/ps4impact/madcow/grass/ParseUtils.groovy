package au.com.ps4impact.madcow.grass;

import org.apache.commons.lang3.StringUtils;

/**
 * Handy utilities for parsing things.
 *
 * @author Gavin Bunney
 */
class ParseUtils {

    /**
     * Unquote the string, by removing the first and last three characters.
     */
    static String unquote(String stringToUnquote) {
        if ((stringToUnquote.startsWith('"""')) && (stringToUnquote.endsWith('"""')))
            return StringUtils.substring(stringToUnquote, '"""'.length(), stringToUnquote.length() - '"""'.length())
        else if ((stringToUnquote.startsWith("'")) && (stringToUnquote.endsWith("'")))
            return StringUtils.substring(stringToUnquote, 1, stringToUnquote.length() - 1)
        else if ((stringToUnquote.startsWith('"')) && (stringToUnquote.endsWith('"')))
            return StringUtils.substring(stringToUnquote, 1, stringToUnquote.length() - 1)
        else
            return stringToUnquote
    }

    /**
     * Attempts to escape the $ signs
     * @param originalString the string with the $ signs in it
     * @return the originalString with the $ signs escaped
     */
    static String unDollarify(String originalString){
        //if finds dollar sign
        if (originalString.contains('$'))
        {
            //treat this as a replacement GString-age
            originalString = originalString.replaceAll($/\$$/$,'\\\\\\$');
        }
        return originalString;
    }

    /**
     * Calls the groovy Eval.me for the string.<br/>
     * Returns an object of the results; if it is a Map/List, then it is just returned.<br/>
     * If it is a String, then it is returned as a String.<br/>
     */
    static def evalMe(String stringToEval) {
        stringToEval = unquote(stringToEval);
        try {
            def evaledValue = Eval.me(unDollarify(stringToEval));
            switch (evaledValue) {
                case Map:
                    return evaledValue as Map;
                case List:
                    return evaledValue as List;
                default:
                    break
            }
        } catch (ignored) {
            // ignored, must just be a normal string
        }
        return stringToEval
    }

    /**
     * Escape all the single quotes in the given string.
     */
    static String escapeSingleQuotes(String str) {
        return (str.contains("'") ? '"' + str + '"' : "'" + str + "'")
    }

}
