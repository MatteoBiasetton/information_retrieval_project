import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Parser p = new Parser("TREC7", ".res", 351, 50);
        System.out.println("Getting data...");
        TopicResult[][] data = p.getTopicList();
        System.out.println("Normalizing data...");
        TopicResult[][] normData = Normalization.SumNorm(data);

        //System.out.println("DATA");
        //printData(data);
        //System.out.println("NORMALIZED_DATA");
        //printData(normData);

        /*System.out.println("Execution of rank fusion algorithms...");
        //comb min
        System.out.println("combMIN");
        //printResults(Fusion.combMin(normData));
        printResultsToFile(Fusion.combMin(normData), "combMIN");

        //comb max
        System.out.println("combMAX");
        //printResults(Fusion.combMax(normData));
        printResultsToFile(Fusion.combMax(normData), "combMAX");

        //comb sum
        System.out.println("combSUM");
        //printResults(Fusion.combSum(normData));
        printResultsToFile(Fusion.combSum(normData), "combSUM");

        //comb anz
        System.out.println("combANZ");
        //printResults(Fusion.combAnz(normData));
        printResultsToFile(Fusion.combAnz(normData), "combANZ");

        //comb mnz
        System.out.println("combMNZ");
        //printResults(Fusion.combMnz(normData));
        printResultsToFile(Fusion.combMnz(normData), "combMNZ");

        //comb med
        System.out.println("combMED");
        //printResults(Fusion.combMed(normData));
        printResultsToFile(Fusion.combMed(normData), "combMED");
*/
        //condorcet fuse
        System.out.println("condorcetFuse");
        //printResults(Fusion.condorcetFuse(normData));
        printResultsToFile(Fusion.condorcetFuse(normData), "condorcetFuse");

        //condorcet fuse
        System.out.println("weightedCondorcetFuse");
        //printResults(Fusion.condorcetFuse(normData));
        printResultsToFile(Fusion.weightedCondorcetFuse(normData), "weightedCondorcetFuse");
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
            System.out.println();
        }
    }

    /**
     * Prints an array of TopicResult
     *
     * @param results data to be printed
     */
    private static void printResultsToFile(TopicResult[] results, String outName) {
        System.out.println("Saving to file...");
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
            System.out.println();
        }
    }


}
