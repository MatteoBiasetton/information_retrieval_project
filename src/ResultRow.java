/**
 * Data structure used to represent a single row in a TopicResult.
 * Every row represents the information (id,ranking,score) about one retrieved document for run idRun and topic idTopic
 */
public class ResultRow implements Comparable {
    private int idRun;
    private int idTopic;
    private String docId;
    private int rank;
    private double score;

    public ResultRow(int idRun, int idTopic, String docId, int rank, double score) {
        this.idRun = idRun;
        this.idTopic = idTopic;
        this.docId = docId;
        this.rank = rank;
        this.score = score;
    }

    /**
     * Get the id of the run relative to this row
     *
     * @return id run
     */
    public int getRun() {
        return idRun;
    }

    /**
     * Get the id of the topic relative to this row
     * @return id topic
     */
    public int getTopic() {
        return idTopic;
    }

    /**
     * Get the doc id of the row
     * @return doc id
     */
    public String getDoc() {
        return docId;
    }

    /**
     * Get the ranking of the document in the retrieved list of documents
     * @return ranking position (1 to 1000)
     */
    public int getRank() {
        return rank;
    }

    /**
     * Get the score returned by the IRS for this combination of run,topic,document
     * @return score
     */
    public double getScore() {
        return score;
    }

    /**
     * set the idRun parameter
     * @param run new value
     */
    public void setRun(int run) {
        idRun = run;
    }

    /**
     * set the idTopic parameter
     * @param topic new value
     */
    public void setTopic(int topic) {
        idTopic = topic;
    }

    /**
     * set the docId parameter
     * @param doc new value
     */
    public void setDoc(String doc) {
        docId = doc;
    }

    /**
     * set the rank parameter
     * @param r new value
     */
    public void setRank(int r) {
        rank = r;
    }

    /**
     * set the score parameter
     * @param s new value
     */
    public void setScore(double s) {
        score = s;
    }

    /**
     * Compares two resultRows according to the score value. Greater score-->better row-->lower ranking
     * @param other
     * @return 1 if other.score>this.score(this.rank>other.rank)
     *        -1 if other.score<this.score(this.rank<other.rank)
     *         0 if equal scores (tie between the two documents)
     */
    @Override
    public int compareTo(Object other) {
        return Double.compare(((ResultRow) other).getScore(), this.score);
    }

    /**
     * Returns the information formatted the same way as the input file
     * @return string for this row
     */
    @Override
    public String toString() {
        return idTopic + " Q0 " + docId + " " + rank + " " + score + " OUTPUT";
    }
}
