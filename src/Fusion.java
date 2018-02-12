import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Implementation of rankFusion Algorithms
 */
public class Fusion {

    /**
     * Implementation of combMin rankFusion algorithm
     *
     * @param data matrix of TopicResult objects data[q][r]=block of ResultRows for topic q on run r
     * @return array of TopicResult, one for each topic
     */
    public static TopicResult[] combMin(TopicResult[][] data) {
        // Initialize output structure
        TopicResult[] combMinResult = new TopicResult[data.length];
        for (int topic = 0; topic < data.length; topic++) {
            combMinResult[topic] = new TopicResult(topic);
        }

        for (int topic = 0; topic < combMinResult.length; topic++) {
            int idx;
            for (int run = 0; run < data[topic].length; run++) {
                for (ResultRow currentRow : data[topic][run].getResultRows()) {
                    idx = combMinResult[topic].search(currentRow.getDoc());
                    if (idx != -1) {
                        combMinResult[topic].setScore(idx, Math.min(combMinResult[topic].getScore(idx), currentRow.getScore()));
                    } else
                        combMinResult[topic].appendRow(new ResultRow(currentRow.getRun(), currentRow.getTopic(), currentRow.getDoc(), currentRow.getRank(), currentRow.getScore()));
                }
            }

            //Sort results based on the scores
            combMinResult[topic].computeRanking();
        }
        return combMinResult;
    }

    /**
     * Implementation of combMax rankFusion algorithm
     *
     * @param data matrix of TopicResult objects data[q][r]=block of ResultRows for topic q on run r
     * @return array of TopicResult, one for each topic
     */
    public static TopicResult[] combMax(TopicResult[][] data) {
        // Initialize output structure
        TopicResult[] combMaxResult = new TopicResult[data.length];
        for (int topic = 0; topic < data.length; topic++) {
            combMaxResult[topic] = new TopicResult(100, topic);
        }

        //For each topic
        for (int topic = 0; topic < combMaxResult.length; topic++) {
            int idx;
            //For each block of results of the current topic in each run
            for (int run = 0; run < data[topic].length; run++) {
                //For each row in the block
                for (ResultRow currentRow : data[topic][run].getResultRows()) {
                    //Search for this document in the results
                    idx = combMaxResult[topic].search(currentRow.getDoc());
                    //If found, update the score, otherwise append a new row to the results
                    if (idx != -1) {
                        combMaxResult[topic].setScore(idx, Math.max(combMaxResult[topic].getScore(idx), currentRow.getScore()));
                    } else {
                        combMaxResult[topic].appendRow(new ResultRow(currentRow.getRun(), currentRow.getTopic(), currentRow.getDoc(), currentRow.getRank(), currentRow.getScore()));
                    }
                }
            }

            //Sort results based on the scores
            combMaxResult[topic].computeRanking();
        }

        return combMaxResult;
    }

    /**
     * Implementation of combSum rankFusion algorithm
     *
     * @param data matrix of TopicResult objects data[q][r]=block of ResultRows for topic q on run r
     * @return array of TopicResult, one for each topic
     */
    public static TopicResult[] combSum(TopicResult[][] data) {
        // Initialize output structure
        TopicResult[] combSumResult = new TopicResult[data.length];
        for (int topic = 0; topic < data.length; topic++) {
            combSumResult[topic] = new TopicResult(topic);
        }

        for (int topic = 0; topic < combSumResult.length; topic++) {
            int idx;
            for (int run = 0; run < data[topic].length; run++) {
                for (ResultRow currentRow : data[topic][run].getResultRows()) {
                    //for each row of the TopicResult, search if the document is already in the result structure
                    idx = combSumResult[topic].search(currentRow.getDoc());
                    //If the document is present, update the score, otherwise append a new ResultRow to the result structure
                    if (idx != -1) {
                        combSumResult[topic].setScore(idx, combSumResult[topic].getScore(idx) + currentRow.getScore());
                    } else
                        combSumResult[topic].appendRow(new ResultRow(currentRow.getRun(), currentRow.getTopic(), currentRow.getDoc(), currentRow.getRank(), currentRow.getScore()));
                }
            }
            combSumResult[topic].computeRanking();
        }
        return combSumResult;
    }

