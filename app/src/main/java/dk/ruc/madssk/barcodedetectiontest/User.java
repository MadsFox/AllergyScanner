package dk.ruc.madssk.barcodedetectiontest;

/**
 * Created by Computer on 13-04-2016.
 */
public class User {
    private static long id;
    private String name;

    public User(long id, String name){
        this.id = id;
        this.name = name;
    }

    public static long getId() {
        return id;
    }

    public static void setId(long id) {
        User.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
