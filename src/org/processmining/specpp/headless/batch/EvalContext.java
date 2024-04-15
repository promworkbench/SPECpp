package org.processmining.specpp.headless.batch;

import org.processmining.specpp.supervision.DirectCSVWriter;
import org.processmining.specpp.util.EvalUtils;

import java.time.Duration;

class EvalContext {

    Duration timeout;
    EvalUtils.EvaluationLogData evaluationLogData;
    DirectCSVWriter<SPECppEvaluationInfo> evalWriter;

}
