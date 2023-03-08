// BirthdayCardProcessor.java
// D. Singletary
// 3/5/23
// Process birthday cards

package edu.fscj.cop2805c.birthday;

import edu.fscj.cop2805c.message.MessageProcessor;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BirthdayCardProcessor extends Thread implements MessageProcessor {

    private ConcurrentLinkedQueue<BirthdayCard> safeQueue;
    private boolean stopped = false;

    public BirthdayCardProcessor(ConcurrentLinkedQueue<BirthdayCard> safeQueue) {
        this.safeQueue = safeQueue;

        // start polling (invokes run(), below)
        this.start();
    }

    // remove messages from the queue and process them
    public void processMessages() {
        System.out.println("before processing, queue size is " + safeQueue.size());
        safeQueue.stream().forEach(e -> {
                // Do something with each element
                e = safeQueue.remove();
                System.out.print(e);
        });
        System.out.println("after processing, queue size is now " + safeQueue.size());
    }

    // allow external class to stop us
    public void endProcessing() {
        this.stopped = true;
        interrupt();
    }

    // poll queue for cards
    public void run() {
        final int SLEEP_TIME = 1000; // ms
        while (true) {
            try {
                processMessages();
                Thread.sleep(SLEEP_TIME);
                System.out.println("polling");
            } catch (InterruptedException ie) {
                // see if we should exit
                if (this.stopped == true) {
                    System.out.println("poll thread received exit signal");
                    break;
                }
            }
        }
    }
}
