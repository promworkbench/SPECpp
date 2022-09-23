package org.processmining.specpp.prom.mvc;

import org.processmining.framework.plugin.PluginContext;

public abstract class AbstractStageController implements StageController {

    protected final SPECppController parentController;

    public AbstractStageController(SPECppController parentController) {
        this.parentController = parentController;
    }


    public SPECppController getParentController() {
        return parentController;
    }

    public PluginContext getContext() {
        return parentController.getPluginContext();
    }

}
