# java-knn

Number of lines: 5095270

## Run JCStress
```bash
java -jar java-knn-jcstress/target/jcstress.jar -r java-knn-jcstress/reports
```

## Run JMeter

- Copy tests to JMeter folder
```bash
cp java-knn-jmeter/target/java-knn-jmeter-1.0.jar $JMETER_HOME/lib/ext/
```

- Copy dependencies to JMeter folder
```bash
cp java-knn-application/target/java-knn-application-1.0.jar $JMETER_HOME/lib/ext/
```