    /**
     * Implementation of combAnz rankFusion algorithm
     *
     * @param data matrix of Topicresult objects data[q][r]=block of ResultRows for topic q on run r
     * @return array of TopicResult, one for each topic
     */
    public static TopicResult[] combAnz(TopicResult[][] data) {
        // Initialize output structure
        TopicResult[] combAnzResult = new TopicResult[data.length];
        for (int topic = 0; topic < data.length; topic++) {
            combAnzResult[topic] = new TopicResult(topic);
        }
        //Arraylist containing the number of summed scores for each document
        ArrayList<Integer> count;
        for (int topic = 0; topic < combAnzResult.length; topic++) {
            int idx;
            count = new ArrayList<Integer>();
            for (int run = 0; run < data[topic].length; run++) {
                for (ResultRow currentRow : data[topic][run].getResultRows()) {
                    idx = combAnzResult[topic].search(currentRow.getDoc());
                    if (idx != -1) {
                        //sum the score to the others
                        combAnzResult[topic].setScore(idx, combAnzResult[topic].getScore(idx) + currentRow.getScore());
                        //increment the counter of the relative document
                        count.set(idx, count.get(idx) + 1);
                    } else {
                        //add a new ResultRow with count=1
                        combAnzResult[topic].appendRow(new ResultRow(currentRow.getRun(), currentRow.getTopic(), currentRow.getDoc(), currentRow.getRank(), currentRow.getScore()));
                        count.add(1);
                    }
                }
            }
            //compute the average
            for (int i = 0; i < combAnzResult[topic].size(); i++) {
                combAnzResult[topic].setScore(i, combAnzResult[topic].getScore(i) / count.get(i));
            }
            //sort documents based on scores
            combAnzResult[topic].computeRanking();
        }
        return combAnzResult;
    }

    /**
     * Implementation of combMnz rankFusion algorithm
     *
     * @param data matrix of Topicresult objects data[q][r]=block of ResultRows for topic q on run r
     * @return array of TopicResult, one for each topic
     */
    public static TopicResult[] combMnz(TopicResult[][] data) {
        // Initialize output structure
        TopicResult[] combMnzResult = new TopicResult[data.length];
        for (int topic = 0; topic < data.length; topic++) {
            combMnzResult[topic] = new TopicResult(topic);
        }
        ArrayList<Integer> count;
        for (int topic = 0; topic < combMnzResult.length; topic++) {
            int idx;
            count = new ArrayList<Integer>();
            for (int run = 0; run < data[topic].length; run++) {
                for (ResultRow currentRow : data[topic][run].getResultRows()) {
                    idx = combMnzResult[topic].search(currentRow.getDoc());
                    if (idx != -1) {
                        combMnzResult[topic].setScore(idx, combMnzResult[topic].getScore(idx) + currentRow.getScore());
                        count.set(idx, count.get(idx) + 1);
                    } else {
                        combMnzResult[topic].appendRow(new ResultRow(currentRow.getRun(), currentRow.getTopic(), currentRow.getDoc(), currentRow.getRank(), currentRow.getScore()));
                        count.add(1);
                    }
                }
            }
            for (int i = 0; i < combMnzResult[topic].size(); i++) {
                combMnzResult[topic].setScore(i, combMnzResult[topic].getScore(i) * count.get(i));
            }
            combMnzResult[topic].computeRanking();
        }
        return combMnzResult;
    }

