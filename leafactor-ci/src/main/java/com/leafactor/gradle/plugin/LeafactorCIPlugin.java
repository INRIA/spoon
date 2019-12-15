package com.leafactor.gradle.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class LeafactorCIPlugin implements Plugin<Project> {
    static final String TASK_NAME = "refactor";

    @Override
    public void apply(Project project) {
        LauncherExtension launcherExtension = project.getExtensions().create("launcherExtension", LauncherExtension.class);
        project.getTasks().create(TASK_NAME, Refactor.class, refactor -> refactor.init(project, launcherExtension));
    }
}



