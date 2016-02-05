package contrailtech.com.imageoftheday;

/**
 * Created by serious on 29/1/16.
 */
public class ItemClass {

    private String name;
    private String description;
    private long date;
    private String imageUrl;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "\nApodFeedItem [\nname=" + name + ", \ndescription=" + description
                + ", \ndate=" + date + ", \nimageUrl=" + imageUrl + "\n]";
    }


}
