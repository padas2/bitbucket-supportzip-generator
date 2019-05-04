package com.padas2.bitbucket.supportzip;

public class TimeoutAnnotationProcessorTest {
    private void printIndefinitely() {
        System.out.println("Printing indefinitely");
    }

    public static void main(String [] args) {
        TimeoutAnnotationProcessorTest test = new TimeoutAnnotationProcessorTest();
        test.printIndefinitely();
    }
}
