package fi.raumankonepaja.deliverylogger;

/**
 * Created by Sami on 11.10.2017.
 */

public class ListItem {

    private int deliveryNumber;

    public ListItem() {
    }

    public ListItem(int deliveryNumber) {
        this.deliveryNumber = deliveryNumber;
    }

    public int getDeliveryNumber() {
        return deliveryNumber;
    }
}