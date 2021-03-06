Description
-----------

Tasks Framework.
This library has to be used to design and run **concurrently** Tasks
organized into execution structures.


Components
--------

## Task
A Task is a basic unit of execution.
It has a name, a state and some requirements.
Its requirements are used to determine if the Task is executable or not.
At the end of its execution, the state move to COMPLETED.

### UnitOfWork
    Representation: A
A unitOfWork is a concrete Task with that contains a Job.
When the Task is executed, its Job is executed too.
The final Task's State is either FAILED or COMPLETED if the job has, respectively, throw an Exception
or finished successfully.

### Pipeline
    Representation: [], [A, B]
A Pipeline represents a **sequential** (non parallelizable) set of Tasks.

### Parallel
    Representation: (), (A, B)
A Parallel represents an **unordonned** (parallelizable) set of Tasks.

### WakeUp
    Representation: @[A]
A WakeUp represents a Task that requires to be awakened before being really executed.

### Delegate
    Representation: |A|
A Delegate represents a Task holder. It is useful when the model requires a place
reservation, but you don't know yet the Task type at this place.

## Execution
An Execution is responsible of executing a Task.
There is an Execution type per Task type (PipelineExecution, ParallelExecution, ...).

## TaskTracker
TaskTrackers are the **extensibility elements** of the Tasks Framework.

Toughts (for the future)
--------

Building
--------

### Requirements

* Maven 3+
* Java 6+

Check out and build:

    git clone https://USERNAME@forge.peergreen.com/git/git/poc/peergreen-tasks.git
    cd peergreen-tasks
    mvn clean install
