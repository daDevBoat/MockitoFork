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


#### Jonatan
```java
@Override
public boolean equals(Object obj) {
    if (this == obj) { // +1
        return true; // -1
    }
    if (obj == null) { // +1
        return false; // -1
    }
    if (getClass() != obj.getClass()) { // +1
        return false; // -1
    }
    SerializableMethod other = (SerializableMethod) obj;
    if (declaringClass == null) { // +1
        if (other.declaringClass != null) { // +1
                return false; // -1
        }
    } else if (!declaringClass.equals(other.declaringClass)) { // +1
        return false; // -1
    }
    if (methodName == null) { // +1
        if (other.methodName != null) { // +1
                return false; // -1
        }
    } else if (!methodName.equals(other.methodName)) { // +1
        return false; // -1
    }
    if (!Arrays.equals(parameterTypes, other.parameterTypes)) { // +1
        return false; // -1
    }
    if (returnType == null) { // +1
        if (other.returnType != null) { // +1
                return false; // -1
        }
    } else if (!returnType.equals(other.returnType)) { // +1
        return false; // -1
    }
    return true; // -1
    // π = 13, s = 11
    // M = π - s + 2 = 4
}
```

1.`M = π - s + 2` yielded a much lower complexity than Lizard. Lizard uses a different formula which does not subtract exit points (and yielded 14 as complexity). It is simple to understand why an exit just after a decision shouldn't increase complexity, in that sense it seems more clear than Lizard's formula.
2. The `equals` function is not particularly long, just complex.
3. The purpose is overriding the equals function (which just compares object references) for the `SerializableMethod` class, in order to compare with custom logic.
4. There are no exceptions.
5. The code is pretty self-explanatory and there is no need to document further the possible outcomes.
### Elias
```java
public static boolean reflectionEquals(
            Object lhs,
            Object rhs,
            boolean testTransients,
            Class<?> reflectUpToClass,
            String[] excludeFields) {
        if (lhs == rhs) { # +1
            return true; # -1
        }
        if (lhs == null || rhs == null) { # +2
            return false;   # -1
        }
        // Find the leaf class since there may be transients in the leaf
        // class or in classes between the leaf and root.
        // If we are not testing transients or a subclass has no ivars,
        // then a subclass can test equals to a superclass.
        Class<?> lhsClass = lhs.getClass();
        Class<?> rhsClass = rhs.getClass();
        Class<?> testClass;
        if (lhsClass.isInstance(rhs)) {     # +1
            testClass = lhsClass;
            if (!rhsClass.isInstance(lhs)) {    # +1
                // rhsClass is a subclass of lhsClass
                testClass = rhsClass;
            }
        } else if (rhsClass.isInstance(lhs)) {  # +1
            testClass = rhsClass;
            if (!lhsClass.isInstance(rhs)) {    # +1
                // lhsClass is a subclass of rhsClass
                testClass = lhsClass;
            }
        } else {
            // The two classes are not related.
            return false;   # -1
        }
        EqualsBuilder equalsBuilder = new EqualsBuilder();
        if (reflectionAppend(lhs, rhs, testClass, equalsBuilder, testTransients, excludeFields)) {  # +1
            return false;   # -1
        }
        while (testClass.getSuperclass() != null && testClass != reflectUpToClass) { # +2
            testClass = testClass.getSuperclass();
            if (reflectionAppend(
                    lhs, rhs, testClass, equalsBuilder, testTransients, excludeFields)) {   # +1
                return false;   # -1
            }
        }
        return equalsBuilder.isEquals();    # -1
    }
    // π = 11, s = 6
    // M = π - s + 2 = 7
``` 
1. The Lizard tool gave the reflectionEquals function a cyclomatic complexity of 12, while the manual counting gave it 7. This is due to Lizard estimating cyclomatic complexity by doing **π + 1** while we use the method described in the lecture as **M = π - s + 2** where **π** is number of decisions and **s** is number of exit points. 
2. The reflectionEquals function is complex, and is acceptably long at 41 lines of code.
3. The purpose of the reflectionEquals function is to check if the lhs is equal to the right hand side and return true or false based on that.
4. No exceptions are thrown by this function.
5. The documentation is not very clear w.r.t all possible outcomes, but there is some helpful comments in terms of understanding how the function works.



1. What are your results for five complex functions?
   * Did all methods (tools vs. manual count) get the same result?
   * Are the results clear?
5. Are the functions just complex, or also long?
6. What is the purpose of the functions?
7. Are exceptions taken into account in the given measurements?
8. Is the documentation clear w.r.t. all the possible outcomes?

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
