import java.io.*;
import java.util.*;

public class Basic {
  /* constant */
  final int DELTA = 30;

  /* Global variables */
  Map<char[], List<Integer>> map;
  Map<Character, Integer> charMap;
  int[][] matrix;
  Scanner scanner;
  StringBuilder sb;
  int[][] opt;
  String str1;
  String str2;
  String finalStr1;
  String finalStr2;

  /* default constructor */
  public Basic() {
    this.map = new HashMap<>();
    this.sb = new StringBuilder();
    matrix = new int[][] {
        { 0, 110, 48, 94 },
        { 110, 0, 118, 48 },
        { 48, 118, 0, 110 },
        { 94, 48, 110, 0 },
    };
    this.charMap = new HashMap<>();
    charMap.put('A', 0);
    charMap.put('C', 1);
    charMap.put('G', 2);
    charMap.put('T', 3);
    finalStr1 = "";
    finalStr2 = "";
  }

  /* reads data from input file */
  public void readFile(File file) {
    try {
      this.scanner = new Scanner(file);
      List<Integer> list = new ArrayList<>();
      while (scanner.hasNextLine()) {
        String str = scanner.nextLine();
        if (Character.isDigit(str.charAt(0))) {
          list.add(Integer.parseInt(str));
        } else {
          list = new ArrayList<>();
          map.put(str.toCharArray(), list);
        }
      }
      scanner.close();
    } catch (FileNotFoundException e) {
      System.out.println("File not found");
    }
  }

  /* generates the strings from the input data format */
  private void generateStrings() {
    boolean first = true;
    for (Map.Entry<char[], List<Integer>> entry : map.entrySet()) {
      char[] str = entry.getKey();
      List<Integer> indices = entry.getValue();
      sb = new StringBuilder(new String(str));
      for (int i : indices) {
        sb.insert(i + 1, str);
        str = sb.toString().toCharArray();
      }
      if (first) {
        this.str2 = sb.toString();
        sb.setLength(0);
        first = false;
      } else {
        this.str1 = sb.toString();
      }
    }
  }

  /* writes output, time complexity and space complexity to file */
  public void writeFile(String fileName, double time, double space) {
    try {
      FileWriter myWriter = new FileWriter(fileName, true);
      // String s = opt[str1.length()][str2.length()] + "\n" + finalStr1 + "\n" +
      // finalStr2 + "\n" + time + "\n" + space
      // + "\n\n";
      String s = str1.length() + str2.length() + "," + time + "," + space + "\n";
      myWriter.write(s);
      myWriter.close();
    } catch (IOException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
  }

  /* gets the memory space used in KB */
  private static double getMemory() {
    double total = Runtime.getRuntime().totalMemory();
    return (total - Runtime.getRuntime().freeMemory()) / 10e3;
  }

  /* gets the time in seconds */
  private static double getTime() {
    return System.nanoTime() / 10e6;
  }

  /* handles the core sequence alignment functionality */
  public void alignment() {
    opt = new int[str1.length() + 1][str2.length() + 1];

    for (int i = 0; i <= str1.length(); i++) {
      opt[i][0] = DELTA * i;
    }
    for (int i = 0; i <= str2.length(); i++) {
      opt[0][i] = DELTA * i;
    }
    for (int i = 1; i <= str1.length(); i++) {
      for (int j = 1; j <= str2.length(); j++) {
        char c1 = str1.charAt(i - 1);
        char c2 = str2.charAt(j - 1);
        int a = charMap.get(c1);
        int b = charMap.get(c2);
        opt[i][j] = Math.min(
            matrix[a][b] + opt[i - 1][j - 1],
            DELTA + Math.min(opt[i][j - 1], opt[i - 1][j]));
      }
    }
  }

  /* gets the final string */
  private void getFinalString() {
    int i = str1.length();
    int j = str2.length();
    StringBuilder input1 = new StringBuilder();
    StringBuilder input2 = new StringBuilder();
    while (i != 0 || j != 0) {
      if ((i >= 1) && (j >= 1)
          && (opt[i][j] == matrix[charMap.get(str1.charAt(i - 1))][(charMap.get(str2.charAt(j - 1)))]
              + opt[i - 1][j - 1])) {
        input1.append(str1.charAt(i - 1));
        input2.append(str2.charAt(j - 1));
        i--;
        j--;
      } else if (j >= 1 && opt[i][j] == DELTA + opt[i][j - 1]) {
        input1.append('_');
        input2.append(str2.charAt(j - 1));
        j--;
      } else {
        input1.append(str1.charAt(i - 1));
        input2.append('_');
        i--;
      }
    }
    input1.reverse();
    finalStr1 = input1.toString();
    input2.reverse();
    finalStr2 = input2.toString();
  }

  /* main function */
  public static void main(String[] args) throws IOException {
    String inputFilename = args[0];
    String outputFilename = args[1];
    double startSpace = getMemory();
    double startTime = getTime();
    Basic basic = new Basic();
    File inputFile = new File(inputFilename);
    basic.readFile(inputFile);
    basic.generateStrings();
    basic.alignment();
    basic.getFinalString();
    double endSpace = getMemory();
    double endTime = getTime();
    double space = endSpace - startSpace;
    double time = endTime - startTime;
    File outputFile = new File(outputFilename);
    outputFile.createNewFile();
    basic.writeFile(outputFilename, time, space);
  }
}
