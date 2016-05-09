package business.model;

import java.io.Serializable;

/**
 * Created by Quang-Minh on 4/16/2016.
 */
public class Business implements Serializable {
  private int    id;
  private String uuid;
  private String name;

  private String address;
  private String address2;

  private String city;
  private String state;

  private String zip;
  private String country;

  private String phone;
  private String website;

  private String created_at;


  public Business() { }

  public Business(int id, String uuid, String name, String address, String address2, String city, String state, String zip, String country, String phone, String website, String created_at) {
    this.id = id;
    this.uuid = uuid;
    this.name = name;
    this.address = address;
    this.address2 = address2;
    this.city = city;
    this.state = state;
    this.zip = zip;
    this.country = country;
    this.phone = phone;
    this.website = website;
    this.created_at = created_at;
  }

  public int getId() {
    return this.id;
  }

  public String getUuid() {
    return this.uuid;
  }

  public String getName() {
    return this.name;
  }

  public String getAddress() {
    return this.address;
  }

  public String getAddress2() {
    return this.address2;
  }

  public String getCity() {
    return this.city;
  }

  public String getState() {
    return this.state;
  }

  public String getZip() {
    return this.zip;
  }

  public String getCountry() {
    return this.country;
  }

  public String getPhone() {
    return this.phone;
  }

  public String getWebsite() {
    return this.website;
  }

  public String getCreated_at() {
    return this.created_at;
  }
}