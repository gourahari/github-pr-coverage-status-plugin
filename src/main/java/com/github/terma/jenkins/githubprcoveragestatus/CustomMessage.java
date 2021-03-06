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

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;

@SuppressWarnings("WeakerAccess")
class CustomMessage {

    //see http://shields.io/ for reference
    private static final String BADGE_TEMPLATE = "https://img.shields.io/badge/coverage-%s-%s.svg";

    private static final String COLOR_RED = "red";
    private static final String COLOR_YELLOW = "yellow";
    private static final String COLOR_GREEN = "brightgreen";

    private final float lineCoverage;
    private final float branchCoverage;

    public CustomMessage(float lineCoverage, float branchCoverage) {
        this.lineCoverage = Percent.roundFourAfterDigit(lineCoverage);
        this.branchCoverage = Percent.roundFourAfterDigit(branchCoverage);
    }

    public String forConsole() {
        return String.format("Line coverage: %s, Branch coverage %s",
                Percent.toWholeNoSignString(lineCoverage),
                Percent.toWholeNoSignString(branchCoverage));
    }

    public String forComment(
            final String buildUrl, final String jenkinsUrl,
            final int yellowThreshold, final int greenThreshold,
            final boolean useShieldsIo) {
        final String icon = forIcon();
        if (useShieldsIo) {
            return "[![" + icon + "](" + shieldIoUrl(icon, yellowThreshold, greenThreshold) + ")](" + buildUrl + ")";
        } else {
            return "[![" + icon + "](" + jenkinsUrl + "/coverage-status-icon/" +
                    "?lineCoverage=" + lineCoverage +
                    "&branchCoverage=" + branchCoverage +
                    ")](" + buildUrl + ")";
        }
    }

    private String shieldIoUrl(String icon, final int yellowThreshold, final int greenThreshold) {
        final String color = getColor(yellowThreshold, greenThreshold);
        // dash should be encoded as two dash
        icon = icon.replace("-", "--");
        try {
            return String.format(BADGE_TEMPLATE, URIUtil.encodePath(icon), color);
        } catch (URIException e) {
            throw new RuntimeException(e);
        }
    }

    private String getColor(int yellowThreshold, int greenThreshold) {
        String color = COLOR_GREEN;
        final int coveragePercent = Percent.of(lineCoverage);
        if (coveragePercent < yellowThreshold) {
            color = COLOR_RED;
        } else if (coveragePercent < greenThreshold) {
            color = COLOR_YELLOW;
        }
        return color;
    }

    /**
     * Example: Line: 92%, Branch: 76%
     */
    public String forIcon() {
        return String.format("line coverage: %s",
                Percent.toWholeNoSignString(lineCoverage));
    }
}
