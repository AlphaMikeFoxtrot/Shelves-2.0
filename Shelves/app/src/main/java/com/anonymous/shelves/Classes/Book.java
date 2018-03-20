package com.anonymous.shelves.Classes;

/**
 * Created by ANONYMOUS on 20-Mar-18.
 */

public class Book {

    private String name, author, ratings, ratingsCount, imageUrl, description, isbn, pageCount, publishedDate, genre;

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setRatings(String ratings) {
        this.ratings = ratings;
    }

    public void setRatingsCount(String ratingsCount) {
        this.ratingsCount = ratingsCount;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setPageCount(String pageCount) {
        this.pageCount = pageCount;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getRatings() {
        return ratings;
    }

    public String getRatingsCount() {
        return ratingsCount;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getPageCount() {
        return pageCount;
    }

    public String getPublishedDate() {
        return publishedDate;
    }
}
