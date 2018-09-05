# Change Log

Notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and versioning adheres to [Semantic Versioning](http://semver.org/).


## 3.0.0 - 2018-09-04

### Added
- This changelog. Oh boy! ; )
- `SynchronizedStateMachine` is a `StateMachine` subclass that synchronizes its
public methods using either itself as the mutex or an object provided during
construction.
- `StateMachine#newStateMachine()` is a new convenience method to create a
machine whose input is the same as its graph.

### Changed
- In version 2.0.0, the signature for `StateMachine#evaluateInput()` was
updated to throw a checked exception. This version renames `evaluateInput()` to
`evaluateInputOrThrow()` and creates a new `evaluateInput()` method that,
rather than throwing an exception, returns whether the input was successfully
queued.
- Many of `StateGraph`'s methods now return the graph instance, allowing calls
to be chained.
- A `StateMachine` now requires both a graph and input adapter during
instantiation.

### Removed
- `StateMachine`'s default constructor and setters for its state graph and
input adapter were removed. They originally existed to make the class more
JavaBean-compliant and to simplify dependency injection. However, that no
longer seems necessary and allowing it created situations that required
additional validation.


## 2.0.0 - 2018-02-20

### Changed
- `StateMachine#evaluateInput()` no longer returns whether the given input was
ignored (i.e., the input did not cause a transition to happen.) This was
necessary because the evaluation may be deferred if evaluation is already in
progress.
- `StateMachine#evaluateInput()` may now throw an `InterruptedException`
because of its use of a semaphore to control access to certain sections of
code.
