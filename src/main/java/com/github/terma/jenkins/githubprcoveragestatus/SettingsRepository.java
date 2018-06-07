package com.github.terma.jenkins.githubprcoveragestatus;

interface SettingsRepository {

    String getGitHubApiUrl();

    String getPersonalAccessToken();

    String getJenkinsUrl();

    String getIconClickUrl();

    int getYellowThreshold();

    int getGreenThreshold();

    boolean isPrivateJenkinsPublicGitHub();

    boolean isUseSonarForMasterCoverage();

    boolean isDisableSimpleCov();

    String getSonarUrl();

    String getSonarToken();
}
