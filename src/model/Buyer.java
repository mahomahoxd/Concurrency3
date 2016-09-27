package model;

public class Buyer extends Person {
    private int balance;
    public Buyer(int id,String name,String lastname,int balance)
    {
        super(id,name,lastname);
        this.balance = balance;

    }
}
