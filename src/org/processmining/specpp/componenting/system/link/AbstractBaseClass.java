package org.processmining.specpp.componenting.system.link;

import org.processmining.specpp.componenting.system.ComponentCollection;
import org.processmining.specpp.componenting.system.FullComponentSystemUser;
import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.componenting.system.LocalComponentRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An abstract base class for any object that may want to make use of the component system.
 * It contains the component system connection objects ({@code ComponentRepository}s).
 * Additionally, it structures an initialization sequence for registered subcomponents, e.g. nested objects.
 * A main aspect of that is transitive local component system requirement resolution, i.e. if a nested subcomponent provides something that this class requires, it will be matched.
 */
public abstract class AbstractBaseClass implements FullComponentSystemUser {

    private final LocalComponentRepository lcr = new LocalComponentRepository();
    private final GlobalComponentRepository gcr = new GlobalComponentRepository();

    private final List<FullComponentSystemUser> subcomponents = new LinkedList<>();

    protected List<FullComponentSystemUser> getSubComponents() {
        return subcomponents;
    }

    @Override
    public void registerSubComponent(FullComponentSystemUser subComponent) {
        if (subComponent != null)
            subcomponents.add(subComponent);
    }

    /**
     * This may have some unintended consequences. Same thing for late registrations, i.e. after initial {@code SPECpp} component initialization.
     * If this class fulfils any requirements, e.g. data, evaluators, or provides observables or observes another component they will dangle.
     * Currently, all requirement containers are refillable, i.e. do not report {@code isFull() == true} ever.
     * In effect, the unregistered component's provisions will still be used until they are re-fulfilled. Consuming containers may forever retain a reference to the component.
     * At the very least, a localComponentSystem update should be performed through the {@code SPECpp} provided handle. It may fix <it>some</it> things.
     */
    @Override
    public void unregisterSubComponent(FullComponentSystemUser subComponent) {
        if (subComponent != null) subcomponents.remove(subComponent);
    }

    @Override
    public List<FullComponentSystemUser> collectTransitiveSubcomponents() {
        List<FullComponentSystemUser> collect = subcomponents.stream()
                                                             .flatMap(fcsu -> fcsu.collectTransitiveSubcomponents()
                                                                                  .stream())
                                                             .collect(Collectors.toList());
        collect.add(this);
        return collect;
    }

    /**
     * Structures the initialization of subcomponents and calls the hook for self initialization {@code initSelf()}.
     */
    @Override
    public final void init() {
        preSubComponentInit();
        for (FullComponentSystemUser subcomponent : subcomponents) {
            subcomponent.init();
        }
        postSubComponentInit();
    }

    protected void postSubComponentInit() {
        initSelf();
    }

    protected void preSubComponentInit() {
    }

    /**
     * Hook for subclasses to initialize themselves after the initial constructor call.
     * At the time this is called, all fulfillable local & global component system requests will be fulfilled.
     * Unless advanced interaction with the underlying systems is required, this is the only relevant hook for user-defined subclasses.
     */
    protected abstract void initSelf();


    @Override
    public ComponentCollection localComponentSystem() {
        return lcr;
    }

    @Override
    public ComponentCollection globalComponentSystem() {
        return gcr;
    }

    @Override
    public ComponentCollection getComponentCollection() {
        return gcr;
    }

}
