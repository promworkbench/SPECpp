package org.processmining.specpp.util;

import org.processmining.specpp.datastructures.encoding.BitEncodedSet;
import org.processmining.specpp.datastructures.encoding.IntEncoding;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.Transition;

public class Placemaker {
    private final IntEncoding<Transition> preEnc, postEnc;

    public Placemaker(IntEncodings<Transition> encodings) {
        this.preEnc = encodings.pre();
        this.postEnc = encodings.post();
    }

    public class InProgress {

        final BitEncodedSet<Transition> preset;
        final BitEncodedSet<Transition> postset;

        public InProgress() {
            this.preset = BitEncodedSet.empty(preEnc);
            this.postset = BitEncodedSet.empty(postEnc);
        }

        public InProgress preset(Transition... pre) {
            for (Transition t : pre) {
                preset.add(t);
            }
            return this;
        }

        public InProgress postset(Transition... post) {
            for (Transition t : post) {
                postset.add(t);
            }
            return this;
        }

        public Place get() {
            return new Place(preset, postset);
        }

    }

    public InProgress start() {
        return new InProgress();
    }

    public InProgress preset(Transition... pre) {
        return start().preset(pre);
    }

}
