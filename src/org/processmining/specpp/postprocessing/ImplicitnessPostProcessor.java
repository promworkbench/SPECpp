package org.processmining.specpp.postprocessing;

import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.specpp.componenting.system.ComponentSystemAwareBuilder;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.evaluation.implicitness.ImplicitnessTestingParameters;

public abstract class ImplicitnessPostProcessor implements PetriNetPostProcessor {

    protected final BitMask consideredVariants;
    protected final ImplicitnessTestingParameters parameters;

    protected ImplicitnessPostProcessor(BitMask consideredVariants, ImplicitnessTestingParameters parameters) {
        this.consideredVariants = consideredVariants;
        this.parameters = parameters;
    }

    public static abstract class Builder extends ComponentSystemAwareBuilder<ImplicitnessPostProcessor> {

        protected final DelegatingDataSource<ImplicitnessTestingParameters> parametersSource = new DelegatingDataSource<>();
        protected final DelegatingDataSource<BitMask> consideredVariantsSource = new DelegatingDataSource<>();

        public Builder() {
            globalComponentSystem().require(ParameterRequirements.IMPLICITNESS_TESTING, parametersSource)
                                   .require(DataRequirements.CONSIDERED_VARIANTS, consideredVariantsSource);
        }

    }

}
