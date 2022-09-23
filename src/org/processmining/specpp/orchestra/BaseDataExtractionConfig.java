package org.processmining.specpp.orchestra;

import org.apache.commons.collections4.BidiMap;
import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.data.DataSourceCollection;
import org.processmining.specpp.componenting.data.StaticDataSource;
import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.log.Log;
import org.processmining.specpp.datastructures.log.impls.LogEncoder;
import org.processmining.specpp.datastructures.log.impls.MultiEncodedLog;
import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.preprocessing.InputDataBundle;

public class BaseDataExtractionConfig implements DataExtractionConfig {

    @Override
    public void registerDataSources(GlobalComponentRepository cr, InputDataBundle bundle) {
        Log log = bundle.getLog();
        IntEncodings<Transition> transitionEncodings = bundle.getTransitionEncodings();
        BidiMap<Activity, Transition> mapping = bundle.getMapping();

        DataSourceCollection dc = cr.dataSources();
        dc.register(DataRequirements.RAW_LOG, StaticDataSource.of(log));
        LogEncoder.LogEncodingParameters lep = LogEncoder.LogEncodingParameters.getDefault();
        MultiEncodedLog multiEncodedLog = LogEncoder.multiEncodeLog(log, transitionEncodings, mapping, lep);
        dc.register(DataRequirements.ENC_LOG, StaticDataSource.of(multiEncodedLog));
        BitMask data = multiEncodedLog.variantIndices();
        dc.register(DataRequirements.CONSIDERED_VARIANTS, StaticDataSource.of(data));
        dc.register(DataRequirements.VARIANT_FREQUENCIES, StaticDataSource.of(multiEncodedLog.variantFrequencies()));
        dc.register(DataRequirements.ENC_ACT, StaticDataSource.of(multiEncodedLog.getEncodings()));
        dc.register(DataRequirements.ENC_TRANS, StaticDataSource.of(transitionEncodings));
        dc.register(DataRequirements.ACT_TRANS_MAPPING, StaticDataSource.of(mapping));
    }

}
