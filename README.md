# Java KNN

This repository is a study for comparing different threading paradigms in Java.

The K-nearest neighbor algorithm was used to process a dataset with the information about grades of the Brazilian National Exam. The main goal is predict the average income of a student based on a its grades in the exam.

The algorithm was implemented in a serial way, and converted to differents versions using traditional Java threads with mutexes, busy wait with atomic variables, Java executors, callables and futures, fork join framework, reactor and Apache Spark.

All the different implementations were tested using the Java Microbenchmark Harness, JCStress and Apache JMeter.

You can run the codes and the JMH tests normally on your IDE.

## To Run JCStress Tests
```bash
java -jar java-knn-jcstress/target/jcstress.jar -r java-knn-jcstress/reports
```

## To Run JMeter Tests

- Copy tests to JMeter folder
```bash
cp java-knn-jmeter/target/java-knn-jmeter-1.0.jar $JMETER_HOME/lib/ext/
```

- Copy dependencies to JMeter folder
```bash
cp java-knn-application/target/java-knn-application-1.0.jar $JMETER_HOME/lib/ext/
```
