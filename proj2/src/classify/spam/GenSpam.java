package classify.spam;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import util.Pair;
import util.Utils;
import util.Interner;

public class GenSpam {

  public static final String MESSAGE_XML_START = new String("<MESSAGE "),
      MESSAGE_XML_END = new String("</MESSAGE ");
  
  public static Interner<String> interner = new Interner<String>();

  public static List<EmailMessage> readOneGenSpamFile(File file) {
    List<EmailMessage> messages = new ArrayList<EmailMessage>();
    try {
      BufferedReader in = new BufferedReader(new FileReader(file));
      String line;
      int numRead = 0;
      while ((line = in.readLine()) != null) {
        if (line.startsWith(MESSAGE_XML_START)) {
          boolean isSpam = line.contains("SPAM");
          List<String> messageLines = new ArrayList<String>();
          while ((line = in.readLine()) != null && !line.startsWith(MESSAGE_XML_END)) {
            messageLines.add(line);
          }
          EmailMessage message = buildEmailMessage(messageLines);
          if (isSpam) {
            message.setLabel(EmailMessage.labelSpam);
          } else {
            message.setLabel(EmailMessage.labelGenuine);
          }
          messages.add(message);
          numRead++;
          if (numRead%1000 == 0) System.err.println("Read " + numRead + " messages.");
        }
      }
      in.close();
    } catch (IOException ie) {
      System.err.println("Error reading file " + file + ": " + ie.toString());
    }
    return messages;
  }

  public static final String DATE_XML_START = new String("<DATE>"),
      DATE_XML_END = new String("</DATE>");

  public static final String FROM_XML_START = new String("<FROM>"),
      FROM_XML_END = new String("</FROM>");

  public static final String TO_XML_START = new String("<TO>"), TO_XML_END = new String("</TO>");

  public static final String SUBJECT_XML_START = new String("<SUBJECT>"),
      SUBJECT_XML_END = new String("</SUBJECT>");

  public static final String BODY_XML_START = new String("<MESSAGE_BODY>"),
      BODY_XML_END = new String("</MESSAGE_BODY>");

  private static void convertOneGenSpamFile(String filename, String label) throws IOException {
    if(!EmailMessage.isValidLabel(label)){
      throw new RuntimeException("Invalid label: " + label);
    }
    String convertFilename = filename + ".cs121";
    BufferedReader in = new BufferedReader(new FileReader(filename));
    BufferedWriter out = new BufferedWriter(new FileWriter(convertFilename));
    String line;
    while ((line = in.readLine()) != null) {
      if (line.equals(MESSAGE_XML_START) || line.equals(MESSAGE_XML_END)) {
        line = line.replaceAll(">", " " + label.toString() + ">");
        // line = line.substring(0, line.length()-1) + " " + label.toString() +
        // ">";
      }
      out.write(line + "\n");
    }
    out.close();
    in.close();
    System.err.println("Conversion successful, stored in " + convertFilename);
  }

  private static EmailMessage buildEmailMessage(List<String> lines) {
    EmailMessage message = new EmailMessage();
    boolean readingBody = false, readingSubject = false, readingFrom = false;
    List<Pair<String, Integer>> body = new ArrayList<Pair<String, Integer>>(), subject = new ArrayList<Pair<String, Integer>>();
    List<String> from = new ArrayList<String>();
    int embeddingLevel = 0;
    for (String line : lines) {
      if (line.length() <= 0) {
        continue;
      }
      if (line.startsWith("<TEXT_NORMAL>") || line.startsWith("</TEXT_NORMAL>")) {
        embeddingLevel = 0;
      } else if (line.startsWith("<TEXT_EMBEDDED>") && embeddingLevel < 4) {
        embeddingLevel++;
      } else if (line.startsWith("</TEXT_EMBEDDED>") && embeddingLevel > 0) {
        embeddingLevel--;
      } else if (readingFrom) {
        if (line.startsWith(FROM_XML_END)) {
          readingFrom = false;
          message.setFrom(from);
          from = new ArrayList<String>();
        } else {
          from.add(interner.intern(line));
        }
      } else if (readingSubject) {
        if (line.contains(SUBJECT_XML_END)) {
          readingSubject = false;
          message.setSubject(subject);
          subject = new ArrayList<Pair<String, Integer>>();
        } else {
          // TODO: do I want to remove these: "^ "?
          String[] tokens = line.split("\\s+");
          for (int i = 1; i < tokens.length; i++) {
            subject.add(new Pair<String, Integer>(interner.intern(tokens[i]), embeddingLevel));
          }
        }
      } else if (readingBody) {
        if (line.contains(BODY_XML_END)) {
          readingBody = false;
          message.setBody(body);
          body = new ArrayList<Pair<String, Integer>>();
        } else {
          // TODO: do I want to remove these: "^ "?
          String[] tokens = line.split("\\s+");
          for (int i = 1; i < tokens.length; i++) {
            body.add(new Pair<String, Integer>(interner.intern(tokens[i]), embeddingLevel));
          }
        }
      } else if (line.contains(DATE_XML_START)) {
        String[] tokens = line.split("\\s+");
        // System.err.println("DATE: " + line.toString());
        switch (tokens.length) {
          case 3:
            message.setTime(interner.intern(tokens[1]));
            break;
          case 8:
            message.setDayOfWeek(interner.intern(tokens[1].substring(0, tokens[1].length() - 1)));
            message.setDate(interner.intern(tokens[2]));
            message.setMonth(interner.intern(tokens[3]));
            message.setYear(interner.intern(tokens[4]));
            message.setTime(interner.intern(tokens[5]));
          default:
            break;
        }
      } else if (line.contains(TO_XML_START)) {
        String[] tokens = line.split("\\s+");
        List<String> to = new ArrayList<String>();
        for (int i = 1; i < tokens.length - 1; i++) {
          to.add(interner.intern(tokens[i]));
        }
        message.setTo(to);
      } else if (line.contains(FROM_XML_START)) {
        from = new ArrayList<String>();
        String[] tokens = line.split("\\s+");
        if (tokens.length <= 1) {
          readingFrom = true;
        } else {
          for (int i = 1; i < tokens.length - 1; i++) {
            from.add(interner.intern(tokens[i]));
          }
          message.setFrom(from);
        }
      } else if (line.contains(SUBJECT_XML_START)) {
        subject = new ArrayList<Pair<String, Integer>>();
        embeddingLevel = 0;
        readingSubject = true;
      } else if (line.contains(BODY_XML_START)) {
        body = new ArrayList<Pair<String, Integer>>();
        embeddingLevel = 0;
        readingBody = true;
      }
    }
    return message;
  }

  public static void main(String[] args) {
    Map<String, Object> argMap = Utils.parseCommandLineArguments(args, true);
    if (argMap.containsKey("-test")) {
      String testFilename = (String) argMap.get("-test");
      List<EmailMessage> messages = GenSpam.readOneGenSpamFile(new File(testFilename));
      for (EmailMessage message : messages) {
        System.err.println(message.toString());
      }
    }
    if (argMap.containsKey("-convert")) {
      String filenameList = (String) argMap.get("-convert");
      String[] filenames = filenameList.split(",");
      for (String filename : filenames) {
        String label = EmailMessage.labelGenuine;
        if (filename.contains("SPAM")) {
          label = EmailMessage.labelSpam;
        }
        try {
          GenSpam.convertOneGenSpamFile(filename, label);
        } catch (IOException ioe) {
          System.err.println("Conversion of " + filename + " failed.");
        }
      }
    }
  }
}
