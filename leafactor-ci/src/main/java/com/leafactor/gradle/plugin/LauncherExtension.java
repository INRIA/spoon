package com.leafactor.gradle.plugin;

import java.util.ArrayList;
import java.util.List;

public class LauncherExtension  {
    private boolean whiteListVariants = false;
    private List<String> variants = new ArrayList<>();
    private String sourceOutputDirectory;

    public String getSourceOutputDirectory() {
        return sourceOutputDirectory;
    }

    public void setSourceOutputDirectory(String sourceOutputDirectory) {
        this.sourceOutputDirectory = sourceOutputDirectory;
    }

    public boolean isWhiteListVariants() {
        return whiteListVariants;
    }

    public void setWhiteListVariants(boolean whiteListVariants) {
        this.whiteListVariants = whiteListVariants;
    }

    public List<String> getVariants() {
        return variants;
    }

    public void setVariants(List<String> variants) {
        this.variants = variants;
    }
}