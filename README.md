# Interprocedural Sign Analysis

This project implements a static sign analysis for Java bytecode. It follows
the possible signs of values through method calls and reports potential
runtime problems, such as division by zero and invalid array accesses.

The analysis is based on a sign lattice and transfer relations for the
operations encountered while analysing a method. It can also inspect calls to
other methods in the analysed class.

## Requirements

- Java 21
- Maven

## Build and test

Run the test suite with:

```bash
mvn clean test
```

Create the executable JAR with:

```bash
mvn package
```

The resulting file is `target/sign-analysis.jar`.

## Running the analysis

The command requires:

- `--class` (`-c`): the fully qualified name of the class to analyse
- `--method` (`-m`): the method signature from which the analysis starts

For example:

```bash
java -jar target/sign-analysis.jar ^
  --class de.uni_passau.fim.se2.sa.examples.PublicFunctional ^
  --method "add:()I"
```

On macOS or Linux, replace the Windows line-continuation character (`^`)
with a backslash (`\`), or run the command on one line.

The analysis prints warnings and errors with their corresponding source line
numbers. If no problems are found, it prints:

```text
No warnings or errors found
```

Other methods from the example class can be analysed by changing the method
signature. For example:

```bash
java -jar target/sign-analysis.jar ^
  --class de.uni_passau.fim.se2.sa.examples.PublicFunctional ^
  --method "div:()I"
```

## Project structure

```text
src/               Analysis implementation and example classes
test/              Unit and property-based tests
expected-results/  Expected results for example analyses
pom.xml            Maven build configuration
```

## Main components

- `SignAnalysisImpl` coordinates the analysis.
- `SignInterpreter` evaluates bytecode instructions using sign information.
- `SignLattice` represents the abstract sign values.
- `SignTransferRelation` defines how operations change abstract values.
