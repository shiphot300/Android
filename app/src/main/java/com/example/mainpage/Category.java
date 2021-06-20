package com.example.mainpage;

public class Category {
    private int categoryImg;
    private String categoryTitle;
    private String imageUrl;

    public Category(int categoryImg, String categoryTitle) {
        this.categoryImg = categoryImg;
        this.categoryTitle = categoryTitle;
    }

    public Category(String categoryTitle, String imageUrl) {
        this.categoryTitle = categoryTitle;
        this.imageUrl = imageUrl;
    }

    public int getCategoryImg() {
        return categoryImg;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
