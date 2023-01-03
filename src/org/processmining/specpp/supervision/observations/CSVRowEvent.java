package org.processmining.specpp.supervision.observations;

public interface CSVRowEvent extends Event {

    String[] getColumnNames();

    String[] toRow();

}
