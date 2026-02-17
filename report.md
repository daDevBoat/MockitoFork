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
To count the complexity by hand, we used the formula provided in the lecture, where

- M = Cyclographic Complexity
- π = number of decision points
- s = number of exit points
- M = π - s + 2

### Questions:

1. **What are your results for five complex functions?**
`M = π - s + 2` yielded a lower complexity than Lizard. Lizard uses a different formula which does not subtract exit points, but only counts the decsions points and adds 1. We understodd both ways of counting, and that both have advantages and drawbacks. Our results matched when we cross-checked each other. 

2. **Are the functions just complex, or also long?**
All of the five functions are complex, but not necessary long, with the longest being 60 lines of code. They mostly score this high complexity through a series of if statements.

3. **What is the purpose of the functions?**
The purpose for each function is explained below it.

4. **Are exceptions taken into account in the given measurements?**
We considered `throws` as exit points, but `catch` blocks not as a decision point, as declared in the lecture. This means that exception handling does not intorduce any additional decision points and don't increase the CC count. 
Lizard does consider `catch` blocks as decision points, if we were to co the same in our manual counting, the comlexity count would increase. 


5. **Is the documentation clear w.r.t. all the possible outcomes?**
Comments regarding the documentation are below each of the functions. 

### <u>Methods</u>:

### Jonatan:
*processAnnotationForMock@33-81@./mockito-core/src/main/java/org/mockito/internal/configuration/MockAnnotationProcessor.java*
- CC reported by Lizard: 11
- CC counted by hand: 9
```java
public static Object processAnnotationForMock(
        Mock annotation, Class<?> type, Supplier<Type> genericType, String name) {
    MockSettings mockSettings = Mockito.withSettings();
    if (annotation.extraInterfaces().length > 0) { // never null +1
        mockSettings.extraInterfaces(annotation.extraInterfaces());
    }
    if ("".equals(annotation.name())) { // +1
        mockSettings.name(name);
    } else {
        mockSettings.name(annotation.name());
    }
    if (annotation.serializable()) { // +1
        mockSettings.serializable();
    }
    if (annotation.stubOnly()) { // +1
        mockSettings.stubOnly();
    }
    if (annotation.lenient()) { // +1
        mockSettings.lenient();
    }
    if (annotation.strictness() != Mock.Strictness.TEST_LEVEL_DEFAULT) { // +1
        mockSettings.strictness(Strictness.valueOf(annotation.strictness().toString()));
    }
    if (!annotation.mockMaker().isEmpty()) { // +1
        mockSettings.mockMaker(annotation.mockMaker());
    }
    if (annotation.withoutAnnotations()) { // +1
        mockSettings.withoutAnnotations();
    }

    mockSettings.genericTypeToMock(genericType.get());

    // see @Mock answer default value
    mockSettings.defaultAnswer(annotation.answer());

    if (type == MockedStatic.class) { // +1
        return Mockito.mockStatic(
                inferParameterizedType(
                        genericType.get(), name, MockedStatic.class.getSimpleName()),
                mockSettings); // -1
    } else if (type == MockedConstruction.class) { // +1
        return Mockito.mockConstruction(
                inferParameterizedType(
                        genericType.get(), name, MockedConstruction.class.getSimpleName()),
                mockSettings); // -1
    } else {
        return Mockito.mock(type, mockSettings); // -1
    }
    // π = 10, s = 3
    // M = π - s + 2 = 9
}
```
- The purpose is managing the @Mock annotation, it sets settings depending on what was passed in the annotation and creates the correct Mock (static, constructors etc.).
- The code could benefit from some extra comments, although it is written in a way that makes it relatively easy to understand. Perhaps it would be nice to explain the generics a bit more `genericTypeToMock`. 


### Elias
*reflectionEquals@223-270@./mockito-core/src/main/java/org/mockito/internal/matchers/apachecommons/EqualsBuilder.java*
- CC reported by Lizard: 12
- CC counted by hand: 7
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
- The purpose of the reflectionEquals function is to check if the lhs is equal to the right hand side and return true or false based on that.
- The documentation is not very clear w.r.t all possible outcomes, but there is some helpful comments in terms of understanding how the function works.


### Jannis:

*append@341-387@./mockito-core/src/main/java/org/mockito/internal/matchers/apachecommons/EqualsBuilder.java*
- CC reported by Lizard: 17
- CC counted by hand: 14


