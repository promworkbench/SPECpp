package org.processmining.specpp.datastructures.log.impls;

import org.processmining.specpp.config.components.SimpleBuilder;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.log.Variant;

public interface VariantBuilder<V extends Variant> extends SimpleBuilder<V> {

    VariantBuilder<V> append(Activity activity);


}
