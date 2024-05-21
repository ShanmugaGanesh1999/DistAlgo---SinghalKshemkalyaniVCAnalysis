import java.util.ArrayList;
import java.util.List;

/**
 * @author Shanmuga Ganesh
 * 
 *         matrix clock, this class is the implementation of Singhal
 *         Kshemkalyani proposal
 */
public class MatrixClock {
    private int pid;
    private int state;
    private int n;
    private int[][] clock; // Matrix clock of size N x 3 (TS, LU, LS)

    MatrixClock(int pid, int n) {
        this.pid = pid;
        this.n = n;
        this.state = 0;
        this.clock = new int[n][3];
        this.initializeMatrixClock();
    }

    public int getState() {
        return this.state;
    }

    /**
     * this method is used for updating the last sent of a process to the current
     * timestamp
     * 
     * @param pid
     */
    public void updateLastSent(int pid) {
        this.clock[pid][2] = this.getLocalTimeStamp();
    }

    /**
     * this method will increment the state each time it receives a message
     */
    public void incrementState() {
        this.state++;
    }

    /**
     * executes when we have a internal event and update the local timestamp
     */
    public void internalEventUpdate() {
        this.clock[this.pid][0]++;
    }

    /**
     * this method will initialize the matrix clock to its default values
     */
    private void initializeMatrixClock() {
        for (int i = 0; i < this.n; i++) {
            this.clock[i][0] = 0; // TS
            this.clock[i][1] = 0; // LU
            this.clock[i][2] = this.pid == i ? -1 : 0; // LS
        }
    }

    /**
     * this will sent the last sent field for a perticular process
     * 
     * @param pid
     * @return
     */
    private int getLastSentOfPid(int pid) {
        return this.clock[pid][2];
    }

    /**
     * this will sent the last updated field for a perticular process
     * 
     * @param pid
     * @return
     */
    private int getLastUpdateOfPid(int pid) {
        return this.clock[pid][1];
    }

    /**
     * this will sent the current timestamp field for a perticular process
     * 
     * @param pid
     * @return
     */
    private int getTimeStampOfPid(int pid) {
        return this.clock[pid][0];
    }

    /**
     * this will sent the current timestamp field for a this process
     * 
     * @return
     */
    private int getLocalTimeStamp() {
        return this.clock[this.pid][0];
    }

    /**
     * this method updates the matrix clock from the sent messages
     * 
     * @param messageList
     */
    public int updateMatrixClock(List<Message> messageList) {
        // variable used in keeping track of no of updates are made and will return it

        int updateCtr = 0;
        for (Message message : messageList) {
            int pid = message.getPid();
            int timestamp = message.getTimestamp();
            if (this.clock[pid][0] < timestamp) {
                this.clock[pid][0] = timestamp;
                this.clock[pid][1] = this.getLocalTimeStamp();
                updateCtr++;
            }
        }
        // this will increment the state of the matrix clock
        this.incrementState();

        return updateCtr;
    }

    /**
     * this method is used for constructing the messages which it has to be sent to
     * the particular process
     * 
     * @param toPid
     * @return
     */
    public List<Message> constructMessage(int toPid) {
        List<Message> messages = new ArrayList<Message>();
        Message message = new Message(this.pid, this.getLocalTimeStamp());
        // for updating the local timestamp by default
        messages.add(message);
        int lsToPid = this.getLastSentOfPid(toPid);
        for (int i = 0; i < n && i != this.pid; i++) {
            if (lsToPid < this.getLastUpdateOfPid(i)) {
                message = new Message(i, this.getTimeStampOfPid(i));
                messages.add(message);
            }
        }
        return messages;
    }

    /**
     * this method will print the current matrix clock
     */
    public void printVC() {
        System.out.println("index | timestamp" + this.pid + " | lastupdated" + this.pid + " | lastsent"
                + this.pid);
        for (int i = 0; i < n; i++) {
            System.out.println("------------------------------------------");
            for (int j = 0; j < 4; j++) {
                if (j == 0) {
                    System.out.print(i + "\t| ");
                } else if (j > 0) {
                    System.out.print(clock[i][j - 1]);
                    if (j < 3) {
                        System.out.print("\t\t| ");
                    }
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}