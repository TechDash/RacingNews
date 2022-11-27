package com.techdash.racingnews;

import java.time.LocalDateTime;

public class News {

  private String title;
  private String url;
  private String image;
  private String addedDate;
  private String category;
  private int opened;

  public News(String titleC, String urlC, String imageC, String category) {
    title = titleC;
    url = urlC;
    image = imageC;
    this.category = category;
    addedDate = LocalDateTime.now().toString();
    opened = 0;
  }

  public News(String titleC, String urlC, String imageC, String dateTime, String category, int openedC) {
    title = titleC;
    url = urlC;
    image = imageC;
    addedDate = dateTime;
    this.category = category;
    opened = openedC;
  }

  public News(String titleC, String urlC, String category) {
    title = titleC;
    url = urlC;
    this.category = category;
    addedDate = LocalDateTime.now().toString();
  }

  public News() {
  }

  public String getTitle() {
    return title;
  }

  public String getUrl() {
    return url;
  }

  public String getImage() {
    return image;
  }

  public String getAddedDate() {
    return addedDate;
  }

  public int getOpened() {
    return opened;
  }

  public String getCategory() {
    return category;
  }
}
