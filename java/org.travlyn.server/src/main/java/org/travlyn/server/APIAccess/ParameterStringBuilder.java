package org.travlyn.server.APIAccess;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ParameterStringBuilder {
    public static String getParamString(Map<String, String> parameters) {
        StringBuilder result = new StringBuilder();

        result.append("?");

        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            result.append("&");
        }

        String resultString = result.toString();
        if (resultString.length()>0) {
            //exclude last & if there were some parameters
            return resultString.substring(0, resultString.length() - 1);
        }else {
            //string empty -> nothing must be excluded
            return  resultString;
        }
    }
}
