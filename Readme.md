Description
-----------

Tasks Framework.
This library has to be used to design and run **concurrently** Tasks organized into Pipelines.


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
or finished successfuly.

### Pipeline
    Representation: [], [A, B]
A Pipeline represents a **sequential** (non parallelizable) set of Tasks.

### Parallel
    Representation: (), (A, B)
A Parallele represents an **unordonned** (parallelizable) set of Tasks.

## Requirement
A Requirement simply verifies that some conditions are met.

## Execution
An Execution is responsible of executing a Pipeline or Parallel.
It uses a JDK ExecutorService to abstract itself from the ThreadPool.

## TaskTracker
TaskTrackers are the **extensibility elements** of the Tasks Framework.

Toughts (for the future)
--------

TBC
We may rework Pipelines and Parallels because they may lead tounderstanding errors.
ATM, Pipelines should enforce the 'in-line' execution of its inner Task, and that's not completely the case right now,
each Task can add a new Requirement on a Task that is out of the scope of the containing Pipeline.
Maybe an InternalTask API that will hide the Task.requirements properties ?
Or maybe an upper level API that will construct Pipelines with Tasks ?
I have to think more about this...

Building
--------

### Requirements

* Maven 3+
* Java 6+

Check out and build:

    git clone https://USERNAME@forge.peergreen.com/git/git/poc/peergreen-tasks.git
    cd peergreen-tasks
    mvn clean install
