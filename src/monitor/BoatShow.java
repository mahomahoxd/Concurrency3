package monitor;


import model.Buyer;
import model.Person;
import model.Viewer;

import java.util.concurrent.locks.*;

public class BoatShow {

    private final int maxUsers;                                                 //Max users that can enter the boatshow simuntaniously
    private int successiveBuyers;                                               //The successive buyers that have been to the shop and bought a yacht
    private int numberOfUsersInShow;                                            //The users that are in the show
    private boolean allViewersSent;                                             //Special condition

    private int numbersOfViewersInQueue;                                        //Number of viewers that are in queue
    private int numbersOfBuyersInQueue;                                         //Number of buyers in queue

    private Lock lock;                                                          //The lock for threads
    private Condition nextBuyer,nextViewer;                                     //The conditions for the threads
    public BoatShow(int maxUsers)
    {
        this.maxUsers = maxUsers;
        this.successiveBuyers = 0;
        this.numbersOfViewersInQueue = 0;
        this.numbersOfBuyersInQueue = 0;

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
            if (person instanceof Buyer) {
                numbersOfBuyersInQueue++;
                System.out.println("Number of buyers in queue " + numbersOfBuyersInQueue);
                while (hasUsersInShow() || allViewersSent) {
                    System.out.println(person.toString() + " waiting to get invited");
                    nextBuyer.await();
                }
                numbersOfBuyersInQueue--;
                numberOfUsersInShow++;
                System.out.println(person.toString() + " invited");

            } else if (person instanceof Viewer) {
                numbersOfViewersInQueue++;
                System.out.println("Number of viewers in queue " + numbersOfViewersInQueue);
                while(hasBuyersInQueue() || noRoomLeft() || !allViewersSent)
                {
                    System.out.println(hasBuyersInQueue() + " " + noRoomLeft() + " " + allViewersSent);
                    System.out.println(person.toString() + " waiting to get invited");
                    nextViewer.await();
                }
                numbersOfViewersInQueue--;
                numberOfUsersInShow++;
                System.out.println(person.toString() + " invited");

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
        numberOfUsersInShow--;
        if(user instanceof Buyer && isSuccessiveBuyersMet())
        {
            successiveBuyers = 0;
            sendAllViewersToShow();
            return;
        }
        System.out.println("Number of people in the show : " + numberOfUsersInShow);
        if(!hasUsersInShow())
        {
            allViewersSent = false;
            System.out.println(allViewersSent);
            if(numbersOfBuyersInQueue >0)
            {
                nextBuyer.signal();
            }
            else
            {
                nextViewer.signalAll();
            }
        }
        lock.unlock();
        System.out.println(user.toString() + " HAS LEFT DA BUILDIN.");
    }

    public boolean isSuccessiveBuyersMet()
    {
        return successiveBuyers == 4;
    }

    public boolean hasUsersInShow()
    {
        return numberOfUsersInShow > 0;
    }

    public boolean hasBuyersInQueue()
    {
        return numbersOfBuyersInQueue > 0;
    }

    public boolean noRoomLeft()
    {
        return numberOfUsersInShow == maxUsers;
    }

    public boolean hasUsersInQueue()
    {
        return numbersOfViewersInQueue + numbersOfBuyersInQueue == 0;
    }

    public void sendAllViewersToShow()
    {

        allViewersSent = true;
        System.out.println(allViewersSent);
        nextViewer.signalAll();
    }

}
