package org.travlyn.server.apiAccess;

import org.travlyn.server.util.Pair;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Utility class to build the param string for API url from Map.
 * @author Joshua Schulz
 * @since 1.0
 */
public class ParameterStringBuilder {
    public static String getParamString(Set<Pair<String,String>> parameters) {
        StringBuilder result = new StringBuilder();

        result.append("?");

        for (Iterator<Pair<String, String>> it = parameters.iterator(); it.hasNext(); ) {
            Map.Entry<String, String> entry = it.next();
            result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            if (entry.getValue() != null) {
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            }
            result.append("&");
        }
        String resultString = result.toString();
        resultString = resultString.replace("%3A",":").replace("%22","");
        if (resultString.length()>0) {
            //exclude last & if there were some parameters
            return resultString.substring(0, resultString.length() - 1);
        }else {
            //string empty -> nothing must be excluded
            return  resultString;
        }
    }
}
