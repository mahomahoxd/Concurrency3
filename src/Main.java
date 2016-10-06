import model.Buyer;
import model.Viewer;
import monitor.BoatShow;

public class Main {
    public static void main(String[] args)
    {
        new Main().run();
    }

    public void run()
    {
        //10 users is the max capacity
        BoatShow boatShow = new BoatShow(10);
        Thread[] buyers;
        Thread[] viewers;

        buyers = new Thread[2]; //2 buyers
        viewers = new Thread[10]; //10 viewers

        for (int i = 0; i < buyers.length; i++) {
            buyers[i] = new Buyer(i,"buyer" + i,boatShow);
            buyers[i].start();
        }
        for (int i = 0; i < viewers.length; i++) {
            viewers[i] = new Viewer(i,"viewer" + i,boatShow);
            viewers[i].start();
        }

    }
}
