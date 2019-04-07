jStately
========

jStately is a state machine library written in Java. Its goal is to allow developers to handcraft
maintainable, readable state graphs in an object-oriented way.

It was written with OOD principles and extensibility in mind. Users build `StateGraph`s primarily
from `State`s and `Transition`s, overriding methods for events like entering a state
(`State#onEnter()`), exiting a state (`State#onExit()`) or following a transition
(`Transition#onTransition`).

jStately relies heavily on interfaces but also provides sensible default implementations. For
example, a user could implement a `Transition` that determines whether it is valid for a given
input based on arbitrary business logic. But the provided `EqualityTransition` implementation will
appeal to users whose transitions are based simply on whether a certain input value was
encountered.

Although jStately does not aim to implement a particular state machine definition, features from
common definitions are present. For example, `CompositeState`s are similar to those in UML.
Composite states form a collection of `State`s, which has its own entry/exit callbacks and can
have transitions, which are evaluated while the machine is in any of the states it contains.

Example 
-------

The example below implements a state graph that models the behavior of the ghost characters in the
classic video game Pac-Man. For those not familiar with the game, a brief summary can be found on
[Wikipedia](https://en.wikipedia.org/wiki/Pac-Man#Gameplay). A simplified diagram of a ghost's
behavior might look like this:

![Pac-Man Ghost state graph](readme/PacManGhostStateGraph.png) 

Defining the states and transitions in jStately might look something like this:

```java
public class GhostStateGraph extends StateGraph<GameEvent> {
    public GhostStateGraph() {
        State<GameEvent> wanderingState = new DefaultState<>("Wandering maze");
        State<GameEvent> chasingState = new DefaultState<>("Chasing Pac-Man");
        State<GameEvent> fleeingState = new DefaultState<>("Fleeing Pac-Man");
        State<GameEvent> returningHomeState = new DefaultState<>("Returning home");

        setStartState(wanderingState);

        addTransition(wanderingState,
                new EqualityTransition<>(chasingState, GameEvent.PACMAN_SPOTTED));
        addTransition(chasingState,
                new EqualityTransition<>(wanderingState, GameEvent.PACMAN_LOST));
        addTransition(fleeingState,
                new EqualityTransition<>(wanderingState, GameEvent.POWER_PELLET_WORE_OFF));
        addTransition(fleeingState,
                new EqualityTransition<GameEvent>(returningHomeState, GameEvent.GHOST_EATEN));
        addTransition(returningHomeState,
                new EqualityTransition<>(wanderingState, GameEvent.GHOST_REACHED_HOME));

        new CompositeState<>(wanderingState, chasingState)
                .addTransition(new EqualityTransition<>(fleeingState, GameEvent.POWER_PELLET_EATEN));
    }
}
```

A `StateGraph` defines the relationship between its states but the graph itself is stateless. It is
a `StateMachine` (which is given a graph during construction) that keeps track of the current state
and evaluates inputs in order to traverse its graph. A `StateMachine` is initialized like so:

```java
StateMachine<GameEvent, GameEvent> machine =
        StateMachine.create(new GhostStateGraph());

// Initializes the machine's state to its graph's start state. Doing so calls
// StateGraph#onStart() and State#onEnter(), as well as CompositeState#onEnter()
// on any composite states that include the start state.
machine.start();
```

As the game continues and it is determined that important things have happened (i.e., things that
may or may not cause the machine to transition states, according to our graph,) an event would be
evaluated by the machine with a call to `machine.evaluateInput(GameEvent.PACMAN_SPOTTED)`.

Keeping track of a system's current state _can_ be useful on its own. But a more powerful use for a
graph is to use it to drive other behaviors. For example, suppose that a developer wants certain
things to happen as the machine transitions between states. One such example might be rendering a
ghost in blue while fleeing Pac-Man. To do that, a developer might define the "fleeing" state like
this:

```java
new DefaultState<GameEvent>("Fleeing Pac-Man") {
    @Override
    public void onEnter() {
        changeGhostColorToBlue();
    }

    @Override
    public void onExit() {
        restoreOriginalGhostColor();
    }
};
```

Alternatively, those same methods (`changeGhostColorToBlue()` and `restoreOriginalGhostColor()`)
could be called from the transitions leading to and from `fleeingState`, like so:

```java
new EqualityTransition<GameEvent>(fleeingState, GameEvent.POWER_PELLET_EATEN) {
    @Override
    public void onTransition(GameEvent gameEvent) {
        changeGhostColorToBlue();
    }
}
```

Submachine States
-----------------

Sometimes it's useful to define a graph that is reused within another graph. In these cases, a
`SubmachineState` can be used. A submachine state is initialized with a graph that has the same
input type as its parent graph. When a state machine enters a submachine state, it creates a new
state machine internally and future `evaluateInput()` calls are delegated to the child state
machine until it reaches a special `State` subclass – `FinalState`. When that happens, the value
returned by the final state's `getResult()` method is evaluated as an input to the parent state
machine, allowing it to transition out of the submachine state.

There is no set limit on how deeply nested state graphs can be using `SubmachineState`.

Input Adapters
--------------

While using jStately, you will probably notice that a `StateGraph` uses generics to specify the
type of input its transitions evaluate. But a `StateMachine` has _two_ type parameters –
`MachineInput` and `TransitionInput`. jStately allows a machine to be initialized with an
`InputAdapter` that allows an input given to the machine to be transformed in to any number of
transition inputs.

A simple example is an adapter that allows a machine to takes a `String` and iterate through its
characters, evaluating each individually. A less trivial example might be an adapter that takes a
widget ID and retrieves the Widget object from a database.

When no transformation is needed (which is often the case,) the provided `PassthroughInputAdapter`
can be used. It is used by default when constructing a machine using `StateMachine.create()`, as in
the example above.

Callbacks
---------

`StateMachineEventListener`s provide callbacks for a number of events that happen within a state
machine. They can be very helpful for debugging and are added to a machine using
`StateMachine#addEventListener()`.

Because the interface has so many methods, `DefaultStateMachineEventListener` is provided as a
no-op implementation for developers to extend, so they can override only the methods they are
interested in.

For developers interested in seeing information regarding all of the events,
`ConsoleStateMachineEventListener` logs `System.out`.
