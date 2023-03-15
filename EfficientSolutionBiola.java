import java.io.*;
import java.util.*;

public class EfficientSolutionBiola {
  /* constants */
  final int DELTA = 30;

  /* Global variables */
  Map<char[], List<Integer>> map;
  Map<Character, Integer> charMap;
  int[][] matrix;
  String str1;
  String str2;
  StringBuilder sb;
  StringBuilder builder1;
  StringBuilder builder2;
  Scanner scanner;

  /* default constructor */
  public EfficientSolutionBiola() {
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
    builder1 = new StringBuilder();
    builder2 = new StringBuilder();
  }

  /* computes the similarities between 2 strings */
  private int[][] align(String x, String y) {
    int[][] opt = new int[x.length() + 1][y.length() + 1];
    for (int i = 0; i <= x.length(); i++) {
      opt[i][0] = DELTA * i;
    }
    for (int i = 0; i <= y.length(); i++) {
      opt[0][i] = DELTA * i;
    }
    for (int i = 1; i <= x.length(); i++) {
      for (int j = 1; j <= y.length(); j++) {
        char c1 = x.charAt(i - 1);
        char c2 = y.charAt(j - 1);
        int a = charMap.get(c1);
        int b = charMap.get(c2);
        opt[i][j] = Math.min(
            matrix[a][b] + opt[i - 1][j - 1],
            DELTA + Math.min(opt[i][j - 1], opt[i - 1][j]));
      }
    }
    return opt;
  }

  /* reverses string */
  private String reverse(String str) {
    sb.setLength(0);
    sb.append(str);
    String reversed = sb.reverse().toString();
    sb.setLength(0);
    return reversed;
  }

  /* finds the split point for the divide step */
  private int findSplitPoint(String x, String y) {
    int splitX = x.length() / 2;
    String xLeft = x.substring(0, splitX);
    String xRight = x.substring(splitX, x.length());
    int splitYAt = 0;
    int[][] leftOpt = align(xLeft, y);
    int[][] rightOpt = align(reverse(xRight), reverse(y));
    int n = leftOpt[0].length - 1;
    int end = leftOpt.length - 1;
    int optimalMinValue = Integer.MAX_VALUE;
    for (int i = 0; i <= n; i++) {
      int val = leftOpt[end][i] + rightOpt[end][n - i];
      if (val < optimalMinValue) {
        splitYAt = i;
        optimalMinValue = val;
      }
    }
    return splitYAt;
  }

  /* gets the final strings and cost */
  public String[] getFinalString(String x, String y, int[][] opt) {
    int i = x.length();
    int j = y.length();
    builder1.setLength(0);
    builder2.setLength(0);
    String[] result = new String[3];
    while (i != 0 || j != 0) {
      if ((i >= 1) &&
          (j >= 1) &&
          (opt[i][j] == matrix[charMap.get(x.charAt(i - 1))][(charMap.get(y.charAt(j - 1)))] +
              opt[i - 1][j - 1])) {
        builder1.append(x.charAt(i - 1));
        builder2.append(y.charAt(j - 1));
        i--;
        j--;
      } else if (j >= 1 && opt[i][j] == DELTA + opt[i][j - 1]) {
        builder1.append('_');
        builder2.append(y.charAt(j - 1));
        j--;
      } else {
        builder1.append(x.charAt(i - 1));
        builder2.append('_');
        i--;
      }
    }
    result[0] = builder1.reverse().toString();
    result[1] = builder2.reverse().toString();
    /* optimal cost for all of y and all of x */
    result[2] = Integer.toString(opt[x.length()][y.length()]);
    return result;
  }

  /* computes the alignment with divide and conquer strategy */
  private String[] alignment(String x, String y) {
    if (x.length() <= 2) {
      int[][] opt = align(x, y);
      return getFinalString(x, y, opt);
    }
    int splitX = x.length() / 2;
    int splitY = findSplitPoint(x, y);
    String xLeft = x.substring(0, splitX);
    String xRight = x.substring(splitX, x.length());
    String yLeft = y.substring(0, splitY);
    String yRight = y.substring(splitY, y.length());

    String[] leftHalf = alignment(xLeft, yLeft);
    String[] rightHalf = alignment(xRight, yRight);

    String cost = Integer.toString(Integer.parseInt(leftHalf[2]) + Integer.parseInt(rightHalf[2]));

    return new String[] { leftHalf[0] + rightHalf[0], leftHalf[1] + rightHalf[1], cost };
  }

  /* processes input data to generate strings */
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

  /* reads input data */
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

  /* gets the memory space used in KB */
  private static double getMemory() {
    double total = Runtime.getRuntime().totalMemory();
    return (total - Runtime.getRuntime().freeMemory()) / 10e3;
  }

  /* gets the time in seconds */
  private static double getTime() {
    return System.nanoTime() / 10e6;
  }

  /* writes output, time complexity and space complexity to file */
  public void writeFile(String fileName, double time, double space, String[] result) {
    try {
      FileWriter myWriter = new FileWriter(fileName, true);
      // String s = result[2] + "\n" + result[0] + "\n" + result[1] + "\n" + time +
      // "\n" + space + "\n\n";
      String s = str1.length() + str2.length() + "," + time + "," + space + "\n";
      myWriter.write(s);
      myWriter.close();
    } catch (IOException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws IOException {
    String inputFilename = args[0];
    String outputFilename = args[1];
    double startSpace = getMemory();
    double startTime = getTime();
    EfficientSolutionBiola efficient = new EfficientSolutionBiola();
    File inputFile = new File(inputFilename);
    efficient.readFile(inputFile);
    efficient.generateStrings();
    String[] result = efficient.alignment(efficient.str1, efficient.str2);
    double endSpace = getMemory();
    double endTime = getTime();
    double space = endSpace - startSpace;
    double time = endTime - startTime;
    File outputFile = new File(outputFilename);
    outputFile.createNewFile();
    efficient.writeFile(outputFilename, time, space, result);
  }
}
