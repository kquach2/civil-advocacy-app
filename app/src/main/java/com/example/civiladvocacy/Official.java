package com.example.civiladvocacy;

import java.io.Serializable;

public class Official implements Serializable {
    private String office;
    private String name;
    private String address;
    private String party;
    private String phoneNumber;
    private String website;
    private String email;
    private String photoURL;
    private String facebookID;
    private String twitterID;
    private String youtubeID;

    public Official(String office, String name, String address, String party, String phoneNumber, String website, String email, String photoURL, String facebookID, String twitterID, String youtubeID) {
        this.office = office;
        this.name = name;
        this.address = address;
        this.party = party;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.email = email;
        this.photoURL = photoURL;
        this.facebookID = facebookID;
        this.twitterID = twitterID;
        this.youtubeID = youtubeID;
    }

    public String getOffice() {
        return office;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getParty() {
        return party;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    public String getEmail() {
        return email;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public String getFacebookID() {
        return facebookID;
    }

    public String getTwitterID() {
        return twitterID;
    }

    public String getYoutubeID() {
        return youtubeID;
    }
}
