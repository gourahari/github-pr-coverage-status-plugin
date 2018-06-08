/*

    Copyright 2015-2016 Artem Stasiuk

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/
package com.github.terma.jenkins.githubprcoveragestatus;

import java.io.IOException;

import hudson.EnvVars;
import hudson.model.Run;
import hudson.model.TaskListener;

@SuppressWarnings("WeakerAccess")
class Utils {

    private static final String key_st = "${";
    private static final String key_end = "}";

    public static final String BUILD_URL_ENV_PROPERTY = "BUILD_URL";

    public static String getJenkinsUrlFromBuildUrl(String buildUrl) {
        final String keyword = "/job/";
        final int index = buildUrl.indexOf(keyword);
        if (index < 0) throw new IllegalArgumentException("Invalid build URL: " + buildUrl + "!");
        return buildUrl.substring(0, index);
    }

    public static String getBuildUrl(Run build, TaskListener listener) throws IOException, InterruptedException {
        return getBuildUrl(build, listener, BUILD_URL_ENV_PROPERTY);
    }

    public static String getBuildUrl(Run build, TaskListener listener, String key) throws IOException, InterruptedException {
        final EnvVars envVars = build.getEnvironment(listener);
        return String.valueOf(envVars.get(key));
    }

    public static String resolveURL(String url, Run build, TaskListener listener) throws IOException, InterruptedException {
        int startIndex = -1;
        int endIndex = 0;
        while (-1 != (startIndex = url.indexOf(key_st, endIndex))) {
            endIndex = url.indexOf(key_end, startIndex);
            String key = url.substring(startIndex + key_st.length(), endIndex);
            url = url.replace(key_st + key + key_end, getBuildUrl(build, listener, key));
        }
        return url;
    }
}
