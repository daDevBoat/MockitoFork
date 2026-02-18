# Report for assignment 3

## Project

Name: **mockito**

URL: https://github.com/mockito/mockito


Mockito is an open-source Java mocking framework that allows developers to create mock objects and verify interactions in unit tests, improving test readability and maintainability. [more info](https://site.mockito.org/)


## Onboarding experience

### Did it build and run as documented?

Mockito is the project we have chosen to work on.

Initially, we explored several projects. Specifically, we looked for projects that were mainly implemented in java, included an automated tool for running tests and contained functions with relatively high complexity, and a clear documentation on how to compile and run the project. 

The projects we looked before deciding to choose Mockito, lacked one or more of this criteria. In addition, we considered important that the project had well-documented contribution guidelines and a sufficient number of open issue to work on in the next assigment.

For mockito, building the project was not entirely straightforward at the beginning, because all the team members have the most recent version of Java, and in order to compile, we needed to downgrade to Java 17. But first, we had to figure out which Java version to use. We tried Java 11, which matches the target language of the project, but it was too low for the Gradle version used in the repository.

The repository already includes the Gradle Wrapper, so there was no need to install Gradle manually. Running `./gradlew build` and `./gradlew test` was not a problem.

We consider that the required tools are well documented, with clear explanations on how to build and test the project.

Gradle automatically downloaded and installed the necessary project dependencies. No additional dependencies had to be installed manually beyond having Java 17 available.

Once we had Java 17 properly configured, the build completed successfully without errors and all the tests run successfully.
    
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
        if (lhs == rhs) { // +1
            return true; // -1
        }
        if (lhs == null || rhs == null) { // +2
            return false;   // -1
        }
        // Find the leaf class since there may be transients in the leaf
        // class or in classes between the leaf and root.
        // If we are not testing transients or a subclass has no ivars,
        // then a subclass can test equals to a superclass.
        Class<?> lhsClass = lhs.getClass();
        Class<?> rhsClass = rhs.getClass();
        Class<?> testClass;
        if (lhsClass.isInstance(rhs)) {     // +1
            testClass = lhsClass;
            if (!rhsClass.isInstance(lhs)) {    // +1
                // rhsClass is a subclass of lhsClass
                testClass = rhsClass;
            }
        } else if (rhsClass.isInstance(lhs)) {  // +1
            testClass = rhsClass;
            if (!lhsClass.isInstance(rhs)) {    // +1
                // lhsClass is a subclass of rhsClass
                testClass = lhsClass;
            }
        } else {
            // The two classes are not related.
            return false;   // -1
        }
        EqualsBuilder equalsBuilder = new EqualsBuilder();
        if (reflectionAppend(lhs, rhs, testClass, equalsBuilder, testTransients, excludeFields)) {  // +1
            return false;   // -1
        }
        while (testClass.getSuperclass() != null && testClass != reflectUpToClass) { // +2
            testClass = testClass.getSuperclass();
            if (reflectionAppend(
                    lhs, rhs, testClass, equalsBuilder, testTransients, excludeFields)) {   // +1
                return false;   // -1
            }
        }
        return equalsBuilder.isEquals();    // -1
    }
    // π = 11, s = 6
    // M = π - s + 2 = 7
