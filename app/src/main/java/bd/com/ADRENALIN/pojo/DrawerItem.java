package bd.com.ADRENALIN.pojo;

/**
 * Created by mahfuz on 7/13/17.
 */

public class DrawerItem {

    private int imgIconId;
    private String name;

    public DrawerItem(int imgIconId, String name) {
        this.imgIconId = imgIconId;
        this.name = name;
    }

    public int getImgIconId() {
        return imgIconId;
    }

    public void setImgIconId(int imgIconId) {
        this.imgIconId = imgIconId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "DrawerItem{" +
                "imgIconId=" + imgIconId +
                ", name='" + name + '\'' +
                '}';
    }
}
