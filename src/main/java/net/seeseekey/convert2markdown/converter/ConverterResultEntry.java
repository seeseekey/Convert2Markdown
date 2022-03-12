package net.seeseekey.convert2markdown.converter;

public class ConverterResultEntry {

    private String id;
    private String author;

    private int year;
    private int month;
    private int day;

    private int hour;
    private int minute;

    private final String content;

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public String getContent() {
        return content;
    }

    public ConverterResultEntry(String content) {
        this.content = content;
    }

    public ConverterResultEntry(String id, String author, int year, int month, int day, int hour, int minute, String content) {
        this.id = id;
        this.author = author;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.content = content;
    }
}
