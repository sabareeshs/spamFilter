package classify.spam;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import classify.general.*;
import util.*;

public class Tester {

  private static Classifier classifier = new AlwaysPositiveClassifier();
  private static EmailMessageFeatureExtractor fextractor = new UnigramFeatureExtractor();
  
  private static double threshold = 0.0;

  private static List<EmailMessage> trainMessages = new ArrayList<EmailMessage>(),
      testMessages = new ArrayList<EmailMessage>();

  private static boolean dumpMessages = false;
  private static boolean useThreshold = false;

  public static void trainAndTestClassifier() {
    if (trainMessages != null && testMessages != null) {
      List<Example> trainingData = new ArrayList<Example>();
      System.err.print("Extracting features and labels from training messages...");
      for (EmailMessage message : trainMessages) {
        trainingData.add(new Example(message.getLabel(), fextractor.extractFeatures(message)));
      }
      System.err.println("DONE!");
      classifier.train(trainingData);
      System.err.print("Testing classifier...");
      testClassifier();
    }
  }

  private static void testClassifier() {
    List<EmailMessage> isSpamLabeledSpam = new ArrayList<EmailMessage>();
    List<EmailMessage> isGenLabeledGen = new ArrayList<EmailMessage>();
    List<EmailMessage> isGenLabeledSpam = new ArrayList<EmailMessage>();
    List<EmailMessage> isSpamLabeledGen = new ArrayList<EmailMessage>();
    for (EmailMessage message : testMessages) {
      Example testInstance = new Example(null, fextractor.extractFeatures(message));
      String trueLabel = message.getLabel();
      Object proposedLabel = EmailMessage.labelSpam;
      if(useThreshold) {
        Counter<Object> scores = classifier.getLabelScores(testInstance);
        if(classifier instanceof classify.student.NaiveBayesClassifier) {
          scores = scores.logNormalize();
          scores = scores.exp();
        }
        double score = scores.getCount(EmailMessage.labelGenuine);
        if (score > threshold) {
          proposedLabel = EmailMessage.labelGenuine;
        }
      } else {
        proposedLabel = classifier.getLabel(testInstance);
      }
      if (trueLabel.equals(EmailMessage.labelSpam)) {
        if (proposedLabel.equals(trueLabel)) {
          isSpamLabeledSpam.add(message);
        } else {
          isSpamLabeledGen.add(message);
        }
      } else {
        if (proposedLabel.equals(trueLabel)) {
          isGenLabeledGen.add(message);
        } else {
          isGenLabeledSpam.add(message);
        }
      }
    }
    System.err.println("DONE!");
    System.err.println("Confusion Matrix:");
    System.err.println("           GUESSES");
    System.err.println("           SPAM      GENUINE     TOTAL");
    System.err.println("SPAM       " + isSpamLabeledSpam.size() + "       " + isSpamLabeledGen.size() + "       " + (isSpamLabeledSpam.size() + isSpamLabeledGen.size()));
    System.err.println("GENUINE    " + isGenLabeledSpam.size() + "       " + isGenLabeledGen.size() + "       " + (isGenLabeledSpam.size() + isGenLabeledGen.size()));
    System.err.println("TOTAL      " + (isSpamLabeledSpam.size() + isGenLabeledSpam.size()) + "       " + (isSpamLabeledGen.size() + isGenLabeledGen.size()) + "       " + testMessages.size());
    System.err.println();
    
    // compute precision
    double numGuessedGen = (double) (isGenLabeledGen.size() + isSpamLabeledGen.size());
    double precision = 0.0;
    if (numGuessedGen > 0.0) {
      precision = (double) isGenLabeledGen.size() / numGuessedGen;
    } 
    System.err.println("PRECISION (on GEN):\t" + precision);      
    
    // compute recall
    double numActualGen = (double) (isGenLabeledGen.size() + isGenLabeledSpam.size());
    double recall = 0.0;
    if (numActualGen > 0) {
      recall = (double) isGenLabeledGen.size() / numActualGen;
    } 
    System.err.println("RECALL (on GEN):\t" + recall);
    
    // compute F1 measure
    double f1 = (2*precision*recall) / (precision+recall);
    System.err.println("F1 (on GEN):\t\t" + f1);
    
//  compute precision
    double numGuessedSpam = (double) (isSpamLabeledSpam.size() + isGenLabeledSpam.size());
    precision = 0.0;
    if (numGuessedGen > 0.0) {
      precision = (double) isSpamLabeledSpam.size() / numGuessedSpam;
    } 
    System.err.println("PRECISION (on SPAM):\t" + precision);      
    
    // compute recall
    double numActualSpam = (double) (isSpamLabeledGen.size() + isSpamLabeledSpam.size());
    recall = 0.0;
    if (numActualGen > 0) {
      recall = (double) isSpamLabeledSpam.size() / numActualSpam;
    } 
    System.err.println("RECALL (on SPAM):\t" + recall);
    
    // compute F1 measure
    f1 = (2*precision*recall) / (precision+recall);
    System.err.println("F1 (on SPAM):\t\t" + f1);
    
    if (dumpMessages) {
      try {
        dumpMessageContentsToDisk(isSpamLabeledSpam, isGenLabeledSpam, isGenLabeledGen, isSpamLabeledGen);
      } catch (IOException ioe) {
        System.err.println("ERROR writing debug files: " + ioe.toString());
      }
    }
  }

