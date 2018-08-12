# Visualizing Runtime Test Coverage

## Table of content
  - [Abstract](#abstract)
  - [Introduction](#introduction)
  - [Implementation](#implementation)
    - [Runtime Trace Agent](#runtime-trace-agent)
    - [Web-Based Trace Visualizer](#web-based-trace-visualizer)
  - [Results](#results)
  - [Future Work](#future-work)
  - [Conclusion](#conclusion)

## Abstract

Software testing is critical for the success of the software, but the cost is normally high
for the task. With the help of testing frameworks, tests can be executed easily, but
creating high value test cases is still time consuming. Since the number of possible
tests for even simple software is seemingly infinite for all practical purposes, it becomes
necessary to use tooling that measures test coverage percentage to help decide when
there are ‘enough’ test cases.

## Introduction
In this project, the goal is to visualize the test coverage to provide more information to
the developer about the quality of their test suite. Here are the details:
- Statically generate call and control flow graphs of the software under test.
- At runtime, trace execution of a test to determine the path taken through the call
and control flow graphs.
- Present a UI allowing one to drill down into different levels of the software:
system view, class view and method view.
  - System view: displays all the class components of the system
  - Class view: displays all the methods in the class
  - Method view: displays control flow graph of the method
- Highlight the paths from each test execution on the UI as well and the control
flow path in the involved methods.

The visualization of the test execution provides the following advantages:
- Easily find code paths without tests
- Provide a better understanding of the test coverage beyond just a percentage
- Readily trace paths for the failed tests

## Implementation

### Runtime Trace Agent

### Web-Based Trace Visualizer

## Results
Show example classes and visualized results.

## Future Work
Talk about improvements we could make or features we could add.

## Conclusion
Summarize work and results.
