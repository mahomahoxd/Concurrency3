package model;

import monitor.BoatShow;

public class Buyer extends Person {
    BoatShow boatShow;
    public Buyer(int id, String name, BoatShow boatShow)
    {
        super(id,name);
        this.boatShow = boatShow;
    }

    public void run(){
        while(true) {
            try{
                justLive();
                boatShow.getAdmitted(this);
                System.out.println(toString() + " has been admitted to the show");
                lookAtYachts();
                buyYacht();
                boatShow.leaveShow(this);
            }
            catch(InterruptedException iex){}
        }

    }



    public void lookAtYachts()
    {
        try
        {
            System.out.println(toString() + " is looking at yachts now");
            //Thread.sleep((int)(Math.random() * 5000));
            Thread.sleep(5000);
            System.out.println(toString() + " has finished looking at yachts, will decide to buy or not now.");
        }
        catch(InterruptedException iex) {}
    }


    private void buyYacht() throws InterruptedException
    {
        int randomNumber = (Math.random() <= 0.5) ? 1 :2;
        if(randomNumber == 1)
        {
            //Buy yacht;
            System.out.println(toString() + " bought a yacht.");
            boatShow.buyYacht(true);
        }
        if(randomNumber == 2)
        {
            //Dont buy yacht;
            System.out.println(toString() + " bought no yacht.");
            boatShow.buyYacht(false);
        }
    }
 }