    /**
     * Implementation of combMed rankFusion algorithm
     *
     * @param data matrix of TopicResult objects data[q][r]=block of ResultRows for topic q on run r
     * @return array of TopicResult, one for each topic
     */
    public static TopicResult[] combMed(TopicResult[][] data) {
        // Initialize output structure
        TopicResult[] combMedResult = new TopicResult[data.length];
        for (int topic = 0; topic < data.length; topic++) {
            combMedResult[topic] = new TopicResult(topic);
        }

        //For each topic populate HashMap<idDoc,v> temp: idDoc is documentId and v is an ArrayList containing the ResulRows containing idDoc
        for (int topic = 0; topic < combMedResult.length; topic++) {
            HashMap<String, ArrayList<ResultRow>> temp = new HashMap<>();
            //For each block of results of the current topic in each run
            for (int run = 0; run < data[topic].length; run++) {
                //For each row in the block
                for (ResultRow currentRow : data[topic][run].getResultRows()) {
                    // populate the HashMap
                    if (!temp.containsKey(currentRow.getDoc())) {
                        ArrayList<ResultRow> newDoc = new ArrayList<>();
                        newDoc.add(new ResultRow(currentRow.getRun(), currentRow.getTopic(), currentRow.getDoc(), currentRow.getRank(), currentRow.getScore()));
                        temp.put(currentRow.getDoc(), newDoc);
                    } else {
                        temp.get(currentRow.getDoc()).add(new ResultRow(currentRow.getRun(), currentRow.getTopic(), currentRow.getDoc(), currentRow.getRank(), currentRow.getScore()));
                    }
                }
            }

            // Sort scores for each document and extract the median
            for (String x : temp.keySet()) {
                ArrayList<ResultRow> results = temp.get(x);
                results.sort(ResultRow::compareTo);
                ResultRow medianResultRow = results.get(results.size() / 2);
                double score = medianResultRow.getScore();
                // Handle arrays with an even number of element
                if (results.size() % 2 == 0) {
                    medianResultRow.setScore((score + results.get(results.size() / 2).getScore()) / 2);
                }
                combMedResult[topic].appendRow(medianResultRow);
            }

            //Sort results based on the scores
            combMedResult[topic].computeRanking();
        }

        return combMedResult;
    }

    /**
     * Implementation of Condorcet-fuse rankFusion algorithm
     * <p>
     * Ranking
     * 1: count = 0
     * 2: for each of the k search systems Si do
     * 3: If Si ranks d1 above d2, count++
     * 4: If Si ranks d2 above d1, count−−
     * 5: If count > 0,rank d1 better than d2
     * 6: Else rank d2 better than d1
     * <p>
     * Fusion
     * 7: Sort results based on the above ranking
     *
     * @param data matrix of TopicResult objects data[q][r]=block of ResultRows for topic q on run r
     * @return array of TopicResult, one for each topic
     */
    public static TopicResult[] condorcetFuse(TopicResult[][] data) {
        // Initialize output structure
        TopicResult[] condorcetFuseResult = new TopicResult[data.length];
        for (int topic = 0; topic < data.length; topic++) {
            condorcetFuseResult[topic] = new TopicResult(topic);
        }

        //For each topic populate HashMap<idDoc,HashMap<run,rank>> temp: idDoc is documentId rank is the ranking position
        //of IdDoc in the corresponding run
        for (int topic = 0; topic < condorcetFuseResult.length; topic++) {
            HashMap<String, HashMap<Integer, Integer>> docRanks = new HashMap<>();
            //For each block of results of the current topic in each run
            for (int run = 0; run < data[topic].length; run++) {
                //For each row in the block
                for (ResultRow currentRow : data[topic][run].getResultRows()) {
                    // populate the HashMap
                    if (!docRanks.containsKey(currentRow.getDoc())) {
                        HashMap<Integer, Integer> newDoc = new HashMap<>();
                        newDoc.put(currentRow.getRun(), currentRow.getRank());
                        docRanks.put(currentRow.getDoc(), newDoc);
                    } else {
                        docRanks.get(currentRow.getDoc()).put(currentRow.getRun(), currentRow.getRank());
                    }
                }
            }

            // Sort scores using the condorcet algorithm
            String[] topicDocs = docRanks.keySet().toArray(new String[docRanks.keySet().size()]);

            //mergeSort(topicDocs, 0, topicDocs.length - 1, data, docRanks, topic);
            //insertionSort(topicDocs, data, docRanks, topic);
            quickSort(topicDocs, data, docRanks, topic);

            // Here array is sorted; produce the right output
            for (int i = 0; i < topicDocs.length; i++) {
                condorcetFuseResult[topic].appendRow(data[topic][0].getRow(0).getTopic(), topicDocs[i], i, (double) (topicDocs.length - i) / topicDocs.length, -1);
            }
        }
        return condorcetFuseResult;
    }

