/**
 * @author Shanmuga Ganesh
 * 
 *         this class will depict the message structure
 */
public class Message {
    private int pid;
    private int timestamp;

    // typical message looks like {(pid,ts)}
    Message(int id, int ts) {
        pid = id;
        timestamp = ts;
    }

    public int getPid() {
        return pid;
    }

    public int getTimestamp() {
        return timestamp;
    }

    /**
     * each message will look like (pid,timestamp)
     */
    @Override
    public String toString() {
        return "(" + this.pid + "," + this.timestamp + ")";
    }
}