package org.processmining.specpp.headless;

import org.processmining.specpp.base.impls.SPECpp;
import org.processmining.specpp.composition.BasePlaceComposition;
import org.processmining.specpp.config.ConfigPresets;
import org.processmining.specpp.config.SPECppConfigBundle;
import org.processmining.specpp.config.parameters.ExecutionParameters;
import org.processmining.specpp.datastructures.log.impls.MockInputBuilder;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.orchestra.ExecutionEnvironment;
import org.processmining.specpp.orchestra.SPECppOutputtingUtils;
import org.processmining.specpp.preprocessing.InputDataBundle;

public class HardcodedInput {

    public static void main(String[] args) {
        SPECppConfigBundle cfg = ConfigPresets.SUPERVISED_EXHAUSTIVE;
        InputDataBundle data = input_one();

        SPECpp<Place, BasePlaceComposition, CollectionOfPlaces, ProMPetrinetWrapper> specpp = null;
        try (ExecutionEnvironment ee = new ExecutionEnvironment()) {
            SPECppOutputtingUtils.preSetup(cfg, data, true);
            specpp = SPECpp.build(cfg, data);
            SPECppOutputtingUtils.postSetup(specpp, true);
            ee.execute(specpp, ExecutionParameters.noTimeouts());
            SPECppOutputtingUtils.duringExecution(specpp, true);
        } catch (InterruptedException ignored) {
        }
        SPECppOutputtingUtils.postExecution(specpp, true, true, true);
    }

    public static InputDataBundle input_one() {
        MockInputBuilder mb = new MockInputBuilder(true);
        mb.addActivities("A", "B", "C", "D", "E");
        mb.addVariant("A", "C", "C", "D");
        mb.addVariant("B", "C", "C", "C", "E");
        mb.addPresetOrdering(mb.artificialStartLabel(), "A", "B", "C", "D", "E");
        mb.addPostsetOrdering(mb.artificialEndLabel(), "E", "D", "C", "B", "A");
        return mb.createInputDataBundle();
    }

    public static InputDataBundle input_two() {
        MockInputBuilder mb = new MockInputBuilder(true);
        mb.addActivities("A", "B", "C", "D", "E");
        mb.addVariant("A", "C", "D");
        mb.addVariant("B", "C", "E");
        mb.addPresetOrdering(mb.artificialStartLabel(), "A", "B", "C", "D", "E");
        mb.addPostsetOrdering(mb.artificialEndLabel(), "E", "D", "C", "B", "A");
        return mb.createInputDataBundle();
    }


}
