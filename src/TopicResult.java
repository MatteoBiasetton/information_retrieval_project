import java.util.ArrayList;
import java.util.Collections;

/**
 * Data structure used to represent the document list for a given Run and a given Topic
 */
public class TopicResult {
    private int idRun;
    private int idTopic;
    private ArrayList<ResultRow> rows;

    /**
     * Constructor for input run
     *
     * @param idRun   id of the given run
     * @param idTopic id of the given topic
     */
    public TopicResult(int idRun, int idTopic) {
        this.idRun = idRun;
        this.idTopic = idTopic;
        rows = new ArrayList<>();
    }

    /**
     * Constructor for result runs, no idRun required
     * @param idTopic id of the given topic
     */
    public TopicResult(int idTopic) {
        this.idRun = -1;
        this.idTopic = idTopic;
        rows = new ArrayList<>();
    }

    /**
     * Add a new document in the block of retrieved documents
     * @param idTopic given topic
     * @param docId id of the document to be added
     * @param rank rank of this document for this topic and this run
     * @param score score of this document for this topic and this run
     * @param idRun given run
     */
    public void appendRow(int idTopic, String docId, int rank, double score, int idRun) {
        ResultRow r = new ResultRow(idRun, idTopic, docId, rank, score);
        rows.add(r);
    }

    /**
     * Add a new document in the block of retrieved documents
     * @param r pre-built row to add
     */
    public void appendRow(ResultRow r) {
        rows.add(r);
    }


    /**
     * Returns the block of rows sorted by asc ranking
     * @return sorted rows
     */
    public ArrayList<ResultRow> getResultRows() {
        Collections.sort(rows);
        return rows;
    }

    /**
     * Returns a copy of the block of rows sorted by asc ranking
     * @return sorted copy of rows
     */
    public TopicResult getResultRowsCopy() {
        Collections.sort(rows);
        TopicResult copy = new TopicResult(idRun, idTopic);
        for (ResultRow row : rows) {
            copy.appendRow(row.getTopic(), row.getDoc(), row.getRank(), row.getScore(), row.getRun());
        }
        return copy;
    }

    /**
     * Returns the TopicResult formatted like the input runs
     * @return block of rows relative to this Topicresult
     */
    public String toString() {
        StringBuilder res = new StringBuilder();
        for (ResultRow r : rows) {
            res.append('\n').append(r.toString());
        }
        return res.toString();
    }

    /**
     * get number of rows in the TopicResult (number of retrieved documents)
     * @return
     */
    public int size() {
        return rows.size();
    }

    /**
     * get run Id of this topicResult
     * @return idRun
     */
    public int getRun() {
        return idRun;
    }

    /**
     * get topic Id of this topicResult
     * @return idTopic
     */
    public int getIdTopic() {
        return idTopic;
    }

    /**
     * get doc Id of the i-th document in the topicResult
     * @param index document index
     * @return id doc
     */
    public String getDoc(int index) {
        return rows.get(index).getDoc();
    }

    /**
     * get score of the i-th document in the topicResult
     * @param index document index
     * @return score
     */
    public double getScore(int index) {
        return rows.get(index).getScore();
    }

    /**
     * get rank of the i-th document in the topicResult
     * @param index document index
     * @return rank
     */
    public int getRank(int index) {
        return rows.get(index).getRank();
    }

    /**
     * get the i-th row in the topicResult
     * @param index document index
     * @return i-th row
     */
    public ResultRow getRow(int index) {
        return rows.get(index);
    }

    /**
     * set the doc Id the i-th document in the topicResult
     * @param index document index
     * @param doc new idDoc
     */
    public void setDoc(int index, String doc) {
        rows.get(index).setDoc(doc);
    }

    /**
     * set the score of the i-th document in the topicResult
     * @param index document index
     * @param s new score
     */
    public void setScore(int index, double s) {
        rows.get(index).setScore(s);
    }

    /**
     * Order the rows in the topicresult and assign ranking
     */
    public void computeRanking() {
        Collections.sort(rows);
        for (int i = 0; i < rows.size(); i++) {
            rows.get(i).setRank(i);
            rows.get(i).setRun(-1);
        }
    }

    /**
     * Returns the index of a document in the topicResult
     * @param doc id of the document to search
     * @return index of the document, -1 if not present
     */
    public int search(String doc) {
        for (int r = 0; r < rows.size(); r++) {
            if (rows.get(r).getDoc().equals(doc)) return r;
        }
        return -1;
    }

}
