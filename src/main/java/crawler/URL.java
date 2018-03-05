package crawler;

public class URL {
    static private int counter = 0;

    private String url;
    private int id;

    public URL(String url) {
        this.url = url;
        this.id = counter++;
    }

    public String getUrl() {
        return this.url;
    }

    public int getId() {
        return this.id;
    }
}
