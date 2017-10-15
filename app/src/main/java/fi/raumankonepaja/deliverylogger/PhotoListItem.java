package fi.raumankonepaja.deliverylogger;

/**
 * Created by Sami on 11.10.2017.
 */

public class PhotoListItem {

    public int deliveryNumber;
    public int deliveryPos;
    public String pictureFileName;
    public String dateAndTime;
    public int rotation;

    public PhotoListItem(int deliveryNumber, int deliveryPos, String pictureFileName, String dateAndTime, int rotation) {
        this.deliveryNumber = deliveryNumber;
        this.deliveryPos = deliveryPos;
        this.pictureFileName = pictureFileName;
        this.dateAndTime = dateAndTime;
        this.rotation = rotation;
    }

    public PhotoListItem() {

    }

    public int getDeliveryNumber() {
        return deliveryNumber;
    }

    public int getDeliveryPos() {
        return deliveryPos;
    }

    public String getPictureFileName() {
        return pictureFileName;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public int getRotation() {
        return rotation;
    }
}