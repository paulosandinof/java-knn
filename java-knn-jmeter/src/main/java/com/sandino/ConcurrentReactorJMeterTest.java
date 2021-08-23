package com.sandino;

import java.io.Serializable;

import com.sandino.concurrent.reactor.ConcurrentReactorKnn;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

public class ConcurrentReactorJMeterTest extends AbstractJavaSamplerClient implements Serializable {

    @Override
    public Arguments getDefaultParameters() {
        Arguments defaultArguments = new Arguments();

        defaultArguments.addArgument("filename", "/home/sandino/Documents/java-knn/data.csv");
        defaultArguments.addArgument("numberOfThreads", "4");
        defaultArguments.addArgument("k", "1000");

        return defaultArguments;
    }

    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {

        String filename = javaSamplerContext.getParameter("filename");
        String numberOfThreads = javaSamplerContext.getParameter("numberOfThreads");
        String k = javaSamplerContext.getParameter("k");

        ConcurrentReactorKnn concurrentReactorKnn = new ConcurrentReactorKnn(filename,
                Integer.valueOf(numberOfThreads));

        SampleResult sampleResult = new SampleResult();
        sampleResult.sampleStart();

        double[] initialRow = { 600, 600, 600, 600, 600, 600 };

        try {
            double prediction = concurrentReactorKnn.predictClassification(initialRow, Integer.valueOf(k));

            sampleResult.sampleEnd();
            sampleResult.setResponseCode("200");
            sampleResult.setResponseMessage("Prediction: " + prediction);
            sampleResult.setSuccessful(true);
        } catch (Exception e) {
            sampleResult.sampleEnd();
            sampleResult.setResponseCode("500");
            sampleResult.setResponseMessage("Exception: " + e.getMessage());
            sampleResult.setSuccessful(false);
        }

        return sampleResult;
    }
}
