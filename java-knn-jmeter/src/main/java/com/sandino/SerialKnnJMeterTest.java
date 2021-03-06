package com.sandino;

import java.io.Serializable;

import com.sandino.serial.SerialKnn;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

public class SerialKnnJMeterTest extends AbstractJavaSamplerClient implements Serializable {

    @Override
    public Arguments getDefaultParameters() {
        Arguments defaultArguments = new Arguments();

        defaultArguments.addArgument("filename", "/home/sandino/Documents/java-knn/data.csv");
        defaultArguments.addArgument("k", "1000");

        return defaultArguments;
    }

    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {

        String k = javaSamplerContext.getParameter("k");
        String filename = javaSamplerContext.getParameter("filename");

        SerialKnn serialKnn = new SerialKnn(filename);

        SampleResult sampleResult = new SampleResult();
        sampleResult.sampleStart();

        double[] initialRow = { 600, 600, 600, 600, 600, 600 };

        try {
            double prediction = serialKnn.predictClassification(initialRow, Integer.valueOf(k));

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
