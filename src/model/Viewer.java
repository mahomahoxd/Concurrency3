package model;

import monitor.BoatShow;

public class Viewer extends Person {
    private BoatShow boatShow;
    public Viewer(int id, String name, BoatShow boatShow)
    {
        super(id,name);
        this.boatShow = boatShow;
    }

    public void run(){
        while(true) {
            try{
                justLive();
                boatShow.getAdmitted(this);
                viewAllBoats();
                boatShow.leaveShow(this);
            }
            catch(InterruptedException iex)
            {

            }
        }
    }

    private void viewAllBoats()
    {
        try
        {
            System.out.println(toString() + " has started on the tour.");
            Thread.sleep((int)(Math.random() * 5000));
        }
        catch(InterruptedException iex) {}
    }
}
