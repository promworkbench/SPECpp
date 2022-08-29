package org.processmining.specpp.datastructures.util;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class RegexLabel extends Label {

    private final Predicate<String> predicate;

    public RegexLabel(String text) {
        super(text);
        Pattern pattern = Pattern.compile(text);
        predicate = pattern.asPredicate();
    }

    @Override
    public boolean lt(Label other) {
        return predicate.test(other.text);
    }

    @Override
    public boolean gt(Label other) {
        if (other instanceof RegexLabel) {
            return ((RegexLabel) other).predicate.test(text);
        } else return false;
    }

    @Override
    public String toString() {
        return "RegEx[" + super.toString() + "]";
    }
}
