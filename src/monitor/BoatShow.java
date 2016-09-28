package monitor;


import model.Buyer;
import model.Person;
import model.Viewer;

import javax.swing.text.View;
import java.util.concurrent.locks.*;

public class BoatShow {

    private final int maxUsers;             //Max users that can enter the boatshow simuntaniously
    private int successiveBuyers;           //The successive buyers that have been to the shop and bought a yacht
    private int numberOfUsersInShow;        //The users that are in the show

    private int numbersOfViewersInQueue;    //Number of viewers that are in queue
    private int numbersOfBuyersInQueue;     //Number of buyers in queue

    private Lock lock;                      //The lock for threads
    private Condition nextBuyer,nextViewer;            //The conditions for the threads
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
        if(person instanceof Buyer)
        {

            //admit the buyer
            lock.lock();
            numberOfUsersInShow++;
            lock.unlock();
        }
        else if(person instanceof Viewer)
        {

        }
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
        lock.unlock();
        System.out.println(user.toString() + " HAS LEFT DA BUILDIN.");
    }

}
