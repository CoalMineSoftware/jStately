jStately
========

jStately is a state machine library written in Java. Its goal is to allow developers to handcraft
maintainable, readable state graphs in an object-oriented way.

It was written with OOD principles in mind. Users build `StateGraph`s primarily from `State`s and
`Transition`s with listeners for events like entering a state, exiting a state or following a
transition. Whether a `Transition` is valid for a given input is determined by a `Predicate`. Users
can provide predicates with whatever arbitrary logic they would like. However, `TransitionBuilder`
allows  users to easily build a transition that's valid for a given input.

Although jStately does not aim to implement a particular state machine definition, common concepts
like composite states and submachines are present.

Composites
---

A composite state forms a collection of `State`s. It has its own entry and exit callbacks and can
have its own transitions. Composite states can be nested. When a state machine fails to find a
valid transition from its current state, it evaluates transitions from its state's composite,
followed by its composite's composite, and so on.

Example 
-------

The state graph below models the behavior of the ghost characters in the classic video game
Pac-Man. For those not familiar with the game, a brief summary can be found on
[Wikipedia](https://en.wikipedia.org/wiki/Pac-Man#Gameplay).

![Pac-Man Ghost state graph](readme/PacManGhostStateGraph.png) 

Defining the states and transitions in jStately might look something like this:

```java
State<GameEvent> wanderingState = new StateBuilder<GameEvent>().setDescription("Wandering maze").build();
State<GameEvent> chasingState = new StateBuilder<GameEvent>().setDescription("Chasing Pac-Man").build();
State<GameEvent> fleeingState = new StateBuilder<GameEvent>().setDescription("Fleeing Pac-Man").build();
State<GameEvent> returningHomeState = new StateBuilder<GameEvent>().setDescription("Returning home").build();

new CompositeStateBuilder<GameEvent>()
        .addState(wanderingState)
        .addState(chasingState)
        .addTransition(TransitionBuilder.forExpectedInputs(fleeingState, GameEvent.POWER_PELLET_EATEN).build());

StateGraph<GameEvent> stateGraph = new StateGraphBuilder<>(wanderingState)
        .addTransition(wanderingState, TransitionBuilder.forExpectedInputs(chasingState, GameEvent.PACMAN_SPOTTED).build())
        .addTransition(chasingState, TransitionBuilder.forExpectedInputs(wanderingState, GameEvent.PACMAN_LOST).build())
        .addTransition(fleeingState, TransitionBuilder.forExpectedInputs(wanderingState, GameEvent.POWER_PELLET_WORE_OFF).build())
        .addTransition(fleeingState, TransitionBuilder.forExpectedInputs(returningHomeState, GameEvent.GHOST_EATEN).build())
        .addTransition(returningHomeState, TransitionBuilder.forExpectedInputs(wanderingState, GameEvent.GHOST_REACHED_HOME).build())
        .build();
```

A `StateGraph` defines the relationships between its states but it is a `StateMachine` that has a
current state and evaluates inputs in order to  traverse the graph. A machine is initialized like 
so:

```java
StateMachine<GameEvent, GameEvent> machine =
        StateMachineBuilder.forMatchingInputTypes(stateGraph).build();

// Initializes the machine's state to its graph's start state. Doing so calls onStart() on the
// Graph's StartListener, onEnter() on start state's EntranceListener, as well as onEnter() on the
// EntranceListener of any CompositeState that contains the start state.
machine.start();
```

As the game continues and it is determined that important things have happened (i.e., things that
may or may not cause the machine to transition states, according to our graph,) an event would be
evaluated by the machine with a call like `machine.evaluateInput(GameEvent.PACMAN_SPOTTED)`.

Although developer's can inspect a state machine's current state, that is generally discouraged in
favor of adding listeners for entering a state, exiting a state or traversing a transition. In our
Pac-Man example, suppose that we want a ghost's color to alternate between blue and white while
fleeing Pac-Man. That could be accomplished by defining the "fleeing" state like so:

```java
State<GameEvent> fleeingState = new StateBuilder<GameEvent>()
        .setDescription("Fleeing Pac-Man")
        .setEntranceListener(() -> ghost.startAnimatingColor())
        .setExitListener(() -> ghost.restoreNormalColor())
        .build();
```

Alternatively, those same methods (`startAnimatingColor()` and `restoreNormalColor()`) could be
called from the transitions leading to and from `fleeingState`, like so:

```java
TransitionBuilder.forExpectedInputs(fleeingState, GameEvent.POWER_PELLET_EATEN)
        .setTransitionListener(gameEvent -> ghost.startAnimatingColor())
        .build()
```

Submachines
---

A `SubmachineState` allows a state graph to be embedded within another. This allows a
self-contained graph to be defined once and re-used in multiple graphs or multiple times within a
single graph. When building a `SubmachineState`, a `StateGraph` with the same input type is
required.  When a machine enters a submachine state, it creates a new state machine instance
internally. Subsequent inputs are delegated to the new machine until it reaches a `FinalState`.
When the inner machine reaches a final state, the outer machine evaluates the value of
`FinalState#getResult()` (also of type `InputType`) on itself,  allowing it to transition out of
the `SubmachineState`.

In general, a `SubmachineState`'s graph should have at least one `FinalState`. Each one can have a
different result, allowing the outer machine to follow a different transition out of the
`SubmachineState`.

There is no set limit on how deeply nested state graphs can be using `SubmachineState`.

Input Adapters
--------------

While using jStately, you will notice that a `StateGraph` uses generics to specify the type of
input its transitions evaluate, but a `StateMachine` has two type parameters – `TransitionInput`
and `MachineInput`. The Example section above creates a machine using
`StateMachineBuilder.forMatchingInputTypes()`, which builds a machine that uses the same type
(`GameEvent`) for both.

Alternatively, developers can provide an `InputAdapter` that the machine uses to transform its
input into zero or more transition inputs. The `InputAdapter` interface has a single method that
takes a `MachineInput` and returns an `Iterator<TransitionInput>`.  

A simple example is an adapter that allows a machine to takes a `String` as its input, iterating
through its characters and evaluating each as an individual transition input.

```java
public class StringToCharacterInputAdapter implements InputAdapter<String, Character> {
    @Override
    public Iterator<Character> adaptInput(String input) {
        return new Iterator<>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return input != null && index < input.length();
            }

            @Override
            public Character next() {
                return input.charAt(index++);
            }
        };
    }
}
```

A less trivial example is an adapter that allows the machine to take a list of widget IDs and
retrieves the corresponding `Widget` objects from a database. Such an adapter would implement
`InputAdapter<List<Integer>, Widget>`.

Callbacks
---------

`StateMachineEventListener` defines callbacks for a number of events that happen within a state
machine. They can be very helpful for debugging and are added to a machine using either
`StateMachine#addEventListener()` or the corresponding call on `StateMachineBuilder`.

For developers interested in logging all of the events to a `PrintStream`, such as `System.out`,
see `PrintStreamStateMachineEventListener`.
