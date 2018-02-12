import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

/**
 * Tools for input data parsing
 */
public class Parser {
    private String path;
    private TopicResult[][] topicList;

    /**
     * Create a parser that load all the data of the runs contained in the given folder.
     *
     * @param folderName    name of the folder containing the input runs
     * @param fileExtension extension of the input files
     * @param firstTopic    number of the first topic contained in each run
     * @param nTopic        number of topics contained in each run
     */
    public Parser(String folderName, String fileExtension, int firstTopic, int nTopic) {
        String separator = File.separator;
        try {
            URL resource = Parser.class.getResource(separator + folderName);
            try {
                path = Paths.get(resource.toURI()).toFile().toString() + separator;
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            System.out.println("FOLDER PATH: " + path);
            System.out.println();
            File dir = new File(path);
            File[] fileList = dir.listFiles((dir1, filename) -> filename.endsWith(fileExtension));
            String[] fileNames = new String[fileList.length];
            for (int i = 0; i < fileList.length; i++) {
                fileNames[i] = fileList[i].getName();
            }
            // Print filenames
            System.out.println("DETECTED FILES:");
            for (String file : fileNames
                    ) {
                System.out.println(file);
            }
            System.out.println();

            FileReader fileReader = new FileReader(path + fileNames[0]);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            Scanner sc;
            int idr; //run id
            int numTopics = nTopic;
            System.out.println("NUMBER OF TOPICS: " + numTopics + "\n");
            bufferedReader.close();
            //initialization
            topicList = new TopicResult[numTopics][fileList.length];
            for (int topic = 0; topic < numTopics; topic++) {
                for (int run = 0; run < fileList.length; run++) {
                    topicList[topic][run] = new TopicResult(run, topic);
                }
            }
            //read input files (runs) --> data organized in
            for (int i = 0; i < fileList.length; i++) {
                fileReader = new FileReader(path + fileNames[i]);
                bufferedReader = new BufferedReader(fileReader);
                idr = Integer.parseInt(fileNames[i].substring(fileNames[i].lastIndexOf('_') + 1, fileNames[i].lastIndexOf('.')));
                while ((line = bufferedReader.readLine()) != null) {
                    sc = new Scanner(line);
                    int q;
                    String d;
                    int r;
                    double s;
                    q = Integer.parseInt(sc.next());    //get the topic index
                    sc.next();                          //skip
                    d = sc.next();                      //get the doc id
                    r = Integer.parseInt(sc.next());    //get ranking
                    s = Double.parseDouble(sc.next());  //get score
                    //add one row to the TopicResult relative to run idr and topic q
                    topicList[q - firstTopic][idr].appendRow(q, d, r, s, idr);
                }
                bufferedReader.close();
            }

        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + path + "'");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Create a parser that load all the data of the runs contained in the given folder.
     * @param folderName name of the folder containing the input runs
     * @param fileExtension extension of the input files
     * @param firstTopic number of the first topic contained in each run
     * @param nTopic number of topics contained in each run
     * @param nFiles number of files to be chosen randomly among the ones contained in the input folder
     */
    public Parser(String folderName, String fileExtension, int firstTopic, int nTopic, int nFiles) {
        String separator = File.separator;
        try {
            URL resource = Parser.class.getResource(separator + folderName);
            try {
                path = Paths.get(resource.toURI()).toFile().toString() + separator;
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            System.out.println();
            File dir = new File(path);
            File[] fileListComplete = dir.listFiles((dir1, filename) -> filename.endsWith(fileExtension));
            String[] fileNamesComplete = new String[fileListComplete.length];
            for (int i = 0; i < fileListComplete.length; i++) {
                fileNamesComplete[i] = fileListComplete[i].getName();
            }


            String line;
            Scanner sc;
            int idr; //run id
            int numTopics = nTopic;


            File fileList[] = new File[nFiles];
            String fileNames[] = new String[nFiles];
            ArrayList<Integer> indexes = new ArrayList<>();
            for (int i = 0; i < fileListComplete.length; i++) {
                indexes.add(i);
            }

            Random rand = new Random(System.currentTimeMillis());
            int k;
            for (int i = 0; i < nFiles; i++) {
                k = rand.nextInt(indexes.size());
                fileList[i] = fileListComplete[indexes.get(k)];
                fileNames[i] = fileNamesComplete[indexes.get(k)];
                indexes.remove(k);
            }

            System.out.println("Chosen files: " + Arrays.toString(fileNames));

            BufferedReader bufferedReader;
            FileReader fileReader;

            //initialization
            topicList = new TopicResult[numTopics][fileList.length];
            for (int topic = 0; topic < numTopics; topic++) {
                for (int run = 0; run < fileList.length; run++) {
                    topicList[topic][run] = new TopicResult(run, topic);
                }
            }
            //read input files (runs) --> data organized in
            for (int i = 0; i < fileList.length; i++) {
                fileReader = new FileReader(path + fileNames[i]);
                bufferedReader = new BufferedReader(fileReader);
                //idr = Integer.parseInt(fileNames[i].substring(fileNames[i].lastIndexOf('_') + 1, fileNames[i].lastIndexOf('.')));
                int topic = 0;
                while ((line = bufferedReader.readLine()) != null) {
                    sc = new Scanner(line);
                    int q;
                    String d;
                    int r;
                    double s;
                    q = Integer.parseInt(sc.next());    //get the topic index
                    sc.next();                          //skip
                    d = sc.next();                      //get the doc id
                    r = Integer.parseInt(sc.next());    //get ranking
                    s = Double.parseDouble(sc.next());  //get score
                    //add one row to the TopicResult relative to run idr and topic q
                    topicList[q - firstTopic][i].appendRow(q, d, r, s, i);
                }
                bufferedReader.close();
            }

        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + path + "'");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }



    /**
     * Returns a copy of the data structure passed in input
     *
     * @param data Matrix of TopicResult to be copied
     * @return data copy
     */
    public static TopicResult[][] getDataCopy(TopicResult[][] data) {
        TopicResult[][] datacopy = new TopicResult[data.length][data[0].length];
        for (int topic = 0; topic < data.length; topic++) {
            for (int run = 0; run < data[topic].length; run++) {
                datacopy[topic][run] = data[topic][run].getResultRowsCopy();
            }
        }
        return datacopy;
    }

    /**
     * Returns a reference to the data structure
     * @return matrix of TopicResult, every cell-->list of ResultRows
     */
    public TopicResult[][] getTopicList() {
        return topicList;
    }

}