    /**
     * Comparison function used by condorcetFuse to compare two documents
     *
     * @param data     Data structure containing the raw data of the runs
     * @param docRanks Data structure containing the ranking positions for each documents
     * @param topic    identifier for the current topic
     * @param d1       document 1
     * @param d2       document 2
     * @return importance of document 1 respect to document 2. If  return a number > 0, than d1 is more
     * important than d2; otherwise d2 is more important than d1
     */
    private static int compareDocuments(TopicResult[][] data, HashMap<String, HashMap<Integer, Integer>> docRanks, int topic, String d1, String d2) {
        int cont = 0;
        for (int run = 0; run < data[topic].length; run++) {
            if (docRanks.get(d1).containsKey(run) && docRanks.get(d2).containsKey(run)) {
                if (docRanks.get(d1).get(run) < docRanks.get(d2).get(run)) {
                    cont++;
                } else {
                    cont--;
                }
            } // If d2 is not present in the current run add score to d1
            else if (docRanks.get(d1).containsKey(run) && !docRanks.get(d2).containsKey(run)) {
                cont++;
            } // If d1 is not present in the current run add score to d2
            else if (!docRanks.get(d1).containsKey(run) && docRanks.get(d2).containsKey(run)) {
                cont--;
            }
        }
        return cont;
    }


    /**
     * Implementation of Weighted-Condorcet-fuse rankFusion algorithm
     * <p>
     * Ranking
     * 1: count = 0
     * 2: for each of the k search systems Si do
     * 3: If Si ranks d1 above d2, count+=(d1-d2)
     * 4: If Si ranks d2 above d1, count−=(d2-d1)
     * 5: If count > 0,rank d1 better than d2
     * 6: Else rank d2 better than d1
     * <p>
     * Fusion
     * 7: Sort results based on the above ranking
     *
     * @param data matrix of TopicResult objects data[q][r]=block of ResultRows for topic q on run r
     * @return array of TopicResult, one for each topic
     */
    public static TopicResult[] weightedCondorcetFuse(TopicResult[][] data) {
        // Initialize output structure
        TopicResult[] condorcetFuseResult = new TopicResult[data.length];
        for (int topic = 0; topic < data.length; topic++) {
            condorcetFuseResult[topic] = new TopicResult(topic);
        }

        //For each topic populate HashMap<idDoc,HashMap<run,score>> temp: idDoc is documentId rank is the ranking position
        //of IdDoc in the corresponding run
        for (int topic = 0; topic < condorcetFuseResult.length; topic++) {
            HashMap<String, HashMap<Integer, Double>> docRanks = new HashMap<>();
            //For each block of results of the current topic in each run
            for (int run = 0; run < data[topic].length; run++) {
                //For each row in the block
                for (ResultRow currentRow : data[topic][run].getResultRows()) {
                    // populate the HashMap
                    if (!docRanks.containsKey(currentRow.getDoc())) {
                        HashMap<Integer, Double> newDoc = new HashMap<>();
                        newDoc.put(currentRow.getRun(), currentRow.getScore());
                        docRanks.put(currentRow.getDoc(), newDoc);
                    } else {
                        docRanks.get(currentRow.getDoc()).put(currentRow.getRun(), currentRow.getScore());
                    }
                }
            }

            // Sort scores using the condorcet algorithm
            String[] topicDocs = docRanks.keySet().toArray(new String[docRanks.keySet().size()]);

            //mergeSort(topicDocs, 0, topicDocs.length - 1, data, docRanks, topic);
            //insertionSort(topicDocs, data, docRanks, topic);
            quickSortWeighted(topicDocs, data, docRanks, topic);

            // Here array is sorted; produce the right output
            for (int i = 0; i < topicDocs.length; i++) {
                condorcetFuseResult[topic].appendRow(data[topic][0].getRow(0).getTopic(), topicDocs[i], i, (double) (topicDocs.length - i) / topicDocs.length, -1);
            }
        }
        return condorcetFuseResult;
    }

