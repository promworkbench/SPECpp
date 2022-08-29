package org.processmining.specpp.datastructures.log.impls;

public class StartEndInsertingVariantBuilderImpl extends VariantBuilderImpl {

    public StartEndInsertingVariantBuilderImpl() {
        append(Factory.ARTIFICIAL_START);
    }

    public VariantImpl build() {
        append(Factory.ARTIFICIAL_END);
        return super.build();
    }

}
