package model;

public class Person extends Thread {
    private int id;
    private String name;

    public Person(int id,String name)
    {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }

    public void justLive()
    {
        try
        {
            System.out.println(toString() + " has started living");
            Thread.sleep((int)(Math.random() * 5000));
        }
        catch(InterruptedException iex) {}
    }
}
