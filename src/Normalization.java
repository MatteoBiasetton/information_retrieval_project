import java.util.ArrayList;

/**
 * class containing static method to normalize the scores of the runs using different techniques
 */
public class Normalization {
    /**
     * Returns the data normalized with the standard max-min norm: shift min to 0, scale max to 1
     *
     * @param data data to normalize
     * @return matrix of TopicResult containing the data with the normalized scores. The output matrix has the format
     * TopicResult[topic][run]
     */
    public static TopicResult[][] MinMaxNorm(TopicResult[][] data) {
        //create a copy of the input, for not working with original data
        TopicResult[][] dataCopy = Parser.getDataCopy(data);
        for (int topic = 0; topic < dataCopy.length; topic++) {
            for (int run = 0; run < dataCopy[topic].length; run++) {
                //for each TopicResult, if is non empty find min score and max score
                if (dataCopy[topic][run].size() != 0) {
                    double[] minMax = getMinMax(dataCopy[topic][run]);
                    //for each row, new_score=(old_score-min_score)/(max_score-min_score)
                    for (int i = 0; i < dataCopy[topic][run].size(); i++) {
                        //Prevent division by zero
                        if (minMax[0] == minMax[1]) {
                            dataCopy[topic][run].setScore(i, 1.0);
                        } else {
                            dataCopy[topic][run].setScore(i, (dataCopy[topic][run].getScore(i) - minMax[0]) / (minMax[1] - minMax[0]));
                        }
                    }
                }
            }
        }
        return dataCopy;
    }

    /**
     * Returns the data normalized with the sum norm: shift min to 0, scale sum to 1
     *
     * @param data data to normalize
     * @return matrix of TopicResult containing the data with the normalized scores. The output matrix has the format
     * TopicResult[topic][run]
     */
    public static TopicResult[][] SumNorm(TopicResult[][] data) {
        //create a copy of the input, for not working with original data
        TopicResult[][] datacopy = Parser.getDataCopy(data);
        for (int topic = 0; topic < datacopy.length; topic++) {
            for (int run = 0; run < datacopy[topic].length; run++) {
                //for each TopicResult, if is non empty find min score and max score
                if (datacopy[topic][run].size() != 0) {
                    double[] minmax = getMinMax(datacopy[topic][run]);
                    double sum = computeSum(datacopy[topic][run]);
                    sum = sum - (minmax[0] * datacopy[topic][run].size());
                    //for each row, new_score=(old_score-min_score)/(sum)
                    for (int i = 0; i < datacopy[topic][run].size(); i++) {
                        //Prevent division by zero
                        if (minmax[0] == minmax[1]) {
                            datacopy[topic][run].setScore(i, 1.0);
                        } else {
                            datacopy[topic][run].setScore(i, (datacopy[topic][run].getScore(i) - minmax[0]) / sum);
                        }
                    }
                }
            }
        }
        return datacopy;
    }

    /**
     * Returns the data normalized with the sum norm: shift mean to 0, scale variance to 1
     *
     * @param data data to normalize
     * @return matrix of TopicResult containing the data with the normalized scores. The output matrix has the format
     * TopicResult[topic][run]
     */
    public static TopicResult[][] ZNorm(TopicResult[][] data) {
        //create a copy of the input, for not working with original data
        TopicResult[][] datacopy = Parser.getDataCopy(data);
        for (int topic = 0; topic < datacopy.length; topic++) {
            for (int run = 0; run < datacopy[topic].length; run++) {
                //get array of scores
                int blockSize = datacopy[topic][run].size();
                double[] scores = getScores(datacopy[topic][run]);
                double scoreSum = computeSum(datacopy[topic][run]);
                double averageScore = scoreSum / blockSize;
                double[] zNormalizedScores = new double[blockSize];
                double scoreVariance = 0;
                for (double s : scores) {
                    scoreVariance += Math.pow((s - averageScore), 2);
                }
                for (int i = 0; i < blockSize; i++) {
                    zNormalizedScores[i] = (scores[i] - averageScore) / Math.sqrt(scoreVariance);
                    datacopy[topic][run].setScore(i, zNormalizedScores[i]);
                }
            }
        }

        return datacopy;
    }

    /**
     * Util method used for normalization.
     * Returns the max and the min score of a TopicResult
     *
     * @param tr TopicResult to process
     * @return array containing the max and the min scores of the given TopicResults
     * array[0] = min, array[1] = max
     */
    private static double[] getMinMax(TopicResult tr) {
        ArrayList<ResultRow> rowBlock = tr.getResultRows();
        double[] minMax = new double[2];
        minMax[0] = rowBlock.get(rowBlock.size() - 1).getScore();
        minMax[1] = rowBlock.get(0).getScore();

        return minMax;
    }

    /**
     * Util method used for normalization.
     * Returns an array containing of the scores of a TopicResult
     *
     * @param tr TopicResult to process
     * @return array containing all the scores of the given TopicResults
     */
    private static double[] getScores(TopicResult tr) {
        double[] scores = new double[tr.size()];
        for (int i = 0; i < tr.size(); i++) scores[i] = tr.getScore(i);
        return scores;
    }

    /**
     * Util method used for normalization.
     * Returns the sum of the scores of a TopicResult
     *
     * @param tr TopicResult to process
     * @return sum of the scores of the given TopicResults
     */
    private static double computeSum(TopicResult tr) {
        double sum = 0;
        for (ResultRow row : tr.getResultRows()) {
            sum += row.getScore();
        }
        return sum;
    }

}