    /**
     * Comparison function used by weightedCondorcetFuse to compare two documents
     *
     * @param data     Data structure containing the raw data of the runs
     * @param docRanks Data structure containing the ranking positions for each documents
     * @param topic    identifier for the current topic
     * @param d1       document 1
     * @param d2       document 2
     * @return importance of document 1 respect to document 2. If  return a number > 0, than d1 is more
     * important than d2; otherwise d2 is more important than d1
     */
    private static double compareDocumentsWithWeights(TopicResult[][] data, HashMap<String, HashMap<Integer, Double>> docRanks, int topic, String d1, String d2) {
        double cont = 0;
        for (int run = 0; run < data[topic].length; run++) {
            if (docRanks.get(d1).containsKey(run) && docRanks.get(d2).containsKey(run)) {
                if (docRanks.get(d1).get(run) > docRanks.get(d2).get(run)) {
                    cont += docRanks.get(d1).get(run) - docRanks.get(d2).get(run);
                } else {
                    cont -= docRanks.get(d2).get(run) - docRanks.get(d1).get(run);
                }
            } // If d2 is not present in the current run add score to d1
            else if (docRanks.get(d1).containsKey(run) && !docRanks.get(d2).containsKey(run)) {
                cont += docRanks.get(d1).get(run);
            } // If d1 is not present in the current run add score to d2
            else if (!docRanks.get(d1).containsKey(run) && docRanks.get(d2).containsKey(run)) {
                cont -= docRanks.get(d2).get(run);
            }
        }
        return cont;
    }

    /**
     * Insertion sort algorithm
     *
     * @param array    array to sort
     * @param data     Data structure containing the raw data of the runs
     * @param docRanks Data structure containing the ranking positions for each documents
     * @param topic    identifier for the current topic
     */
    private static void insertionSort(String[] array, TopicResult[][] data, HashMap<String, HashMap<Integer, Integer>> docRanks, int topic) {
        String temp;
        for (int i = 1; i < array.length; i++) {
            temp = array[i];
            int j = i;
            while (j > 0 && compareDocuments(data, docRanks, topic, temp, array[j - 1]) > 0) {
                array[j] = array[j - 1];
                j--;
            }
            array[j] = temp;
        }
    }

    /**
     * Quick sort algorithm
     *
     * @param arr      array to sort
     * @param data     Data structure containing the raw data of the runs
     * @param docRanks Data structure containing the ranking positions for each documents
     * @param topic    identifier for the current topic
     */
    private static void quickSort(String[] arr, TopicResult[][] data, HashMap<String, HashMap<Integer, Integer>> docRanks, int topic) {
        quick(arr, 0, arr.length - 1, data, docRanks, topic);
    }

    /**
     * Util function for quick sort
     *  @param arr      array to sort
     * @param start    start index
     * @param end      end index
     * @param data     Data structure containing the raw data of the runs
     * @param docRanks Data structure containing the ranking positions for each documents
     * @param topic    identifier for the current topic
     */
    private static void quick(String[] arr, int start, int end, TopicResult[][] data, HashMap<String, HashMap<Integer, Integer>> docRanks, int topic) {
        if (start < end) {
            int pivot = partition(arr, start, end, data, docRanks, topic);
            quick(arr, start, pivot - 1, data, docRanks, topic);
            quick(arr, pivot + 1, end, data, docRanks, topic);
        }
    }

    /**
     * Util function for quick sort, partition the elements around a pivot
     *
     * @param arr      array
     * @param start    start index
     * @param end      end index
     * @param data     Data structure containing the raw data of the runs
     * @param docRanks Data structure containing the ranking positions for each documents
     * @param topic    identifier for the current topic
     * @return index of the pivot
     */
    private static int partition(String[] arr, int start, int end, TopicResult[][] data, HashMap<String, HashMap<Integer, Integer>> docRanks, int topic) {
        int pivotIndex = start + (int) (Math.random() * (end - start + 1));
        String pivot = arr[pivotIndex];
        swap(arr, pivotIndex, end);
        int i = start - 1;
        for (int j = start; j < end - 1; j++) {
            if (compareDocuments(data, docRanks, topic, arr[j], pivot) > 0) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, end, i + 1);
        return i + 1;
    }


    /**
     * Quick sort algorithm used by weightedCondorcetFuse
     *
     * @param arr      array to sort
     * @param data     Data structure containing the raw data of the runs
     * @param docRanks Data structure containing the ranking positions for each documents
     * @param topic    identifier for the current topic
     */
    private static void quickSortWeighted(String[] arr, TopicResult[][] data, HashMap<String, HashMap<Integer, Double>> docRanks, int topic) {
        quickWeighted(arr, 0, arr.length - 1, data, docRanks, topic);
    }

