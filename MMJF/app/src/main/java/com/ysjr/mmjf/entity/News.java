package com.ysjr.mmjf.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017-12-4.
 */

public class News implements Parcelable {
  public News() {

  }
  protected News(Parcel in) {
  }

  public static final Creator<News> CREATOR = new Creator<News>() {
    @Override public News createFromParcel(Parcel in) {
      return new News(in);
    }

    @Override public News[] newArray(int size) {
      return new News[size];
    }
  };

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
  }
}
