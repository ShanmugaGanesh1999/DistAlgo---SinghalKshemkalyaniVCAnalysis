import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Shanmuga Ganesh
 * 
 *         main class for executing the algorithm
 */
public class SinghalKshemkalyaniDistributedSystem {
    // analysis variables
    private static int runFrom = 5, runTo = 100;
    // instances for storing the analysis
    private static int[] maximumLenghtMessageConstruction = new int[runTo];
    private static int[] averageLengthMessageConstruction = new int[runTo];

    private static int[] averageNumberMessageUpdation = new int[runTo];
    private static int[] maximumNumberMessageUpdation = new int[runTo];

    // Change the default values
    private static int numberOfProcesses = -1, maximumBoundPerProcess = 5, initialProcess = 0;

    private static List<Integer> processBoundList;
    private static Random random;
    private static Process[] processes;
    private static Boolean initiator = true, followers = false;

    /**
     * This method decreases the bound of a particular process
     * 
     * @param pid
     */
    private static void processInUse(int pid) {
        int bound = processBoundList.get(pid);
        processBoundList.set(pid, bound - 1);
    }

    /**
     * This method initiates the Process bound List to the maximum bound per process
     */
    private static void initiateProcessBound() {
        for (int i = 0; i < numberOfProcesses; i++) {
            processBoundList.add(i, maximumBoundPerProcess);
        }
    }

    /**
     * This Method checks that particular process can be executed or not
     * 
     * @param pid
     * @return
     */
    private static boolean isProcessBounded(int pid) {
        return processBoundList.get(pid) > 0;
    }

    /**
     * This method checks that the algorithm is in its terminal stage or last stage
     * of completion
     * 
     * @return
     */
    private static boolean isAlgoIsInLastStage() {
        double sum = processBoundList.stream().mapToDouble(p -> p).sum();
        return sum <= numberOfProcesses;
    }

    /**
     * This method checks that all the process is bounded or can't be executed
     * 
     * @return
     */
    private static boolean isProcessTerminated() {
        int processCtr = 0;
        for (int i = 0; i < numberOfProcesses; i++) {
            if (!isProcessBounded(i)) {
                processCtr++;
            }
        }
        return processCtr == numberOfProcesses;
    }

    /**
     * This method executes send operation to a random process
     * 
     * @param pid
     */
    public static void execute(int pid) {
        // decreasing the bound for the running process
        processInUse(pid);

        // Simulating sending messages to other processes
        int i = random.nextInt(numberOfProcesses);
        // here we are checking the random Process can't be the sender
        if (i == pid) {
            if (i < numberOfProcesses - 1) {
                i += 1;
            } else {
                i -= 1;
            }
        }
        processes[pid].sendMessage(processes[i]);
    }

    /**
     * This is the main underlying algorithm implementation
     */
    public static void algorithm() {
        // Initialize and start all processes
        processes = new Process[numberOfProcesses];
        for (int i = 0; i < numberOfProcesses; i++) {
            processes[i] = new Process(i, numberOfProcesses);
        }

        // to execute the initiator process
        if (initiator) {
            execute(initialProcess);
            initiator = false;
            followers = true;
        }

        /*
         * executing the followers till max bound of all processes is met and no message
         * in it's channel
         */
        int lastExecuted = initialProcess;
        while (followers) {
            int i = random.nextInt(numberOfProcesses);
            /*
             * here we are checking the random process i is bounded and Should not be The
             * last executed process or the algorithm is in its last stage of Termination.
             */
            if (isProcessBounded(i) && (i != lastExecuted || isAlgoIsInLastStage())) {
                execute(i);
                lastExecuted = i;
            } /*
               * else, we are checking the process terminated or not, and then making the
               * followers as false,
               * which implies Global termination
               */
            else if (isProcessTerminated()) {
                followers = false;
            }
        }
    }

    /**
     * resetting the variables and clearing the memory it allocated previously
     * 
     * @param numProcesses
     */
    public static void reset(int numProcesses) {
        processBoundList = null;
        processes = null;
        initiator = true;
        followers = false;

        Process.maximumLenghtMessageConstructed = 0;
        Process.maximumNumberMessageUpdated = 0;
        Process.sumMessageConstructed = 0;
        Process.sumMessageUpdated = 0;

        numberOfProcesses = numProcesses;

        random = new Random();
        processBoundList = new ArrayList<Integer>();

        initiateProcessBound();
    }

    /**
     * it will store the analysis made with the algorithm each time
     * 
     * @param range
     */
    private static void recordAnalysis(int range) {
        maximumLenghtMessageConstruction[range] = Process.maximumLenghtMessageConstructed;
        averageLengthMessageConstruction[range] = (Integer) Process.sumMessageConstructed / range;

        maximumNumberMessageUpdation[range] = Process.maximumNumberMessageUpdated;
        averageNumberMessageUpdation[range] = (Integer) Process.sumMessageUpdated / range;
    }

    /**
     * printing the entire results of analysis
     */
    private static void printingTheResults() {

        System.out.println("maximumLenghtMessageConstruction = " + getSingleResult(maximumLenghtMessageConstruction));

        System.out.println("averageLengthMessageConstruction = " + getSingleResult(averageLengthMessageConstruction));

        System.out.println("maximumNumberMessageUpdation = " + getSingleResult(maximumNumberMessageUpdation));

        System.out.println("averageNumberMessageUpdation = " + getSingleResult(averageNumberMessageUpdation));
    }

    /**
     * for returning a single analysis result
     * 
     * @param arr
     * @return
     */
    private static String getSingleResult(int[] arr) {
        String str = "[";
        for (int i = 0; i < runTo; i++) {
            if (i < runFrom) {
                str += "0, ";
            } else {
                str += arr[i];
                if (i != runTo - 1) {
                    str += ", ";
                }
            }
        }
        return str + "]";
    }

    /**
     * main method
     * 
     * @param args
     */
    public static void main(String[] args) {
        // we're running the number of processors from different range
        for (int range = runFrom; range < runTo; range++) {
            reset(range);
            algorithm();

            recordAnalysis(range);
        }
        // print the entire result so that we can plot it and visualize it
        printingTheResults();
    }
}
