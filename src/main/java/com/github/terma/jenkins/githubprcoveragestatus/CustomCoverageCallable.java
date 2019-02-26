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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;

import hudson.FilePath;
import hudson.Util;
import hudson.remoting.VirtualChannel;
import jenkins.MasterToSlaveFileCallable;

@SuppressWarnings("WeakerAccess")
final class CustomCoverageCallable extends MasterToSlaveFileCallable<Float> implements CoverageRepository {

    private final CoverageReportParser parser;

    CustomCoverageCallable(final CoverageReportParser parser) {
        this.parser = parser;
    }

    private static List<Float> getFloats(File ws, String path, CoverageReportParser parser) {
        FileSet fs = Util.createFileSet(ws, path);
        DirectoryScanner ds = fs.getDirectoryScanner();
        String[] files = ds.getIncludedFiles();
        List<Float> cov = new ArrayList<Float>();
        for (String file : files) cov.add(parser.get(new File(ds.getBasedir(), file).getAbsolutePath()));
        return cov;
    }

    @Override
    public float get(final FilePath workspace) throws IOException, InterruptedException {
        if (workspace == null) throw new IllegalArgumentException("Workspace should not be null!");
        return workspace.act(new CustomCoverageCallable(parser));
    }

    @Override
    public Float invoke(final File ws, final VirtualChannel channel) throws IOException {
        final List<Float> cov = new ArrayList<Float>();
        cov.addAll(getFloats(ws, "**/Cobertura.xml", parser));
        cov.addAll(getFloats(ws, "**/cobertura.xml", parser));
        cov.addAll(getFloats(ws, "**/cobertura-coverage.xml", parser));

        float s = 0;
        for (float v : cov) s += v;

        if (cov.isEmpty()) {
            return 0f;
        } else {
            return s / cov.size();
        }
    }

}
