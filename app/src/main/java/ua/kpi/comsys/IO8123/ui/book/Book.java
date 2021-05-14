package  ua.kpi.comsys.IO8123.ui.book;


public class Book {

    private final String title;
    private final String subtitle;
    private String authors;
    private String publisher;
    private final String isbn13;
    private String pages;
    private String year;
    private String rating;
    private String desc;
    private final String price;
    private final String imagePath;

    public Book(String title, String subtitle, String isbn13, String price, String imagePath){
        this.title = title;
        this.subtitle = subtitle;
        this.isbn13 = isbn13;
        this.price = price;
        this.imagePath = imagePath;
    }

    public Book(String title, String subtitle, String price){
        this.title = title;
        this.subtitle = subtitle;
        this.isbn13 = "noid";
        this.price = price;
        this.imagePath = "";
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getAuthors() {
        return authors;
    }

    public String getDesc() {
        return desc;
    }

    public String getPages() {
        return pages;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getRating() {
        return rating;
    }

    public String getYear() {
        return year;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public String getPrice() {
        return price;
    }

    public String getImagePath() {
        return imagePath;
    }
}