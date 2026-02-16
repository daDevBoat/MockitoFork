# Report for assignment 3

This is a template for your report. You are free to modify it as needed.
It is not required to use markdown for your report either, but the report
has to be delivered in a standard, cross-platform format.

## Project

Name:

URL:

One or two sentences describing it

## Onboarding experience

Did it build and run as documented?
    
See the assignment for details; if everything works out of the box,
there is no need to write much here. If the first project(s) you picked
ended up being unsuitable, you can describe the "onboarding experience"
for each project, along with reason(s) why you changed to a different one.


## Complexity

1. What are your results for five complex functions?
   * Did all methods (tools vs. manual count) get the same result?
   * Are the results clear?
2. Are the functions just complex, or also long?
3. What is the purpose of the functions?
4. Are exceptions taken into account in the given measurements? 
5. Is the documentation clear w.r.t. all the possible outcomes?

### Alexander Mannertorn

```java
    //pi = number of decision
    //s = number of exit
    private <T> void triggerRetransformation(Set<Class<?>> types, boolean flat) {
        Set<Class<?>> targets = new HashSet<Class<?>>();

        try {
            for (Class<?> type : types) {
                //pi=1
                if (flat) {
                    //pi = 3
                    if (!mocked.contains(type) && flatMocked.add(type)) {
                        assureInitialization(type);
                        targets.add(type);
                    }
                } else {
                    do {
                        //pi = 4
                        if (mocked.add(type)) {
                            //pi = 5
                            if (!flatMocked.remove(type)) {
                                assureInitialization(type);
                                targets.add(type);
                            }
                            addInterfaces(targets, type.getInterfaces());
                        }
                        type = type.getSuperclass();
                        //pi = 6
                    } while (type != null);
                }
            }
        } catch (Throwable t) {
            for (Class<?> target : targets) {
                mocked.remove(target);
                flatMocked.remove(target);
            }
            // s = 1
            throw t;
        }
        //pi = 7
        if (!targets.isEmpty()) {
            try {
                assureCanReadMockito(targets);
                instrumentation.retransformClasses(targets.toArray(new Class<?>[targets.size()]));
                Throwable throwable = lastException;
                //pi = 8
                if (throwable != null) {
                    // s = 2
                    throw new IllegalStateException(
                            join(
                                    "Byte Buddy could not instrument all classes within the mock's type hierarchy",
                                    "",
                                    "This problem should never occur for javac-compiled classes. This problem has been observed for classes that are:",
                                    " - Compiled by older versions of scalac",
                                    " - Classes that are part of the Android distribution"),
                            throwable);
                }
            } catch (Exception exception) {
                for (Class<?> failed : targets) {
                    mocked.remove(failed);
                    flatMocked.remove(failed);
                }
                //s = 3
                throw new MockitoException("Could not modify all classes " + targets, exception);
            } finally {
                lastException = null;
            }
        }

        mocked.expungeStaleEntries();
        flatMocked.expungeStaleEntries();
    }
        //M = cyclomatic complexity 
        //M = pi - s + 2 = 8 - 3 + 2 = 5 + 2 = 7
```
1. I counted the cyclomatic complexity to 7 for the triggerRetransformation() function, but
the Lizard tool said the cylomatic complexity number (CCN) was 14, this is because the Lizard tool
uses another way of counting the CCN then the method we have used where the cyclomatic complexity
is equal to the number of decisions minus the number of exits plus 2.
2. The triggerRetransformation() function is not just complex but also quite long (60 lines of code) 
3. The purpose of the triggerRetransformation() is to collect a set of classes that needs to be reinstrumented and then call instrumentation.retransformClasses(...).
4. Yes, since we count the throws as an exit point
5. There is no documentation specific for the triggerRetransformation() function but the code makes all possible outcomes pretty clear. 

## Refactoring

Plan for refactoring complex code:

Estimated impact of refactoring (lower CC, but other drawbacks?).

Carried out refactoring (optional, P+):

git diff ...

## Coverage

### Tools

Document your experience in using a "new"/different coverage tool.

How well was the tool documented? Was it possible/easy/difficult to
integrate it with your build environment?

### Your own coverage tool

Show a patch (or link to a branch) that shows the instrumented code to
gather coverage measurements.

The patch is probably too long to be copied here, so please add
the git command that is used to obtain the patch instead:

git diff ...

What kinds of constructs does your tool support, and how accurate is
its output?

### Evaluation

1. How detailed is your coverage measurement?

2. What are the limitations of your own tool?

3. Are the results of your tool consistent with existing coverage tools?

## Coverage improvement

Show the comments that describe the requirements for the coverage.

Report of old coverage: [link]

Report of new coverage: [link]

Test cases added:

git diff ...

Number of test cases added: two per team member (P) or at least four (P+).

## Self-assessment: Way of working

Current state according to the Essence standard: ...

Was the self-assessment unanimous? Any doubts about certain items?

How have you improved so far?

Where is potential for improvement?

## Overall experience

What are your main take-aways from this project? What did you learn?

Is there something special you want to mention here?
