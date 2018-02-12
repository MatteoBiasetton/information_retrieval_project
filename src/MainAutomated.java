import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainAutomated {
    public static void main(String[] args) {

        for (int nDoc = 2; nDoc <= 12; nDoc++) {
            for (int iteration = 0; iteration < 20; iteration++) {
                String folder = "TREC7";
                Parser p = new Parser(folder, ".res", 351, 50, nDoc);
                TopicResult[][] data = p.getTopicList();
                TopicResult[][] normData = Normalization.SumNorm(data);

                System.out.println(folder + " nDoc:" + nDoc + " iteration:" + iteration);

                //comb min
                printResultsToFile(Fusion.combMin(normData), folder + "_" + nDoc + "_" + "combMIN" + "_" + iteration);

                //comb max
                printResultsToFile(Fusion.combMax(normData), folder + "_" + nDoc + "_" + "combMAX" + "_" + iteration);

                //comb sum
                printResultsToFile(Fusion.combSum(normData), folder + "_" + nDoc + "_" + "combSUM" + "_" + iteration);

                //comb anz
                printResultsToFile(Fusion.combAnz(normData), folder + "_" + nDoc + "_" + "combANZ" + "_" + iteration);

                //comb mnz
                printResultsToFile(Fusion.combMnz(normData), folder + "_" + nDoc + "_" + "combMNZ" + "_" + iteration);

                //comb med
                printResultsToFile(Fusion.combMed(normData), folder + "_" + nDoc + "_" + "combMED" + "_" + iteration);

                //condorcet fuse
                printResultsToFile(Fusion.condorcetFuse(normData), folder + "_" + nDoc + "_" + "condorcetFuse" + "_" + iteration);

                //weighted condorcet fuse
                printResultsToFile(Fusion.weightedCondorcetFuse(normData), folder + "_" + nDoc + "_" + "weightedCondorcetFuse" + "_" + iteration);
            }
        }
    }

    /**
     * Prints an array of TopicResult
     *
     * @param results data to be printed
     */
    public static void printResults(TopicResult[] results) {
        for (TopicResult x : results) {
            System.out.println(x.toString() + "\n");
        }
    }

    /**
     * Prints results grouped first by topic, then by run
     *
     * @param data Topicresult matrix to be printed
     */
    public static void printData(TopicResult[][] data) {
        for (TopicResult[] row : data) {
            for (TopicResult x : row) {
                if (!x.toString().trim().equals("")) {
                    System.out.println(x.toString());
                }
            }
        }
    }

    /**
     * Prints an array of TopicResult
     *
     * @param results data to be printed
     */
    private static void printResultsToFile(TopicResult[] results, String outName) {
        //System.out.println("Saving to file...");
        FileWriter f = null;
        try {
            f = new FileWriter(new File(outName));
            for (TopicResult x : results) {
                f.write(x.toString());
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (f != null) {
                    f.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
