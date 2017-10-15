package fi.raumankonepaja.deliverylogger;

/**
 * Created by Sami on 11.10.2017.
 */

public class ListItem implements Comparable<ListItem> {

    private int deliveryNumber;

    public ListItem() {
    }

    public ListItem(int deliveryNumber) {
        this.deliveryNumber = deliveryNumber;
    }

    public int getDeliveryNumber() {
        return deliveryNumber;
    }

    @Override
    public int compareTo(ListItem o) {
        int compareDeliveryNumber = o.getDeliveryNumber();

        if(deliveryNumber<compareDeliveryNumber){
            return -1;
        } else if (deliveryNumber==compareDeliveryNumber){
            return 0;
        } else {
            return 1;
        }

    }
}