  private static void dumpMessageContentsToDisk(List<EmailMessage> isSpamLabeledSpam,
                                                List<EmailMessage> isGenLabeledSpam,
                                                List<EmailMessage> isGenLabeledGen,
                                                List<EmailMessage> isSpamLabeledGen) throws IOException {
    System.err.print("\nDumping message contents to disk (files end in .debug)...");
    BufferedWriter out = new BufferedWriter(new FileWriter("isSpamLabeledSpam.debug"));
    out.write("Messages correctly labeled as SPAM: " + isSpamLabeledSpam.size() + "\n");
    for (EmailMessage message : isSpamLabeledSpam) {
      out.write(message.toString());
    }
    out.close();

    out = new BufferedWriter(new FileWriter("isGenLabeledGen.debug"));
    out.write("Messages correctly labeled as GENUINE: " + isGenLabeledGen.size() + "\n");
    for (EmailMessage message : isGenLabeledGen) {
      out.write(message.toString());
    }
    out.close();

    out = new BufferedWriter(new FileWriter("isGenLabeledSpam.debug"));
    out.write("Messages falsely labeled as SPAM: " + isGenLabeledSpam.size() + "\n");
    for (EmailMessage message : isGenLabeledSpam) {
      out.write(message.toString());
    }
    out.close();

    out = new BufferedWriter(new FileWriter("isSpamLabeledGen.debug"));
    out.write("Messages falsely labeled as GENUINE: " + isSpamLabeledGen.size() + "\n");
    for (EmailMessage message : isSpamLabeledGen) {
      out.write(message.toString());
    }
    out.close();
    System.err.println("DONE!");
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    Map<String, Object> argMap = Utils.parseCommandLineArguments(args, true);
    if (argMap.containsKey("-classifier")) {
      String classifierName = (String) argMap.get("-classifier");
      classifier = (Classifier) Utils.getNewObjectByName(classifierName,
                                                         new Class[0],
                                                         new Object[0]);
    }
    System.err.println("CLASSIFIER:\t\t" + classifier.getClass().getName());
    if(classifier instanceof BinaryClassifier) {
      BinaryClassifier bc = (BinaryClassifier)classifier;
      bc.setPositiveLabel(EmailMessage.labelGenuine);
      System.err.println("Set " + bc.getClass().getName() + ".positiveLabel to " + bc.getPositiveLabel().toString());
      bc.setNegativeLabel(EmailMessage.labelSpam);
      System.err.println("Set " + bc.getClass().getName() + ".negativeLabel to " + bc.getNegativeLabel().toString());
    }
    if (argMap.containsKey("-fextractor")) {
      String fextractorName = (String) argMap.get("-fextractor");
      fextractor = (EmailMessageFeatureExtractor) Utils.getNewObjectByName(fextractorName, new Class[0],
                                                               new Object[0]);
    }
    System.err.println("FEATURE EXTRACTOR:\t" + fextractor.getClass().getName());
    if (argMap.containsKey("-train")) {
      String trainFilename = (String) argMap.get("-train");
      File trainFile = new File(trainFilename);
      System.err.println("TRAIN FROM:\t\t" + trainFile.getAbsolutePath());
      System.err.print("reading training messages...");
      trainMessages = GenSpam.readOneGenSpamFile(trainFile);
      System.err.println("DONE!");
    } else {
      System.err.println("ERROR: You must provide a file of training messages with the -train option");
    }
    if (argMap.containsKey("-test")) {
      String testFilename = (String) argMap.get("-test");
      File testFile = new File(testFilename);
      System.err.println("TEST FROM:\t\t" + testFile.getAbsolutePath());
      System.err.print("reading test messages...");
      testMessages = GenSpam.readOneGenSpamFile(testFile);
      System.err.println("DONE!");
    } else {
      System.err.println("ERROR: You must provide a file of test messages with the -test option");
    }
    if (argMap.containsKey("-threshold")) {
      useThreshold = true;
      threshold = (Double) argMap.get("-threshold");
      System.err.println("USING THRESHOLD=" + threshold + " to classify GENUINE");
    }
    if (argMap.containsKey("-debug")) {
      dumpMessages = true;
      System.err.println("WILL DUMP TEST MESSAGE CONTENTS TO DISK");
    }
    trainAndTestClassifier();
  }
}
