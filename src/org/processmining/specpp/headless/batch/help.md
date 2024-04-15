# Command Line Batch Execution & Evaluation Tool

Provides a command line interface for batch execution and optionally evaluation of parameter variations.

### CLI arguments

* `-l`/`-log` path to the input log in .xes format
* `-c`/`-config` path to the configuration file [(.json, view format)](#Configuration-file-format)
* `-v`/`-variations` path to the parameter variations file [(.json, view format)](#Parameter-variations-file-format)
* `-r`/`-range` (optional) restrict execution a range of configuration variation indices format is `[low, high)`
  with `low/high = integer | _`
* `-o`/`-output` path to the desired output directory
* `-ev`/`-evaluate` whether to automatically compute quality metrics on resulting models
* `-m`/`-monitor` whether to save the output of data monitors to files
* `-viz`/`-visualize` whether to visualize and thus layout the resulting petri nets
* `-dry`/`-dry_run` to test the configuration, no executions will be launched
* `-lb`/`-label` (optional) label of this batch execution (a sub folder is created in the output directory)
* `-nt`/`-num_threads` (optional) targeted parallelism level
* `-pec_time`/`-pec_timeout` (optional) timeout in seconds for PEC-cycling (graceful cancellation, i.e., the
  intermediate result is used for post-processing)
* `-pp_time`/`-pp_timeout` (optional) timeout in seconds for post-processing (hard cancellation, i.e., no result is
  produced)
* `-total_time`/`-total_timeout` (optional) timeout in seconds for an entire run (hard cancellation, i.e., no result is
  produced)
* `-ev_time`/`-evaluation_timeout` (optional) timeout in seconds for evaluation computations if requested

### Configuration file format

The algorithm configuration is specified in a (hopefully) human-readable and concise _json_ format.

The format essentially mirrors the code-defined configuration. Each class in the algorithm structure is manually
specified.
To keep this slightly shorter and not having to fully qualify classes with their package hierarchy, we defined some
canonical packages for the specific components.
If a given class name does not appear to be fully qualified, it is completed as if belonging to the canonical package
given in brackets below.
See below for an example configuration.

```
{
  "Input Processing": {
    "eventClassifier": (org.deckfour.xes.classification)"XEventNameClassifier",
    "addStartEndTransitions": false,
    "activityOrderingStrategy": (org.processmining.specpp.preprocessing.orderings)"RandomOrdering"
  },
  "Components": {
    "Supervisors": (org.processmining.specpp.supervision.supervisors) ["PerformanceSupervisor"],
    "Evaluators": (org.processmining.specpp.evaluation) [
      "fitness.AbsolutelyNoFrillsFitnessEvaluator$Builder",
      "markings.LogHistoryMaker"
    ],
    "Proposing": {
      "Proposer": (org.processmining.specpp.proposal)"ConstrainablePlaceProposer$Builder",
      "Tree Structure": {
        "Tree": (org.processmining.specpp.datastructures.tree.base.impl)"EnumeratingTree",
        "Expansion Strategy": (org.processmining.specpp.datastructures.tree)"heuristic.EventingHeuristicTreeExpansion",
        "Node Generation Logic": (org.processmining.specpp.datastructures.tree.nodegen)"MonotonousPlaceGenerationLogic$Builder",
        "Heuristic": (org.processmining.specpp.evaluation.heuristics)"EventuallyFollowsTreeHeuristic$Builder"
      }
    },
    "Compositing": {
      "Composition": (org.processmining.specpp.composition)"StatefulPlaceComposition",
      "Composer": (org.processmining.specpp.composition.composers) [
        "PlaceFitnessFilter",
        "PlaceAccepter"
      ]
    },
    "Post Processors": (org.processmining.specpp.postprocessing)[
      "SelfLoopPlaceMerger",
      "ProMConverter"
    ]
  },
  "Parameters": {
    "base": (org.processmining.specpp.config.presets)"BaseParameters",
    "extensions": [
      {
        "label": "placegenerator.parameters",
        "type": (org.processmining.specpp.config.parameters)"PlaceGeneratorParameters",
        "args": {
          "maxTreeDepth": 11
        }
      },
      {
        "label": "supervision.parameters",
        "type": "SupervisionParameters",
        "args": {
          "useConsole": true,
          "useFiles": false,
          "classesToInstrument": []
        }
      }
    ]
  }
}
```

Before we go into detail on the structure and various blocks contained within it, we briefly digress into a technical
aspect of this which is mostly relevant for developers.

----

To be more precise with "specifying an implementation", we actually intend to specify no-argument suppliers of fixed- to
no-argument (though parameterizable via the framework's requirement-management-system) builders of the specific
implementing class.
This pattern is used to allow one configuration object to be used for multiple instantiations without side effects and
to simplify the dynamic reflective class initialization.
We support two ways of going this.

1. directly naming the implementation class
2. naming a class that conforms to the function interface of `Supplier<'implementation we want to specify'>`

When the implementation class can be meaningfully instantiated by a no-argument constructor, we can
simply use (1). In the background, we assign a supplier to a `newInstance()` reflection call to the given type.
A constructed class can still use the requirement-management-system to request parameter objects or offer observables,
etc.
For classes which come from outside the framework, i.e. don't use the aforementioned system for parameters or required
sub-objects, or where a stricter separation of computation logic and make-it-work-framework is intended, we may specify
a specific builder class. This is option (2).
Some components implemented in the framework use this pattern with internal classes.
See `ConstrainablePlaceProposer$Builder` as an example. `$Builder` specifies the inner class `Builder`
of `ConstrainablePlaceProposer`.
The builder class has a no-argument constructor in which it requests another component configuration which it uses in
its `get()` equivalent to construct a `ConstrainablePlaceProposer` instance.

----

The structure is divided into input processing config, algorithm component config and algorithm parameter config. We'll
name here the programmatic equivalents for the benefit of developers.

#### Input Processing

+ **PreProcessingParameters** consisting of an `XEventClassifier` impl. and `addStartEndTransitions=true|false`.
+ **DataExtractionParameters** consisting of an `ActivityOrderingStrategy` impl.

#### Components

+ **Supervisors** list of `Supervisor` impls. A `BaseSupervisor` (provides console and file output to succeeding
  supervisors) and a `TerminalSupervisor` (handles flushing the asynchronous event-pipe-network at shutdown) are
  automatically inserted if the list is non-empty.
+ **Evaluators** list of `ProvidesEvaluators` impls. which register an evaluator
  service (`Evaluator<Evaluable, Evaluation>`) in the requirement-management-system. Standard implementations are
  token-based fitness evaluators, or the marking history computer. Evaluators are used for heuristics, or generally
  separable computations that can be made available to all algorithm components.
+ **Proposing Block**
    + **Proposer** `ProposerComponent<Place>` impl. EfficientTree-based proposers use the efficient tree configured in
      the following as their candidate source.
    + **Tree Structure Block**
        + **Tree** `EfficientTreeComponent<PlaceNode>` impl. Holds the necessary tree nodes of the candidate generating
          tree. Uses an expansion strategy to decide which nodes to expand next and the child generation logic to
          actually compute those children with local parent node information only.
        + **Expansion Strategy** `ExpansionStrategyComponent<PlaceNode>` impl. Decides the next node to expand based on
          all previously generated nodes. Can be a `HeuristicTreeExpansion` with an internal priority queue.
        + **Node Generation Logic** `ChildGenerationLogicComponent<Place, PlaceState, PlaceNode>` impl. Responsible for
          computing `PlaceNode` children of a given node given its `PlaceState` (stores already generated children) and
          under all previously observed candidate constraints. This component listens to candidate constraint events and
          manages an internal representation (e.g. WiringMatrix) to guarantee that any subsequently generated nodes meet
          those constraints.
        + **Heuristic** `HeuristicStrategy<PlaceNode, TreeNodeScore>` impl. (only required if the expansion strategy is
          of type `HeuristicTreeExpansion`). Externalization of the logic for computing tree node scores used in the
          expansion strategy.
+ **Compositing Block**
    + **Composition** `AdvancedComposition<Place>` impl. (a `CompositionComponent<Place>` which also supports removal (
      acceptance revocation) of candidates) or list of nested compositions with a terminal element at the end. Nested
      compositions forward their candidate storage to their child composition. Regular (terminal) compositions directly
      manage a collection of candidates.
    + **Composer** `ComposerComponent<Place, AdvancedComposition<Place>, CollectionOfPlaces>` impl. or list of nested
      composers with a terminal composer at the end. Nested composers forward their accepted candidates into their child
      composer. Regular (terminal) composers manage a composition object.
+ **Post Processors** list of `PostProcessor<S, T>`s (transformers of type S into T) where the first one
  has `S = CollectionOfPlaces` and the last one `T = ProMPetrinetWrapper` and all in-between types are compatible.

#### Parameters

+ **Parameters Block**
    + **base** (optional) a `ProvidesParameters` impl. that can act as a preset to be modified in the following block.
    + **ext** list of parameter requirements specified as objects. They override previously defined specifications of
      the preset.
        + "label" is used as an identifier in combination with "type" (impl. of `Parameters`) to specify the fulfilled
          requirement. The requirement-management-system connects the defined instantiation of the parameter class to
          all components that request the given type and type (or a subtype of it).
        + "args" are passed to a gson json deserializer to construct the parameter instance. Omitted constructor
          arguments are set to null or the respective primitive defaults. Complex objects as arguments that are not gson
          deserializable by default are not supported. Custom enums work.

### Parameter variations file format

Parameter variations are also encoded in a compact _json_ representation. The structure is a list of lists of _parameter
variation specifications_.
A parameter variation specification is just like a regular parameter specification as described above but with
constructor arguments specified as lists of values to iterate over. This representation was chosen to reduce duplication
and make the configuration as concise as possible.
Each label & type combo may appear only once.
An example is the following which specifies that `TauFitnessThresholds` should be instantiated with variations of the (
single) `tau` field. This block thus indirectly specifies _three_ different variations.

```
{
  "label": "fitness.tau_threshold",
  "type": "TauFitnessThresholds",
  "args": {
    "tau": [
      0.3,
      0.6,
      1
    ]
  }
}
```

For parameter classes with multiple fields/constructor arguments, there is the option to either vary the arguments
together or independently. See:

```
{
  "label": "placegenerator.parameters",
  "type": "PlaceGeneratorParameters",
  "vary args independently": false,
  "args": {
    "maxTreeDepth": [
      5,
      5,
      5,
      8,
      8,
      8
    ],
    "acceptWiringConstraints": [
      false,
      false,
      false,
      true,
      true,
      true
    ]
  }
```

If the tag `vary args independently` is set to `false` (or absent), the field variation lists have to be the same
length. Instances use a value from each list at the same index. The example thus specifies _six_ variations of which the
first and last three respectively are equal.
If it is set to `true`, all combinations of values are generated. Take the following example.

```
{
  "label": "implicitness.parameters",
  "type": "ImplicitnessTestingParameters",
  "vary args independently": true,
  "args": {
    "version": [
      "ReplayBased",
      "LPBased"
    ],
    "subLogRestriction": [
      "None",
      "FittingOnAcceptedPlacesAndEvaluatedPlace",
      "MerelyFittingOnEvaluatedPair"
    ]
  }
}
```

It specifies all combinations of the `version` values and `subLogRestriction` values, that is, six in number.
These parameter variation specification blocks are then structured like the following example, again with the option to
vary them together or independently.

```
[
  [spec_a], # brackets are optional for singleton lists
  [
    spec_b,
    spec_c
  ]
]
```

Each inner list is varied independently of the other inner lists. This is meant to group parameters within those lists
that should be varied together.
In this example, the inner list/group `[spec_b, spec_c]` specifies that one variation, analogously to argument
variations, takes specifications of `spec_b` together with specifications of `spec_c` of the same index.
Crucially, these specs have to contain the same number of variations. Combined, they then again define a list of
variations, now of multiple parameters classes instead of parameter class fields.
Between these groups, variations are composed by taking all possible combinations between the groups' variations. This
quickly leads to a multiplicative blow-up.
Taking the structure of the above example with the `TauFitnessThreshold` spec as `spec_a`, `PlaceGeneratorParameters`
as `spec_b` and `ImplicitnessTestingParameters` as `spec_c`, we get the 18 variations.
`spec_a` specifies three, `spec_b` and `spec_c` each six. `spec_b` and `spec_c` are varied together, so their group also
specifies exactly six variations. `spec_a` is a singleton group with simply its own three variations.
Then the combinations of the two groups are `3*6=18`.

Overall, this structure provides a lot of flexibility for specifying parameter variations for a batch evaluation run. If
the component config or input data is to be varied as well, that can be achieved by again batch calling this command. 