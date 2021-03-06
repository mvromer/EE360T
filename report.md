# Visualizing Runtime Test Coverage

## Table of content
- [Visualizing Runtime Test Coverage](#visualizing-runtime-test-coverage)
  - [Table of content](#table-of-content)
  - [Abstract](#abstract)
  - [Introduction](#introduction)
  - [Technique](#technique)
    - [Runtime Agent](#runtime-agent)
    - [Web-Based Trace Explorer](#web-based-trace-explorer)
      - [Data Source](#data-source)
      - [UI Design](#ui-design)
      - [Technologies](#technologies)
      - [Implementation Details](#implementation-details)
  - [Demonstration](#demonstration)
  - [Future Work](#future-work)
    - [Limitations](#limitations)
    - [Enhancements](#enhancements)
  - [Conclusion](#conclusion)

## Abstract

Software testing is critical for the success of the software, but the cost is normally high
for the task. With the help of testing frameworks, tests can be executed easily, but
creating high value test cases is still time consuming. Since the number of possible
tests for even simple software is seemingly infinite for all practical purposes, it becomes
necessary to use tooling that measures test coverage percentage to help decide when
there are ‘enough’ test cases.

## Introduction
Despite many of the [researched and measured benefits](https://dl.acm.org/citation.cfm?id=2114489.2114785) of test driven development, experience has shown that it is still hard in many cases to convince a software engineer to incorporate this as part of their development process. However, even if one is consistently engaging in good software testing practices, it is just as difficult to build an effective suite of tests in an efficient manner.

Part of the challenge is determining whether a particular set of tests is good and worthy of inclusion in the overall test suite. One measure of goodness typically stems from the engineering team wanting to achieve some level of coverage with their test suite. Off the shelf tools exist for a variety of language environments capable of measuring and visualizing coverage such as [EclEmma](https://www.eclemma.org/) for Java and [NCover](https://www.ncover.com/) for .NET. However, many of these tools simply focus on reporting the aggregate value for the metric they are measuring, e.g., total code coverage, without deeper insights into the code actually covered at runtime.

Sometimes these tools will highlight source code lines executed during the execution of one or more tests, which can be good for viewing at a course level the runtime behavior of the code under test. However, very few of these tools will allow a software engineer to easily view the actual paths taken by their code, making it difficult to trace the actual execution of a test, especially one that touches several software components. This can be a severe impediment to a developer that is, for example, trying to recreate an error by constructing a representative test case and wishes to confirm that the test executes the same faulty sequence of instructions.

In this project, our goal is to record and visualize runtime test execution so that we may provide more information to a developer about the quality of their test suite. In particular, we will show how a combination of control flow and call graphs statically generated from the code under test in conjunction with trace information gathered at runtime can be used to identify the paths executed by each test in a test suite. We present this information in a web-based user interface that allows the developer to visualize the coverage of their test suite at three different levels:

- System view: Displays all thes classes in the system under test and highlights those covered by a particular test. The call graph across all classes is also viewable from here.
- Class view: Displays all the methods in the class and highlights those covered by a particular test.
- Method view: Displays the control flow graph of a method under test. Highlights the instruction nodes and execution paths taken within a method by a particular test.

The user interface allows a developer to drill-down from the system view all the way down to the method view. By providing more fine-grained information at deeper levels, the goal is to provide the developer with more insights into the coverage quality of each individual test as well as all tests in aggregate.

## Technique
Our test coverage analysis tool comprises two primary components: a runtime agent for instrumenting and tracing the code under test and a web-based review tool for visualizing and exploring trace results. In general, the agent assumes a well-defined set of test methods that validates functionality is provided by the code under test. The agent will generate a new trace for each test method executed, and each trace records the execution path taken by the code under test.

We target code compiled for the Java 8 SE JVM and whose tests are implemented using the [JUnit 4](https://junit.org/junit4/) test framework. Nevertheless, the techniques described herein are applicable to other language environments in which source code compiles to an object code format (e.g., bytecode) that can be readily parsed and augmented through instrumentation prior to execution.

### Runtime Agent
Our runtime agent is implemented as a custom Java agent that is loaded by the JVM prior to the execution of a program's main method. The agent installs both a custom class file transformer and a runtime shutdown hook that executes just prior to the JVM terminating. Here _class file_ is taken to mean a sequence of bytes in the Java class file format as defined by sections [2.1](https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-2.html#jvms-2.1) and [4](https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html) in the _Java Virtual Machine Specification: Java 8 SE Edition_. As such, our agent operates on the Java bytecode level as opposed to the Java source code level.

The agent is responsible for maintaining a trace record for each test method executed. Each record contains, among other things, a sequence of the bytecode instructions executed by the code under test. The agent delegates responsibility of the trace records to a global trace registry implemented using a [monostate pattern](http://wiki.c2.com/?MonostatePattern). The registry facilitates the creation and maintains the storage of all trace records during the lifetime of the agent. The use of a monostate simplified the task of class file transformation by distilling each instrumentation task down to a series of one or more static method calls.

Our agent's class file transformer is called by the JVM for each class loaded and defined at runtime. It allows the transformer an opportunity to modify the definition of the supplied class file prior to passing the modified bytecode back to the JVM for verification and application. A transformer may be selective in which parts, e.g., methods, of the class file it modifies, and it may choose to signal to the JVM that no modifications occurred by simply returning null.

Our class file transformer uses the [ASM bytecode manipulation framework](https://asm.ow2.io/) to instrument two sets of methods. The first are those marked with JUnit's `@Test` annotation. The start of these methods are augmented to create a new trace record by calling into our global trace registry. The newly created record becomes the _current_ or _active_ trace record, and the results of subsequent calls that record the instructions executed along a given path are stored within the current trace record.

The second set of methods instrumented by our class file transformer are those that belong to the code under test. The transformer identifies those classes to instrument by matching the fully qualified name of the class, i.e., class name prefixed with its package name, against a set of class name prefixes that are passed in as a configuration parameter to the runtime agent. If one of the class prefixes matches the name of the class passed to the transformer, then the transformer will instrument each of the class's methods.

For each method instrumented, a control flow graph is generated for the method and stored in the global trace registry. Conceptually each node represents an executable bytecode instruction within the method's definition, and each node is assigned a globally unique identifier for the current execution of the JVM. Each control flow graph is modified to include to pseudo-nodes representing well-defined entry and exit nodes respectively into and out of the method. These pseudo-nodes are also assigned globally unique identifiers and are used to simplify trace and control flow processing for the agent.

After constructing a method's control flow graph, for each node in the graph the class file transformer will add a call to the global trace registry to record the global ID of the node. The call for a given node is added immediately before the corresponding bytecode instruction is executed. Special handling is done to ensure the graph's pseudo-nodes are properly recorded as well. At runtime, this has the effect of producing a sequence of global node ID values corresponding to the exact execution path taken by the instrumented method, which is then recorded in the current trace record.

The transformer also uses a method's control flow graph to generate a partial call graph for the code under test. This partial call graph is recorded with the global trace registry for later processing. Once all methods under test have been instrumented, the complete call graph for the methods under test can be constructed.

In addition to generating a call graph, the class file transformer will also add instrumentation that is used to determine the sequence of method that are called during the execution of a given test method. The transformer augments a method's entry by inserting a call to record when the instrumented method has been entered. At the method's exit point, the transformer inserts a corresponding call to record when the instrumented method is about to return. At runtime, this information is forwarded to the current trace record which then uses it to construct the method call sequence.

If the code under test has been compiled with debug information and the runtime agent has been configured with the root directory containing the code, then the class file transformer will also annotate nodes in the control flow graph with their corresponding line of source code. This is used in the trace explorer to help map traces back to the lines of code actually executed by a JUnit test suite. If no source path is given or no debug information is available in the compiled bytecode, then nodes are identified in the trace explorer by their enclosing method and their node ID.

When the JVM terminates, the agent's shutdown hook is executed. During this time, the global trace registry serializes each trace record created at runtime, the call graph for the code under test, and the control flow graph for each method under test that was instrumented. Internal mapping state is also recorded so that the results stored in the previous three structures can be appropriately interpreted by downstream processors. All data is serialized to a JSON file in a format that is readily consumable by the web-based trace explorer.

### Web-Based Trace Explorer
This is the User Interface for visualizing the call graph, control flow graph of a system and it also enables user to select desired test case and highlight the coverage of selected test, features includes:
- generate different views and graphs for a system
- options for highlighting individual test cases and overall test coverage
- zoom in out feature for graphs
- adjustable split pane for different views
- responsive layout

#### Data Source
The Trace Explorer requires a JSON file named `results.json` as the data source for generating different graphs. The JSON should has 4 major data in order to generate all the graphs and enable all the features of the UI:
- callGraph: generating call graph for all classes in the `System View Pane`
- contorlFlows: generating control flow graph for methods of all classes in the `Method View Pane`
- traceRecords: each record shows as an item on the `View Test Dropdown` and highlights the paths on the `System View Pane` and `Method View Pane` when the corresponding test is selected.
- globalToLocalNodeId: the mapping reference  using a global id for all the nodes which contains details about the class and method that node belongs to.

#### UI Design
The Trace Explorer aims to help provide an intuitive interface for the user to understand the system architecture better and visualizing test coverage at different layers. There are 3 sections in the UI:

- Header: it is the fixed position section at the top containing the name of the application and the `View Test Dropdown`. User can switch between each individual test case or simply select `all` for showing the overall test coverage of all test cases.
- System View Pane: it is the upper large pane for showing all the classes of the system along with edges between their methods, which representing the call graph of the system. Each class is clickable for changing content in the `Method View Pane`, the thick border around the class indicates the selected state of the class.
- Method View Pane: it is the bottom pane of the page for showing control flow graph of methods in the selected class.

Apart from that, each node in the graph has it's own description, it shows the method name if the node represents a method and shows related line number and syntax if it's a node in a method. This can help user better associate the graph to the source code of the program.

Because of the screen size limitation, the graph generated for a complex system could too large to fit in the screen and readable at the same time. A zoom-in & -out feature for both panes is added to enlarge or minimize the graph according to the need of the user, plus svg vector graphics serves well for this purpose.

![alt text](screenshots/visualizer.jpg "UI design")

#### Technologies
This web application is using [webpack](https://webpack.js.org/) as the build tool for transpiling [Javascript ES6](https://developer.mozilla.org/en-US/docs/Web/JavaScript/New_in_JavaScript/ECMAScript_2015_support_in_Mozilla) and pre-processing [sass](https://sass-lang.com/) for styles in both development and production. [Mermaid.js](https://github.com/knsv/mermaid) is the main Javascript library for graph generation, and Mermaid.js has [d3](https://d3js.org/) and [dagre-d3](https://github.com/dagrejs/dagre-d3) as the dependencies for graphical layout and drawing libraries.
There're also some helper functions in the application for specific features. [split.js](https://nathancahill.github.io/Split.js/) helps to create the adjustable split panes on the page, and [he](https://github.com/mathiasbynens/he) encodes special characters to HTML entities, so they can be used as descriptions on the mehod nodes.

#### Implementation Details
As mentioned before, Mermaid.js is used for generating the call graphs and control flow graph, Mermaid takes a simple markdown-like script to do that. The challenge is to gather data from the data source and convert them into the Mermaid script and another challenge is to making the generated SVG graphs to be interactive. The original Mermaid library cannot archive the goal, We have to slightly modify it, so the elements in the graphs are generated with the desired attributes which make them selectable by Javascript for adding the highlighting effects as well as some transition effects.

The app first loops through  `controlFlows` object from the data source to build the class blocks and control flow graphs and then retrieves data from `callGraph` to build the edges between class blocks. Each element in the blocks and graph has a unique id composed from the JSON data and in the consistent format like this:
- `System View Pane`:
  - class container: [classsName]
  - method: [globalMethodId]
  - call graph edge: [globalMethodId:from]-[globalMethodId:to]

- `Method View Pane`:
  - method container: [methodName]-[methodDescriptor]
  - method node: [globalNodeId]
  - control flow graph edge: [globalNodeId:from]-[globalNodeId:to]

So each element can be selected individually, When a test is selected from the `Test Dropdown`, it takes global node ids from the `tracePath` of the `traceRecords` object, and select all the related elements and add the highlighting styles to them in order to show the actual test paths. Similarly, the `all` option on the `Test Dropdown` trigger the code to retrieve and highlight `tracePath` for all tests to show the overall coverage.

## Demonstration
This is a simple demonstration of the data source generated by the Trace Agent and it's result on the Trace Explorer.
<!-- links may need to update -->
- [Java code example](https://github.com/mvromer/EE360T/tree/master/ControlTracer/TraceDriver/src/ee360t/controlflow/trace/examples)
- [Test driver example](https://github.com/mvromer/EE360T/blob/master/ControlTracer/TraceDriver/src/ee360t/controlflow/trace/driver/App.java)
- [corresponding Trace Agent output](https://github.com/mvromer/EE360T/blob/master/ui/dist/results.json)

On the Trace Exploer:
1. Call graphs showing in the System View Pane with no control flow graph showing and no test selected
![alt text](screenshots/result-1.png "demo step 1")

2. Click on one of the class block and have the methods in that class showing in the Method view Pane
![alt text](screenshots/result-2.png "demo step 2")

3. Select a test from the Test Dropdown to highlight the test path on the graphs
![alt text](screenshots/result-3.png "demo step 3")

4. Enlarge a comples graphs to view the details
![alt text](screenshots/result-4.png "demo step 4")

## Future Work
Our current implementation can be improved by both addressing existing limitations and developing additional feature enhancements. Here we explore both sets of opportunities for improvement and discuss future work that can be done to take advantage of them.

### Limitations
A handful of issues limits the scenarios in which our current implementation could be applied. Most notably the runtime agent is **not** thread-safe, thus executing any instrumented method that executes across multiple threads would be inherently dangerous. At best we could expect the output of the agent to contain corrupted trace records. To resolve this, thread synchronization would need to be added to coordinate access to the shared global state maintained by the trace registry.

The scalability of the trace explorer currently prevents large numbers of classes or classes that span a large number of nodes to be viewed efficiently. Test suites that result in the instrumentation of a large number of classes and/or methods generate system renderings that are difficult to view and navigate, despite the availability of zoom controls within the user interface. Furthermore, agent output that spans on the order of thousands of nodes causes the trace explorer to experience periods of unresponsiveness as it processes and renders all of the nodes prior to displaying the results.

One strategy for improving responsiveness would be make greater use of lazy evaluation when loading and processing the trace results produced by the agent. In particular, instead of parsing and rendering all control flow graphs when the trace explorer first loads, each graph could be processed the first time the user requests to view it. Another approach would be to introduce new user interface elements that help limit the amount of content that is displayed at any given time, allowing more screen real estate to be dedicated to a few number of elements at any given time. Such elements would include options for filtering which methods, and thus control flow graphs, are visible at a given time as well as pagination features to allow for efficient scrolling through visual elements within the browser.

Finally, our trace explorer currently loads the trace results file from a fixed path on disk. This limits testability since not all browsers, e.g., Chrome, support a JavaScript web application loading files from local storage. Moving forward, this can be improved by introducing a small server component that can locally host the trace results file and server it over a REST API that the trace explorer would call directly. This has the additional advantage of further preparing the trace explorer for deployment in more production-like scenarios where the expectation would be for it to integrate with other components over a REST API.

### Enhancements
A number of enhancements can be made to the runtime agent to not only make it more broadly applicable to a wider range of test suites but also enhance the usability of the trace explorer.

By using a set of prefixes to select which classes to instrument, it is possible for the agent to transform more classes than would be normally desired. For example, suppose the code under test and its test suite are both located in the the `org.example.app` package. Using a prefix of `org.example.app` would have the undesirable effect of instrumenting the code's test suite. It is possible to use a fully qualified class name as the "prefix" in these cases, e.g., `org.example.app.App` if the application under test was implemented in the `App` class; however, this would still fail in scenarios where the test suite is implemented in a class whose name contains the configured prefix, e.g., `AppTest`. This exact scenario was observed when running the runtime agent against the [JTransforms](https://github.com/wendykierp/JTransforms) test suite.

These issues can be mitigated by the providing runtime agent configuration parameters that allow users to explicitly specify the classes they wish to instrument. Additionally, the existing parameter used to specify class name prefixes to match against can be augmented such that the parameter value provided is a regular expression instead of a static prefix. This would allow users a finer level of control over which classes are augmented while also keeping the agent's parameterization concise as possible.

Currently the control flow graph generation does not take into account inheritance hierarchies. For example, if an `INVOKEDYNAMIC` instruction is encountered and the specified method is called through a base class object reference, the control flow and call graphs will show edges to the base class version of the method only. To better model polymorphic calls, it would be ideal for both graphs to generate edges to all known overrides for the invoked method, possibly annotating the edges in such a way to emphasize that each represents one of many possible calls that could occur at runtime.

Another control flow graph improvement would be better handling of interclass method calls. At present, we model these in our call graph but not in our control flow graphs. Instead, we only model intraclass method calls at the control flow level.

For all methods within a class, each method's control flow graph is augmented so that an invocation to a method within the same class has an edge to the invoked method's entry node. Additionally, an edge is added from the invoke method's exit node to the calling method's return site. However, for any method that invokes a method outside the former's class, the generated control flow graph contains nothing to represent the invocation of the called method.

This can lead to possible confusion when using the trace explorer to view the control flow graph for a method containing one of these interclass method calls. The user interface simply renders this as a linear sequence of nodes wholly contained within the method rather than showing proper edges that result in control flow being transferred to the called method. Incorporating this interclass method call information at the control flow level would help in generating more accurate diagrams that better explain to the user what paths their software could potentially execute at runtime.

## Conclusion
Using the statically generated control flow and call graphs with traces of code paths executed, we have demonstrated how to construct a tool for visualizing at both course- and fine-grained levels the runtime behavior of a suite of tests and how to tie the information together in a relatively cohesive manner. Despite existing limitations and opportunities for improvement, our tool is still can help developers to easily find code paths not covered by existing tests, to give them a better understanding of the test coverage beyond just a single percentage value, and to readily trace paths for failed tests. This level of visiblity can in turn be used to build higher quality test methods. The principles presented can also be readily applied to comparable language and runtime environments such as .NET Framework and .NET Core. Finally, further enhancements will allow it handle a broad range of code under test and potentially applicable in new use cases.

