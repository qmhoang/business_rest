package business.model;

import lombok.Value;

import java.io.Serializable;

/**
 * Created by Quang-Minh on 4/16/2016.
 */
@Value
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
}