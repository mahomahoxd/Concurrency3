package monitor;


import model.Buyer;
import model.Person;
import model.Viewer;

import java.util.concurrent.locks.*;

public class BoatShow {

    private final int maxUsers;                                                 //Max users that can enter the boatshow simuntaniously
    private int successiveBuyers;                                               //The successive buyers that have been to the shop and bought a yacht
    private int numberOfBuyersInShow;
    private int numberOfViewersInShow;                                          //The users that are in the show

    private boolean allViewersSent;                                             //Special condition
    private int numberOfViewersSpecial;

    private int numbersOfViewersInQueue;                                        //Number of viewers that are in queue
    private int numbersOfBuyersInQueue;                                         //Number of buyers in queue

    private Lock lock;                                                          //The lock for threads
    private Condition nextBuyer,nextViewer;                                     //The conditions for the threads
    public BoatShow(int maxUsers)
    {
        successiveBuyers = 0;
        numberOfBuyersInShow = 0;
        numberOfViewersInShow = 0;
        numberOfViewersSpecial = 0;
        numbersOfViewersInQueue = 0;
        numbersOfBuyersInQueue = 0;
        allViewersSent = false;

        this.maxUsers = maxUsers;
        lock = new ReentrantLock();
        nextBuyer = lock.newCondition();
        nextViewer = lock.newCondition();

        System.out.println("The hiswa boat show has started!");
        System.out.println("It has a max capacity of : " + maxUsers + " users.");
    }

    /**
     * People will call this method to get admitted
     **/
    public void getAdmitted(Person person) throws InterruptedException
    {
        lock.lock();
        try {
            //If the user in the queue is a Buyer.
            if (person instanceof Buyer) {
                //+1 on the buyers queue length
                numbersOfBuyersInQueue++;

                //Stuck in a loop if :
                //Buyers are in the show
                //Has viewers in the show
                //If special condition has occured.
                while (hasBuyersInShow() || hasViewersInShow() || allViewersSent) {
                    System.out.println(person.toString() + " is waiting");
                    nextBuyer.await();
                }
                System.out.println(person.toString() + " invited");
                //Invited so buyers in queue is one less, +1 to buyers in the show.
                System.out.println("Number of viewers in show : " + numberOfViewersInShow);
                numbersOfBuyersInQueue--;
                numberOfBuyersInShow++;

            }
            //If the person is a Viewer
            else if (person instanceof Viewer) {
                //+1 to viewers in the queue
                numbersOfViewersInQueue++;

                //Stuck in here if :
                //There are buyers in the show
                //If the buyers are in queue but if all viewers are sent ignore that
                //If the special condition applies, checks if the user was on the invite list to

                while(hasBuyersInShow() || (hasBuyersInQueue() && !allViewersSent) || noRoomLeft()) {
                    System.out.println(person.toString() + " is waiting");
                    nextViewer.await();
                }
                System.out.println(person.toString() + " invited");
                numbersOfViewersInQueue--;
                numberOfViewersInShow++;
            }
        }
        finally{lock.unlock();}
    }

    /**
     * The method called by buyers when they bought a yacht or not.
     * @param bought If the buyer bought the yacht or nah
     * @throws InterruptedException
     */
    public void buyYacht(boolean bought) throws InterruptedException
    {
        lock.lock();
        try
        {
            if(bought)
                successiveBuyers++;

            System.out.println("Number of successive buyers : " + successiveBuyers);
        }
        finally
        {
            lock.unlock();
        }
    }

    public void leaveShow(Person user)
    {
        lock.lock();
        try {
            if (user instanceof Buyer) {
                numberOfBuyersInShow--;
                if (isSuccessiveBuyersMet()) {
                    successiveBuyers = 0;
                    sendAllViewersToShow();
                }
            } else if (user instanceof Viewer) {
                numberOfViewersInShow--;
                if(isSpecialConditionViewer()) {
                    numberOfViewersSpecial--;
                }
                if (!hasViewersInShow()) {
                    //just in case if it was true
                    allViewersSent = false;
                }
            }
            //Normal rules apply
            if (!allViewersSent) {
                if (numbersOfBuyersInQueue > 0) {
                    nextBuyer.signal();
                } else {
                    nextViewer.signalAll();
                }
            }
        }
        finally {
            lock.unlock();
        }
        System.out.println(user.toString() + " has left the HISWA.");
    }

    private boolean isSuccessiveBuyersMet()
    {
        return successiveBuyers == 4;
    }

    private boolean hasViewersInShow()
    {
        return numberOfViewersInShow > 0;
    }

    private boolean hasBuyersInShow()
    {
        return numberOfBuyersInShow > 0;
    }

    private boolean hasBuyersInQueue()
    {
        return numbersOfBuyersInQueue > 0;
    }

    private boolean noRoomLeft()
    {
        if(allViewersSent) {
            return false;
        }
        else if(numberOfBuyersInShow > 0) {
            return true;
        }
        else if(numberOfViewersInShow == maxUsers) {
            return true;
        }
        else {
            return false;
        }
    }

    private boolean isSpecialConditionViewer()
    {
        if(numberOfViewersSpecial > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private void sendAllViewersToShow()
    {
        allViewersSent = true;
        System.out.println(numberOfViewersSpecial + " " + numbersOfViewersInQueue);
        numberOfViewersSpecial = numbersOfViewersInQueue;
        nextViewer.signalAll();
    }
}