    /**
     * Util function for quick sort used by weightedCondorcetFuse
     *
     * @param arr      array to sort
     * @param start    start index
     * @param end      end index
     * @param data     Data structure containing the raw data of the runs
     * @param docRanks Data structure containing the ranking positions for each documents
     * @param topic    identifier for the current topic
     */
    private static void quickWeighted(String[] arr, int start, int end, TopicResult[][] data, HashMap<String, HashMap<Integer, Double>> docRanks, int topic) {
        if (start < end) {
            int pivot = partitionWeighted(arr, start, end, data, docRanks, topic);
            quickWeighted(arr, start, pivot - 1, data, docRanks, topic);
            quickWeighted(arr, pivot + 1, end, data, docRanks, topic);
        }
    }

    /**
     * Util function for quick sort, partition the elements around a pivot
     *
     * @param arr      array
     * @param start    start index
     * @param end      end index
     * @param data     Data structure containing the raw data of the runs
     * @param docRanks Data structure containing the ranking positions for each documents
     * @param topic    identifier for the current topic
     * @return index of the pivot
     */
    private static int partitionWeighted(String[] arr, int start, int end, TopicResult[][] data, HashMap<String, HashMap<Integer, Double>> docRanks, int topic) {
        int pivotIndex = start + (int) (Math.random() * (end - start + 1));
        String pivot = arr[pivotIndex];
        swap(arr, pivotIndex, end);
        int i = start - 1;
        for (int j = start; j < end - 1; j++) {
            if (compareDocumentsWithWeights(data, docRanks, topic, arr[j], pivot) > 0) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, end, i + 1);
        return i + 1;
    }

    /**
     * Util function for quick sort, swap two element of an array
     *
     * @param arr array
     * @param i1  index 1
     * @param i2  index 2
     */
    private static void swap(String[] arr, int i1, int i2) {
        String temp = arr[i1];
        arr[i1] = arr[i2];
        arr[i2] = temp;
    }

    /**
     * Merge sort algorithm
     *
     * @param arr      array to sort
     * @param start    start index
     * @param end      end index
     * @param data     Data structure containing the raw data of the runs
     * @param docRanks Data structure containing the ranking positions for each documents
     * @param topic    identifier for the current topic
     */
    private static void mergeSort(String[] arr, int start, int end, TopicResult[][] data, HashMap<String, HashMap<Integer, Integer>> docRanks, int topic) {
        if (start < end) {
            int mid = (start + end) / 2;
            mergeSort(arr, start, mid, data, docRanks, topic);
            mergeSort(arr, mid + 1, end, data, docRanks, topic);
            merge(arr, start, mid, end, data, docRanks, topic);
        }
    }

    /**
     * Util function for merge sort, merge two sub array
     *
     * @param arr      array to
     * @param start    start index
     * @param end      end index
     * @param data     Data structure containing the raw data of the runs
     * @param docRanks Data structure containing the ranking positions for each documents
     * @param topic    identifier for the current topic
     */
    private static void merge(String[] arr, int start, int end, int high, TopicResult[][] data, HashMap<String, HashMap<Integer, Integer>> docRanks, int topic) {
        int i1 = start;
        int i2 = end + 1;
        int i = 0;
        String temp[] = new String[high - start + 1];
        while (i1 <= end && i2 <= high) {
            if (compareDocuments(data, docRanks, topic, arr[i1], arr[i2]) > 0) {
                temp[i++] = arr[i1++];
            } else {
                temp[i++] = arr[i2++];
            }
        }
        while (i1 <= end) {
            temp[i++] = arr[i1++];
        }
        while (i2 <= high) {
            temp[i++] = arr[i2++];
        }
        System.arraycopy(temp, 0, arr, start, temp.length);
    }

    /**
     * Utility test function used to randomize a given array
     *
     * @param a array to randomize
     */
    private static void randomizeArray(String[] a) {
        String temp;
        Random r = new Random(System.currentTimeMillis());
        for (int i = 0; i < a.length; i++) {
            int index = r.nextInt(a.length);
            System.out.println(index);
            temp = a[index];
            a[index] = a[i];
            a[i] = temp;
        }
    }

}
