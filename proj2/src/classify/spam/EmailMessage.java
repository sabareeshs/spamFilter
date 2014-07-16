package classify.spam;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.LinkedHashSet;

import util.Pair;

public class EmailMessage {

  public static final String labelSpam = "SPAM",
                      labelGenuine = "GENUINE",
                      labelUnknown = "UNKNOWN";
  
  public static boolean isValidLabel(String label) {
    return(label.equals(labelSpam) || label.equals(labelGenuine)
        || label.equals(labelUnknown));
  }
  
  public static Set<String> getValidLabels() {
    Set<String> validLabels = new LinkedHashSet<String>();
    validLabels.add(labelSpam);
    validLabels.add(labelGenuine);
    validLabels.add(labelUnknown);
    return validLabels;
  }
  
  private String label;

  private String date, month, year;

  private String dayOfWeek, time;

  private List<String> from, to;

  private List<Pair<String, Integer>> body, subject;

  public EmailMessage() {
    label = labelUnknown;
    date = new String();
    month = new String();
    year = new String();
    dayOfWeek = new String();
    time = new String();
    from = new ArrayList<String>();
    to = new ArrayList<String>();
    body = new ArrayList<Pair<String, Integer>>();
    subject = new ArrayList<Pair<String, Integer>>();
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
     if(!isValidLabel(label)){
       throw new RuntimeException("Invalid label: " + label);
    } else {
      this.label = label;
    }
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public List<String> getFrom() {
    return from;
  }

  public void setFrom(List<String> from) {
    this.from = from;
  }

  public List<String> getTo() {
    return to;
  }

  public void setTo(List<String> to) {
    this.to = to;
  }

  public List<Pair<String, Integer>> getBody() {
    return body;
  }

  public void setBody(List<Pair<String, Integer>> body) {
    this.body = body;
  }

  public List<Pair<String, Integer>> getSubject() {
    return subject;
  }

  public void setSubject(List<Pair<String, Integer>> subject) {
    this.subject = subject;
  }

  public String getDayOfWeek() {
    return dayOfWeek;
  }

  public void setDayOfWeek(String dayOfWeek) {
    this.dayOfWeek = dayOfWeek;
  }

  public String getMonth() {
    return month;
  }

  public void setMonth(String month) {
    this.month = month;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  private static int maxWordsPerLine = 10;

  public String toString() {
    String messageString = new String("<MESSAGE " + label + ">\n");
    messageString += ("DATE:\t" + dayOfWeek + " " + date + " " + month + " " + year + " " + time + "\n");
    messageString += ("FROM:\t" + from + "\n");
    messageString += ("  TO:");
    for (String str : to) {
      messageString += ("\t" + str + "\n");
    }
    messageString += "SUBJ:";
    int lastEmbeddingLevel = -1, wordsOnLine = 0;
    for (Pair<String, Integer> pair : subject) {
      if (pair.second() != lastEmbeddingLevel) {
        lastEmbeddingLevel = pair.second();
        messageString += "\n";
        wordsOnLine = maxWordsPerLine;
      }
      if (wordsOnLine >= maxWordsPerLine) {
        wordsOnLine = 0;
        messageString += "\n\t";
        for (int i = 0; i < lastEmbeddingLevel; i++) {
          messageString += "> ";
        }
      }
      messageString += (" " + pair.first());
      wordsOnLine++;
    }
    messageString += "\nBODY:";
    lastEmbeddingLevel = 0;
    wordsOnLine = 0;
    for (Pair<String, Integer> pair : body) {
      if (pair.second() != lastEmbeddingLevel) {
        lastEmbeddingLevel = pair.second();
        messageString += "\n";
        wordsOnLine = maxWordsPerLine;
      }
      if (wordsOnLine >= maxWordsPerLine) {
        wordsOnLine = 0;
        messageString += "\n\t";
        for (int i = 0; i < lastEmbeddingLevel; i++) {
          messageString += "> ";
        }
      }
      messageString += (" " + pair.first());
      wordsOnLine++;
    }
    messageString += ("\n</MESSAGE " + label + ">\n\n");
    return messageString;
  }
}