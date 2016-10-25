/**
 * Author      : Aaron May 
 * Date        : 25/10/2016
 *
 * The Shortest Remaining Time (SRT) scheduler chooses a process with the shortest expected remaining time.
 * When a new process becomes available, the OS may preempt a running process if the expected time to completion for the currently running process 
 * is longer than the expected run time of the new process.
 */
import java.io.*;
import java.util.*;

public class SRT {
    private Processor newProcessor;//used to pass a process to the processor which provides execution time to the process
    private ArrayList<Process> readyQueue;//the list of processes that are waiting to execute
    private ArrayList<Process> processes;//the list of process that are to be executed from have not arrived into the system yet
    private ArrayList<Process> completed;//the list of processes that have completely finished processing and have no further execution time
    public static int timer;//the current time at any point within the execution of all processes
    private int processQueueSize;//used to store the size of the list that is passed in at the constructor.
    private Process previousProcess;//used to store the last process that was sent to the processor. Used so if the same process is sent to processor twice in a row the output will not be affected.
    /*
     * Takes an ArrayList of processes as parameter and performs a deep copy of this list, which will be worked on rather than the input list.
     * Input: ArryayList of processes
     */
    public SRT(ArrayList<Process> list) {
        readyQueue = new ArrayList<Process>();//the list of processes that are waiting to execute
        processes = new ArrayList<Process>();//the list of process that are to be executed from have not arrived into the system yet
        completed =  new ArrayList<Process>();//the list of process that are to be executed from have not arrived into the system yet
        newProcessor = new Processor();//used to pass a process to the processor which provides execution time to the process
        for(int i = 0; i < list.size();i++) { //create a deep copy of list
            processes.add(new Process(list.get(i).getProcessId(),list.get(i).getArrivalTime(),list.get(i).getExecTime()));
        }
        timer = 0;//set timer to 0 as this is the start of the algorithm
        processQueueSize = list.size();//store the size of the list that is passed in at the constructor.
    }

    /*
     * Send every process to the processor until all execution time reaches 0
     * Output the time where each process is sent to the processor. If the same processor is sent twice in a row (or more) only the first time will be output.
     * Returns a list of processes that have sall been executed.
     * Input: none
     * Output: completed arraylist of processes
     */
    public ArrayList<Process> start() {
        Process process;//temp process used to store the processes from the readyQueue
        Comparator<Process> execIDComparatorSRT = Process.execIDTimeComparator();//Used to sort a list of processes by exec time then by id if exec times are equal

        do{
            storeNextProcess();//store the next process to be executed into the ready queue
            Collections.sort(readyQueue, execIDComparatorSRT); //sorts inputList by exec time then by id time if exec time is equal
            if(readyQueue.size() == 0) {//test if the ready queue has any processes in it
                readyQueue.add(processes.remove(0));//if ready queue has no processes in it, automatically remove and store a process from the processes queue
            }
            process = readyQueue.get(0);//store first process in readyQueue
            if(timer < process.getArrivalTime()) { //increments time when there are no processes to process
                timer = process.getArrivalTime();
            }

            if(previousProcess != process) { //ensures if the same process is processed directly after it self the output does not duplicate
                System.out.println("T" + timer + ": " + process.getProcessId());
            }   
            process = newProcessor.provideCPUTimeSRT(process);//send current process to processor and then store back into process var
            storeNextProcess();//store the next process to be executed into the ready queue
            readyQueue.remove(0);//remove first process in readyQueue as has already been executed
            if(process.getExecTime() > 0) {//check if process still has more processing time
                readyQueue.add(process);//process still has more processing time
            }
            else{
                completed.add(process); //process has completed processing
            }
            previousProcess = process;//store current process into previousProcess to handle if the process is processed next and will cause a duplicate output
        } 
        while(readyQueue.size()!= 0 || completed.size() != processQueueSize);//continue until the readyQueue is empty or the completed queue reaches the size of the original process list
        return completed;//return the processes that have all been processed
    }

    /*
     * Stores the next process to be executed into the ready queue.
     * Input: none
     * Output: none
     */
    public void storeNextProcess() {
        while(processes.size() != 0) {//check that the processes size is not empty
            if(processes.get(0).getArrivalTime() <= timer) {//if next process has arrived
                readyQueue.add(processes.remove(0));//store process in ready queue
            }
            else { //list is in arrival time - can assume that any processes after i not due yet
                break;
            }
        }
    }
}
