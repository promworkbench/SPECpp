package org.processmining.specpp.componenting.data;

import org.processmining.specpp.base.Candidate;
import org.processmining.specpp.base.Result;
import org.processmining.specpp.componenting.evaluation.EvaluatorConfiguration;
import org.processmining.specpp.componenting.system.ComponentInitializer;
import org.processmining.specpp.componenting.system.link.CompositionComponent;
import org.processmining.specpp.config.*;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.log.Log;
import org.processmining.specpp.datastructures.log.impls.MultiEncodedLog;
import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.datastructures.tree.base.NodeProperties;
import org.processmining.specpp.datastructures.tree.base.NodeState;
import org.processmining.specpp.datastructures.tree.base.TreeNode;
import org.processmining.specpp.datastructures.tree.base.impls.LocalNodeWithExternalizedLogic;
import org.processmining.specpp.datastructures.tree.base.traits.LocallyExpandable;
import org.processmining.specpp.datastructures.vectorization.IntVector;
import org.processmining.specpp.util.JavaTypingUtils;

public class DataRequirements {


    public static final DataRequirement<BitMask> CONSIDERED_VARIANTS = dataSource("considered_variants", BitMask.class);

    public static final DataRequirement<IntVector> VARIANT_FREQUENCIES = dataSource("variant_frequencies", IntVector.class);
    public static final DataRequirement<Log> RAW_LOG = dataSource("raw_log", Log.class);

    public static final DataRequirement<MultiEncodedLog> ENC_LOG = dataSource("multi_enc_log", MultiEncodedLog.class);
    public static final DataRequirement<IntEncodings<Transition>> ENC_TRANS = dataSource("transition_encodings", JavaTypingUtils.castClass(IntEncodings.class));
    public static final ConfigurationRequirement<SupervisionConfiguration> SUPERVISOR_CONFIG = configuration("supervisor_config", SupervisionConfiguration.class);
    public static final ConfigurationRequirement<EvaluatorConfiguration> EVALUATOR_CONFIG = configuration("evaluator_config", EvaluatorConfiguration.class);

    public static <C extends Candidate, I extends CompositionComponent<C>, R extends Result> ConfigurationRequirement<ProposerComposerConfiguration<C, I, R>> proposerComposerConfiguration() {
        return configuration("proposer_composer_config", JavaTypingUtils.castClass(ProposerComposerConfiguration.class));
    }

    public static <R extends Result, F extends Result> ConfigurationRequirement<PostProcessingConfiguration<R, F>> postprocessingConfiguration() {
        return configuration("postprocessing_config", JavaTypingUtils.castClass(PostProcessingConfiguration.class));
    }

    public static <P extends NodeProperties, S extends NodeState, N extends LocalNodeWithExternalizedLogic<P, S, N>> ConfigurationRequirement<EfficientTreeConfiguration<P, S, N>> efficientTreeConfiguration() {
        return configuration("generating_tree_config", JavaTypingUtils.castClass(EfficientTreeConfiguration.class));
    }

    public static <N extends TreeNode & LocallyExpandable<N>> ConfigurationRequirement<TreeConfiguration<N>> treeConfiguration() {
        return configuration("tree_config", JavaTypingUtils.castClass(TreeConfiguration.class));
    }

    public static <T> DataRequirement<T> dataSource(String label, Class<T> type) {
        return new DataRequirement<>(label, type);
    }

    public static <T> FulfilledDataRequirement<T> dataSource(String label, Class<T> type, DataSource<T> dataSource) {
        return dataSource(label, type).fulfilWith(dataSource);
    }

    public static <T> FulfilledDataRequirement<T> dataSource(DataRequirement<T> requirement, DataSource<T> dataSource) {
        return requirement.fulfilWith(dataSource);
    }

    public static <F extends ComponentInitializer> ConfigurationRequirement<F> configuration(String label, Class<F> type) {
        return new ConfigurationRequirement<>(label, type);
    }

    public static <F extends ComponentInitializer> FulfilledDataRequirement<F> configuration(String label, Class<F> type, DataSource<F> factory) {
        return configuration(label, type).fulfilWith(factory);
    }

}
