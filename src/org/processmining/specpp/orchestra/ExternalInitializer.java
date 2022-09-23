package org.processmining.specpp.orchestra;

import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.specpp.componenting.system.link.AbstractBaseClass;
import org.processmining.specpp.config.ExternalInitializationParameters;
import org.processmining.specpp.datastructures.encoding.BitEncodedSet;
import org.processmining.specpp.datastructures.encoding.IntEncoding;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.datastructures.tree.constraints.AddWiredPlace;
import org.processmining.specpp.datastructures.tree.constraints.WiringConstraint;
import org.processmining.specpp.supervision.piping.Observable;
import org.processmining.specpp.supervision.piping.PipeWorks;

public class ExternalInitializer extends AbstractBaseClass {

    private final Observable<WiringConstraint> externalWiringConstraintsPublisher = PipeWorks.eventSupervision();
    private final DelegatingDataSource<ExternalInitializationParameters> parametersSource = new DelegatingDataSource<>();
    private final DelegatingDataSource<IntEncodings<Transition>> transitionEncodingsSource = new DelegatingDataSource<>();

    public ExternalInitializer() {
        globalComponentSystem().require(ParameterRequirements.EXTERNAL_INITIALIZATION, parametersSource)
                               .require(DataRequirements.ENC_TRANS, transitionEncodingsSource)
                               .provide(SupervisionRequirements.observable("external.constraints.wiring", WiringConstraint.class, externalWiringConstraintsPublisher));
    }

    @Override
    public void initSelf() {
        if (parametersSource.isSet()) {
            ExternalInitializationParameters initializationParameters = parametersSource.getData();
            if (initializationParameters.isInitiallyWireSelfLoops()) {
                IntEncodings<Transition> transitionIntEncodings = transitionEncodingsSource.getData();
                IntEncoding<Transition> presetEncoding = transitionIntEncodings.getPresetEncoding();
                IntEncoding<Transition> postsetEncoding = transitionIntEncodings.getPostsetEncoding();
                for (Transition t : transitionIntEncodings.domainIntersection()) {
                    BitEncodedSet<Transition> preset = BitEncodedSet.empty(presetEncoding);
                    BitEncodedSet<Transition> postset = BitEncodedSet.empty(postsetEncoding);
                    preset.add(t);
                    postset.add(t);
                    externalWiringConstraintsPublisher.publish(new AddWiredPlace(Place.of(preset, postset)));
                }
            }
        }
    }
}
