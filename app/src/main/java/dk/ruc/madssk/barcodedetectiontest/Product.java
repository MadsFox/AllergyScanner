package dk.ruc.madssk.barcodedetectiontest;

import android.graphics.Bitmap;

/**
 * Created by Computer on 13-04-2016.
 */
public class Product {
    private long id;
    private String name;
    private String producer;
    private String EAN;
    private Bitmap image;
    private String creationDate;

    public Product(int id, String name, String producer, String EAN, String creationDate, Bitmap image){
        this.id = id;
        this.name = name;
        this.producer = producer;
        this.EAN = EAN;
        this.image = image;
        this.creationDate = creationDate;
    }
    public Product(int id, String name, String producer, String EAN, String creationDate){
        this.id = id;
        this.name = name;
        this.producer = producer;
        this.EAN = EAN;
        this.creationDate = creationDate;
    }
    public Product(String EAN){
        this.EAN = EAN;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getEAN() {
        return EAN;
    }

    public void setEAN(String EAN) {
        this.EAN = EAN;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
