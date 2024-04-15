## SPECpp
This is the development repository for the bottom-up process discovery framework _SPECpp_.
An acronym for:
- **S**upervision
- **P**roposal
- **E**valuation
- **C**omposition
- **P**ost-**P**rocessing

It is intended as a standalone (with ivy dependencies) runnable framework (see package /headless, most importantly [batch CLI info](src/org/processmining/specpp/headless/batch/help.md)), however an interactive ProM plugin making use of this framework is included here (see package /prom).
This piece of software provides some structure, common implementations and extensive "supervision" as well as "inter component dependency and data management" support for interested developers who want to play around with evolutions of the original [eST-Miner](http://dx.doi.org/10.1007/978-3-030-21571-2_15) by L. Mannel et al.

The logical structure of the discovery approach is looping proposal, evaluation & composition, with post-processing at the end.
In this instance, proposal (potential candidate oracle) is specified via efficient local tree traversal.
Composition is handled by token-based replay fitness thresholding, as well as variants on "postponing" strategies that first collect a number of places (e.g. a tree level), then make slightly less greedy acceptance/rejection decisions (Delta & Uniwired Composer).
They can also make use of local information-based evaluations of the at-this-point intermediate result regarding a potential candidate. Instanced here by concurrent implicitness testing.
Finally, post-processing is a pipeline of transforming operations starting off with the final set of collected places, e.g. implicit place removal.

A big technical aspect is the "inter component dependency and data management". Components can request as well as provide arbitrarily definable dependencies, e.g. data sources & parameters, evaluation functions, observables & observers.
The at-runtime declared dependencies are resolved after constructor call and are either satisfied or not at _init()_ time.
The observables and observers are the facility by which supervision system functions. Components can publish generic performance measurement events, as well as arbitrary user defined "xy happened" events.
Concurrently running supervisors can plug into these streams of events as observers, transform it, e.g. counting, and finally log it.
Particularly for the _ProMless_ execution format, visualization components such as live updating charts are available.

#### To Run
1. Clone
2. Setup project java jdk (1.8)
3. Setup ivy facet in IDE (IvyIDEA-like plugin installation may be required)
   1. Mark ivysettings.xml as settings file
4. Resolve dependencies
5. Run entry points
   1. You can use the configurations in specpp/.run/ (IntelliJ) and the  ProM .launch files (Eclipse(r)) 
   2. ProMPackageManager has to be run first to resolve ProM dependencies
   3. ProM launches this local ProM instance with the plugin in the package prom/
   4. ProMLessSPECpp contains the basic 'event log in - Petri net out' command line interface
   5. `BasicSupervisedSPECpp` is deprecated but provides lots of output for experimentation
   6. `CodeDefinedConfigurationSample` has a hard-coded execution setup that exemplifies the code defined framework configuration options
   7. `Batching` provides an extensive command line interface for batch execution and automatic parameter variation (see [help](src/org/processmining/specpp/headless/batch/help.md))
