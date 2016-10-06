package monitor;


import model.Buyer;
import model.Person;
import model.Viewer;

import java.util.concurrent.locks.*;

public class BoatShow {

    private int maxUsers;                           //The number of users that can be in the show.
    private int successiveBuyers;                   //The number of successive buyers that have bought a yacht.
    private int numberOfBuyersInShow;               //The number of buyers that are in the show
    private int numberOfBuyersInQueue;              //The number of buyers in the queue
    private int numberOfViewersInShow;              //The number of viewers that are in the show
    private int numberOfViewersInQueue;             //The number of viewers in the queue
    private int numberOfViewersSpecial;             //The number of viewers that are in under the special condition

    private Lock lock;                              //The lock

    private Condition nextBuyer,nextViewer;         //The conditions (nextBuyer is the queue for buyers and nextViewer is the queue for the viewers)
    public BoatShow(int maxUsers)
    {
        //Initializing everything
        successiveBuyers = 0;
        numberOfBuyersInShow = 0;
        numberOfViewersInShow = 0;
        numberOfViewersSpecial = 0;
        numberOfViewersInQueue = 0;
        numberOfBuyersInQueue = 0;

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
                numberOfBuyersInQueue++;

                //Stuck in a loop if :
                //Buyers are in the show
                //Has viewers in the show
                //If special condition has occured.
                while (hasBuyersInShow() || hasViewersInShow() || hasSpecialConditionViewers()) {
                    System.out.println(person.toString() + " is waiting");
                    nextBuyer.await();
                }
                System.out.println(person.toString() + " invited");
                //Invited so buyers in queue is one less, +1 to buyers in the show.
                System.out.println("Number of viewers in show : " + numberOfViewersInShow);
                numberOfBuyersInQueue--;
                numberOfBuyersInShow++;

            }
            //If the person is a Viewer
            else if (person instanceof Viewer) {
                //+1 to viewers in the queue
                numberOfViewersInQueue++;

                //Stuck in here if :
                //There are buyers in the show
                //If the buyers are in queue but if all viewers are sent ignore that
                //If the special condition applies, checks if the user was on the invite list
                //Checks if there is room
                while((hasBuyersInQueue() && !hasSpecialConditionViewers()) || noRoomLeft()) {
                    System.out.println(person.toString() + " is waiting");
                    nextViewer.await();
                }
                System.out.println(person.toString() + " invited");
                //If it is a special condition viewer, minus one on the counter
                if(hasSpecialConditionViewers())
                {
                    System.out.println(numberOfViewersSpecial);
                    numberOfViewersSpecial--;
                }
                //Lower the viewers in queue, but plus one on the ones in the show.
                numberOfViewersInQueue--;
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
            //If the buyer did buy a yacht then +1 to successive buyers
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
            //Checks whether its a buyer or a viewer
            if (user instanceof Buyer) {
                //If its a buyer, remove one from the show
                numberOfBuyersInShow--;
                //If the successive buyers count is met then send out all the viewers so that the special condition is true
                if (isSuccessiveBuyersMet()) {
                    successiveBuyers = 0;
                    sendAllViewersToShow();
                }
            }
            //Else if its a viewer then
            else if (user instanceof Viewer) {
                //Remove one from the show
                numberOfViewersInShow--;
            }
            //If the special condition isnt applied
            //Normal rules apply
            if (!hasSpecialConditionViewers()) {
                if (numberOfBuyersInQueue > 0) {
                    //Calls one buyer
                    nextBuyer.signal();
                } else {
                    //Calls all available viewers
                    nextViewer.signalAll();
                }
            }
        }
        finally {
            lock.unlock();
        }
        System.out.println(user.toString() + " has left the HISWA.");
    }

    /**
     * Checks if max successive buyers is met, in this assignment, 4
     * @return true if it is met, else false
     */
    private boolean isSuccessiveBuyersMet()
    {
        return successiveBuyers == 4;
    }

    /**
     * Checks whether the show has viewers in it
     * @return True if it does, else false
     */
    private boolean hasViewersInShow()
    {
        return numberOfViewersInShow > 0;
    }

    /**
     * Checks whether the show has buyers in it
     * @return True if it does, else false
     */
    private boolean hasBuyersInShow()
    {
        return numberOfBuyersInShow > 0;
    }

    /**
     * Checks whether the queue contains buyers
     * @return True if it does, else false
     */
    private boolean hasBuyersInQueue()
    {
        return numberOfBuyersInQueue > 0;
    }

    /**
     * Checks if there is room left
     * @return true if there isnt, false if there is room left
     */
    private boolean noRoomLeft()
    {
        //Checks if its a special condition first
        if(hasSpecialConditionViewers()) {
            return false;
        }
        //Checks if it has buyers in the show
        else if(hasBuyersInShow()) {
            return true;
        }
        //Checks if the viewers in the show have reached the max of the show
        else if(numberOfViewersInShow == maxUsers) {
            return true;
        }
        //If none of this is the case, return false
        else {
            return false;
        }
    }

    /**
     * Checks which amount can enter for the special condition
     * @return If it still does have space, it will return true, else false
     */
    private boolean hasSpecialConditionViewers()
    {
        return numberOfViewersSpecial > 0;
    }

    /**
     * Signals all viewers to wake up and sets the amount that was in queue at that time, to invite later.
     */
    private void sendAllViewersToShow()
    {
        numberOfViewersSpecial = numberOfViewersInQueue;
        nextViewer.signalAll();
    }
}