```java 

public EqualsBuilder append(Object lhs, Object rhs) {
   if (!isEquals) {  // +1
     return this;  // -1
   }
   if (lhs == rhs) { // +1
      return this;  // -1
   }
   if (lhs == null || rhs == null) {  // +2
      this.setEquals(false);
      return this; // -1
   }
   Class<?> lhsClass = lhs.getClass();
   if (!lhsClass.isArray()) {  // +1
      if (lhs instanceof BigDecimal && rhs instanceof BigDecimal) {  // +2
         isEquals = (((BigDecimal) lhs).compareTo((BigDecimal) rhs) == 0);
      } else {
         // The simple case, not an array, just test the element
         isEquals = lhs.equals(rhs);
      }
   } else if (lhs.getClass() != rhs.getClass()) {  // +1
      // Here when we compare different dimensions, for example: a boolean[][] to a boolean[]
      this.setEquals(false);

      // 'Switch' on type of array, to dispatch to the correct handler
      // This handles multi dimensional arrays of the same depth
   } else if (lhs instanceof long[]) {  // +1
      append((long[]) lhs, (long[]) rhs);
   } else if (lhs instanceof int[]) {  // +1
      append((int[]) lhs, (int[]) rhs);
   } else if (lhs instanceof short[]) {  // +1
      append((short[]) lhs, (short[]) rhs);
   } else if (lhs instanceof char[]) {  // +1
      append((char[]) lhs, (char[]) rhs);
   } else if (lhs instanceof byte[]) {  // +1
      append((byte[]) lhs, (byte[]) rhs);
   } else if (lhs instanceof double[]) {  // +1
      append((double[]) lhs, (double[]) rhs);
   } else if (lhs instanceof float[]) {  // +1
      append((float[]) lhs, (float[]) rhs);
   } else if (lhs instanceof boolean[]) {  // +1
      append((boolean[]) lhs, (boolean[]) rhs);
   } else {
      // Not an array of primitives
      append((Object[]) lhs, (Object[]) rhs);
   }
   return this;  // -1
   // π = 16, s = 4
   // M = π - s + 2 = 14
```
- This method is part of a builder and comapres two values ````lhs``` and ```rhs``` and appends the result to the running equality check stored in the builder. If the values are arrays, it calls the append method with the correct type, so that the arrays are compared properly, and then returns the same builder instance.
- The class itself has javadocs as documentation, which helps to understand the method. The method itself has some comments too, but an additional docstring would have been benefetial to faster understand the method and what its purpose is. 


### Arnau
*matches@17-41@./mockito-core/src/main/java/org/mockito/internal/matchers/ArrayEquals.java*
- CC reported by Lizard: 21
- CC counted by hand: 11

```java
    @Override
    public boolean matches(Object actual) {
        Object wanted = getWanted();
        if (wanted == null || actual == null) { //+1 +1 = +2
            return super.matches(actual); // -1
        } else if (wanted instanceof boolean[] && actual instanceof boolean[]) { // +1 +1 = +2
            return Arrays.equals((boolean[]) wanted, (boolean[]) actual); // -1
        } else if (wanted instanceof byte[] && actual instanceof byte[]) { // +1 +1 = +2
            return Arrays.equals((byte[]) wanted, (byte[]) actual); // -1
        } else if (wanted instanceof char[] && actual instanceof char[]) { // +1 +1 = +2
            return Arrays.equals((char[]) wanted, (char[]) actual); // -1 
        } else if (wanted instanceof double[] && actual instanceof double[]) { // +1 +1 = +2
            return Arrays.equals((double[]) wanted, (double[]) actual); // -1
        } else if (wanted instanceof float[] && actual instanceof float[]) { // +1 +1 = +2
            return Arrays.equals((float[]) wanted, (float[]) actual); // -1
        } else if (wanted instanceof int[] && actual instanceof int[]) { // +1 +1 = +2
            return Arrays.equals((int[]) wanted, (int[]) actual); // -1
        } else if (wanted instanceof long[] && actual instanceof long[]) { // +1 +1 = +2
            return Arrays.equals((long[]) wanted, (long[]) actual); // -1
        } else if (wanted instanceof short[] && actual instanceof short[]) { // +1 +1 = +2
            return Arrays.equals((short[]) wanted, (short[]) actual); // -1
        } else if (wanted instanceof Object[] && actual instanceof Object[]) { // +1 +1 = +2
            return Arrays.equals((Object[]) wanted, (Object[]) actual); // -1
        }
        return false; // -1
    }

   // π = 20, s = 11
   // M = π - s + 2 = 20 - 11 + 2 = 11
```
- The purpose of the method is to compare two objects, (**wanted** and **actual**) and determine whether they match. This method manually checks the runtime type of the objects and dispatches the comparison to the correct Arrays.equals() overload.
- There is no specific documentation provided for the matches function as the overall purpose of the function is relatively evident from the implementation itself.

### Alexander Mannertorn

*triggerRetransformation@272-331@./mockito-core/src/main/java/org/mockito/internal/creation/bytebuddy/InlineBytecodeGenerator.java*
- CC reported by Lizard: 14
- CC counted by hand: 7


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
- The purpose of the triggerRetransformation() is to collect a set of classes that needs to be reinstrumented and then call instrumentation.retransformClasses(...).
- There is no documentation specific for the triggerRetransformation() function but the code makes all possible outcomes pretty clear. 

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