``` 
- The purpose of the reflectionEquals function is to check if the lhs object is equal to the rhs object and return true or false based on that.
- The documentation is not very clear w.r.t all possible outcomes, but there is some helpful comments and javadocs in terms of understanding how the function works.


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

### Elias
The plan for refactoring the reflectionEquals is to extract section of code that determines the common test class and put that in its own function. If the goal is to reduce CC count one could as well split if with || (OR) opperators into separate if statements. If I were to estimate the impact of executing this refactoring plan it would be lower the CC, and hopefully make it more readable. 

The actual implementation can be found on this [link](https://github.com/daDevBoat/MockitoFork/commit/d4078c4d193115ceee5f02f2cace77eb0fb625a3)

### Jonatan
To refactor the `processAnnotationForMock` function, I extracted the common theme of having an if statement and then running one single line of code into a function that was called instead, does precisely that. This reduces the amount of `if`-statements drastically, and thus also the CC count, which was reduced from 11 in lizard to 4 (manual count was 9->1).

Implementation is [here](https://github.com/daDevBoat/MockitoFork/commit/6a8a2257123bc656f6860c03a29ac8c1c8c11328)


### Jannis
The `append` method consists of a first part that checks for flags, null value and if the class is an array. The second part consists of a row of `else if` statements that depending on the type, call a different append method. The plan to reduce CC is, to split the original method into two parts. The first part remained in the `append` method, while the second part was moved the `appendByType` method. The CC changed the following (original -> append & appendByType).
- Lizard: 17 -> 9 & 9
- By hand: 14 -> 7 & 9

Implementation can be found [here](https://github.com/daDevBoat/MockitoFork/commit/75f1d82b0965717a27f567667d331499c3e55530)

### Arnau

For the `matches(Object)` function, the cyclomatic complexity of `11` in the original `matches(Object)` function is not truly necessary. The complexity arises from having 10 consecutive `if/else if` branches, all following the exact same pattern: first, check the type of both `wanted` and `actual`, then delegate to `Array.equals()`.

It is possible to split the code into two smaller units to reduce complexity, by following the following refactoring plan:

The plan consists of applying an `Extract Method` refactoring pattern, by extracting the primitive array comparison logic into a dedicated helper method named `matchesPrimitiveArray(Object wanted, Object actual)`.

**Following and implementing the refactoring plan:**
- By hand, the cyclomatic complexity is reduced from `11` to `3`.
- With lizard, the cyclomatic complexity is reduced from `21` to `6`.

**Implementation of the refactoring can be found here:**
[ExtractMethod_refactor_matches](https://github.com/daDevBoat/MockitoFork/commit/2a4fcf36649b83f2324935883e3489de7e018a77)

## Coverage

### Tools

#### Document your experience in using a "new" different coverage tool.

We used `JaCoCo` as the automated coverage tool. In the Mockito repository, JaCoCo is already integrated into the Gradle build configuration, so no additional setup or installation was required.

Our experience was very positive. It was straightforward to generate a coverage report by running `./gradlew clean test jacocoTestReport`. 

We found that running `clean` before executing the tests ensured that the coverage report was properly updated.

After the execution, the report was generated automatically. By opening the `index.html` file located a the following path: `build/reports/jacoco/test/html/`, we were able to navigate through the project structure and inspect the specific functions we had instrumented.

The report clearly highlights coverage using `green` for fully covered code, `yellow` for partially covered branches, and `red` for uncovered code.

### Your own coverage tool

Show a patch (or link to a branch) that shows the instrumented code to
gather coverage measurements.

[Own coverage tool](https://github.com/daDevBoat/MockitoFork/blob/code-coverage-tool/mockito-core/src/main/java/org/CoverageRecorder.java)

- Arnau:  [matches() function](https://github.com/daDevBoat/MockitoFork/commit/62dc3981f76f53a9041b012a11aba6078093b2f6)
- Elias:   [reflectionEquals()](https://github.com/daDevBoat/MockitoFork/commit/4f98296824128b2cc12db14c66875086df066c0d)
- Jannis: [append](https://github.com/daDevBoat/MockitoFork/commit/b4f5e7f5ec69eca6fa0f0e3c93387bf6c9b701ab)
- Jonatan [processAnnotationForMock](https://github.com/daDevBoat/MockitoFork/commit/ff57751306c2bbf1941d3203d5a9c27c05ea27f2#diff-04ea5ad3f5c1df787656eb2cd92305a86b2e48bf99455b9fb14260ee4a6a6424)
- Alexander

### Evaluation

#### 1. How detailed is your coverage measurement?

Our manual coverage tool measures branch coverage by explicitly instrumenting selected control-flow structures in the source code. For each branch, we: 

1. Manually assign a unique ID.
2. Register all possible branch IDs before execution `CoverageRecorder.register(Ids)`.
3. Insert a `CoverageRecorder.mark(ID)` call at the beginning of each branch outcome.
4. Generate a report at the end of execution showing the covered and uncovered branches.

In our implementation, we decided to define a branch as: 
- Each outcome of an `if-else` statement. 
- Each loop construct (`for and while`) 

This means our coverage measurement is detailed whithin the scope we defined. However, we do not consider:

- Ternary operators (`condition ? yes : no`) 
- Short-circuit logical operators (`&&`, `||`)
- Exception-handling control flow. 


#### 2. What are the limitations of your own tool?

The main limitations of our manual instrumentation are related to maintainability and completeness.

First, it requires manual modification of the source code. Any change in control flow (for example, adding a new `else if` branch) requires updating the assign of the branch IDs.

Second, the tool is error-prone, if we missed to mark a branch, the coverage report becomes inaccurate.

If we modify the program, by adding a new else if branch, we must:
1. Assign a new unique branch ID.
2. Register it in CoverageRecorder.register();
3. Insert a corresponding. CoverageRecorder.mark().


#### 3. Are the results of your tool consistent with existing coverage tools?

For the types of branches we explicitly decided to measure (i.e., `if-else` statements and loop constructs such as `for` and `while`), our manual coverage results are consistent with those reported by the automated coverage tool that we are using, `JaCoCo`. In this cases, both approaches are consistent, as they identify the same branch outcomes as covered or uncovered.

`JaCoCo` provides a more precise and complete branch coverage measurment as it performs bytecode-level instrumentation and therefore detects additional branches that we do not take into account. So our tool is consistent within the scope we have defined, but it does not capture all the possible branch outcomes that `JaCoCo` reports.

## Coverage improvement
For every test introduced we mention the TR that needed to be fullfilled to improve branch coverage.

A more elaborate report on the tests can be found in the `linked commit` and the `comment` attached to it. Changes in the coverage can also be found in the attached comment.

### Arnau - matches(Object)

Report of new coverage (for P): [tests_n1_n2](https://github.com/daDevBoat/MockitoFork/commit/542ef4482cf9fdc4a90cdeb90e5809932c06c1bd)

Report of new coverage (for P+): [tests_n3_n4](https://github.com/daDevBoat/MockitoFork/commit/703294937254d520f75cfa09c1219e9bfcb88657)

1) TR1: matches(Object) must return false when wanted and actual are primitive arrays of different types, for example: int[] vs long[], even if numeric values are equivalent.

2) TR2: matches(Object) must enter the branch when wanted != null and actual == null, and must return false via super.matches(actual).

3) TR3: matches(Object), if wanted is boolean[] but actual is not boolean[], the boolean[] && boolean[] condition must return false.

4) TR4: matches(Object), if wanted is Object[] but actual is primitive array, the Object[] && Object[] condition must return false.

### Jannis
Commit of the 4 test, including coverage before and after: [GitHub](https://github.com/daDevBoat/MockitoFork/commit/7821a71ebc1eaf9370126713b7a69e5a6ff650ad)


1) TR: The append(object, object) method sets the `isEquals` flag to false if only one of the two objects is of type BigDecimal 

2) TR: The append(object, object) method sets the `isEquals` flag to false if both objects are of type BigDecimal, but have different values.

3) TR: The append(char [], char[]) method sets the `isEquals` flag to false if both objects are of type char but have different lengths.

4) TR: The append(char [], char[]) method sets the `isEquals` flag to false if both objects are of type char but have different lengths.

### Jonatan
Commit of the 4 tests, including coverage before and after as a comment on the commit: [Github](https://github.com/daDevBoat/MockitoFork/commit/87ec5759be86e9fc329ef8c44fbbbd1217518b7b#commitcomment-177545435)

1. TR: The mockMaker passed to the @Mock annotation is non-empty and set as specified.
2. TR: A mockMaker is not passed to the @Mock annotation (i.e. it is empty), the Mock is still performed without setting an explicit mockMaker.
3. TR: If a static class is mocked, the returned mock is of type MockedStatic and basic mock functionality (as mocking a function) works as expected.
4. TR: If the constructor of a class is mocked, the returned mock is of type MockedConstruction and any calls to the constructors are mocked as expected.

### Elias 
Commit of the 6 (2 + 4) test for reflectEquals and append(int[], int[]), including coverage before and after: [GitHub](https://github.com/daDevBoat/MockitoFork/commit/d461ef64ba056126e9274abb53a4b0bea8ce272a)

1. TR: If two objects are identical instances of the same class with the same field values, reflectionEquals returns true; if at least one field value differs, it returns false.
2. TR: If reflectUpToClass is the same as the runtime class of the compared objects, the while loop condition (testClass != reflectUpToClass) evaluates to false and the loop does not execute.
3. TR: If two int[] arrays passed to append(int[], int[]) have different lengths, the method returns the same EqualsBuilder instance without marking the arrays as equal.
4. TR: If append(int[], int[]) is invoked when the builder is already in a non-equal state or when one of the arrays is null, the method returns the same EqualsBuilder instance without changing the equality result.
5. TR: If two int[] arrays passed to append(int[], int[]) contain different element values, the method returns the same EqualsBuilder instance and the equality state reflects that the arrays are not equal.
6. TR: If one of the int[] arrays passed to append(int[], int[]) is null, the method returns the same EqualsBuilder instance and treats the arrays as not equal.

### Alexander


## Self-assessment: Way of working
When comparing our way-of-working to the Essence checklist, we are in state `In-Place`. Looking at the checklist, we fulfill all the points prior to the state `Working-well`. 

In the first assignment, we evaluated ourselves to be in the state `In-Use`, so we have improved one state so far. The way of working became easier to use and supported us more and more throughout the course. Structured commits, organized issues, and meaningful commit messages enable us to work more effectively.

To continue in the next state, `Working-well`, we are missing the continuous tuning of our way of working and our tools. We could also improve in how naturally we use and apply our way-of-working. This was also the only point of discussion, as for some of us it feels more natural than for others.


## Overall experience

What are your main take-aways from this project? What did you learn?

Is there something special you want to mention here?
