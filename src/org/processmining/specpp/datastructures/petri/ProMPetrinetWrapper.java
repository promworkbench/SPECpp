package org.processmining.specpp.datastructures.petri;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.DirectedGraph;
import org.processmining.models.graphbased.directed.DirectedGraphEdge;
import org.processmining.models.graphbased.directed.DirectedGraphNode;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.ExpandableSubNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.specpp.base.Result;

import java.util.Collection;
import java.util.Set;

public class ProMPetrinetWrapper implements Result {

    private final Petrinet net;
    private final Marking initialMarking;
    private final Marking finalMarking;

    public Marking getInitialMarking() {
        return initialMarking;
    }

    public Marking getFinalMarking() {
        return finalMarking;
    }

    public ProMPetrinetWrapper(Petrinet net, Marking initialMarking, Marking finalMarking) {
        this.net = net;
        this.initialMarking = initialMarking;
        this.finalMarking = finalMarking;
    }

    public Petrinet getNet() {
        return net;
    }

    public String getLabel() {
        return net.getLabel();
    }

    public Collection<Transition> getTransitions() {
        return net.getTransitions();
    }

    public Place addPlace(String s) {
        return net.addPlace(s);
    }

    public Place addPlace(String s, ExpandableSubNet expandableSubNet) {
        return net.addPlace(s, expandableSubNet);
    }

    public Place removePlace(Place place) {
        return net.removePlace(place);
    }

    public Collection<Place> getPlaces() {
        return net.getPlaces();
    }

    public Arc addArc(Place place, Transition transition, int i) {
        return net.addArc(place, transition, i);
    }

    public Arc addArc(Place place, Transition transition) {
        return net.addArc(place, transition);
    }

    public Arc addArc(Transition transition, Place place, int i) {
        return net.addArc(transition, place, i);
    }

    public Arc addArc(Transition transition, Place place) {
        return net.addArc(transition, place);
    }

    public Arc addArc(Place place, Transition transition, int i, ExpandableSubNet expandableSubNet) {
        return net.addArc(place, transition, i, expandableSubNet);
    }

    public Arc addArc(Place place, Transition transition, ExpandableSubNet expandableSubNet) {
        return net.addArc(place, transition, expandableSubNet);
    }

    public Arc addArc(Transition transition, Place place, int i, ExpandableSubNet expandableSubNet) {
        return net.addArc(transition, place, i, expandableSubNet);
    }

    public Arc addArc(Transition transition, Place place, ExpandableSubNet expandableSubNet) {
        return net.addArc(transition, place, expandableSubNet);
    }

    public Arc removeArc(PetrinetNode petrinetNode, PetrinetNode petrinetNode1) {
        return net.removeArc(petrinetNode, petrinetNode1);
    }

    public Arc getArc(PetrinetNode petrinetNode, PetrinetNode petrinetNode1) {
        return net.getArc(petrinetNode, petrinetNode1);
    }

    public Set<PetrinetNode> getNodes() {
        return net.getNodes();
    }

    public Set<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> getEdges() {
        return net.getEdges();
    }

    public Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> getInEdges(DirectedGraphNode directedGraphNode) {
        return net.getInEdges(directedGraphNode);
    }

    public Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> getOutEdges(DirectedGraphNode directedGraphNode) {
        return net.getOutEdges(directedGraphNode);
    }

    public void removeEdge(DirectedGraphEdge<? extends DirectedGraphNode, ? extends DirectedGraphNode> directedGraphEdge) {
        net.removeEdge(directedGraphEdge);
    }

    public void removeNode(DirectedGraphNode directedGraphNode) {
        net.removeNode(directedGraphNode);
    }

    public DirectedGraph<?, ?> getGraph() {
        return net.getGraph();
    }

    @Override
    public String toString() {
        return net.toString();
    }

    @Override
    public boolean equals(Object o) {
        return net.equals(o);
    }

    @Override
    public int hashCode() {
        return net.hashCode();
    }

    public AttributeMap getAttributeMap() {
        return net.getAttributeMap();
    }

    public int compareTo(DirectedGraph<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> o) {
        return net.compareTo(o);
    }

}
