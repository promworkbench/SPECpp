package org.processmining.specpp.config.parameters;

import org.processmining.specpp.base.impls.SPECpp;
import org.processmining.specpp.componenting.system.link.*;
import org.processmining.specpp.evaluation.fitness.AbstractBasicFitnessEvaluator;

import java.util.HashSet;
import java.util.Set;

public class SupervisionParameters implements Parameters {

    private final boolean useConsole, useFiles;
    private final Set<Class<?>> classesToInstrument;

    public SupervisionParameters(boolean useConsole, boolean useFiles) {
        this.useConsole = useConsole;
        this.useFiles = useFiles;
        classesToInstrument = new HashSet<>();
    }

    public SupervisionParameters(boolean useConsole, boolean useFiles, Set<Class<?>> classesToInstrument) {
        this.useConsole = useConsole;
        this.useFiles = useFiles;
        this.classesToInstrument = classesToInstrument;
    }


    public static SupervisionParameters instrumentNone(boolean useConsole, boolean useFiles) {
        return new SupervisionParameters(useConsole, useFiles);
    }

    public static SupervisionParameters instrumentAll(boolean useConsole, boolean useFiles) {
        HashSet<Class<?>> s = new HashSet<>();
        s.add(ProposerComponent.class);
        s.add(ComposerComponent.class);
        s.add(CompositionComponent.class);
        s.add(PostProcessorComponent.class);
        s.add(ExpansionStrategyComponent.class);
        s.add(EfficientTreeComponent.class);
        s.add(ChildGenerationLogicComponent.class);
        s.add(AbstractBasicFitnessEvaluator.class);
        s.add(SPECpp.class);
        return new SupervisionParameters(useConsole, useFiles, s);
    }

    public static SupervisionParameters getDefault() {
        return instrumentAll(true, true);
    }

    public Set<Class<?>> getClassesToInstrument() {
        return classesToInstrument;
    }

    public boolean shouldObjBeInstrumented(Object o) {
        return shouldClassBeInstrumented(o.getClass());
    }

    public boolean shouldClassBeInstrumented(Class<?> oClass) {
        for (Class<?> aClass : classesToInstrument) {
            if (aClass.isAssignableFrom(oClass)) {
                return true;
            }
        }
        return classesToInstrument.contains(oClass);
    }


    public boolean isUseConsole() {
        return useConsole;
    }

    public boolean isUseUseFiles() {
        return useFiles;
    }

    @Override
    public String toString() {
        return "SupervisionParameters{" + "useConsole=" + useConsole + ", useFiles=" + useFiles + ", classesToInstrument=" + classesToInstrument + '}';
    }
}
