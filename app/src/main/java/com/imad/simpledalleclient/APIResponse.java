package com.imad.simpledalleclient;

import java.util.List;

public class APIResponse {
    private List<String> images;
    private String version;

    public APIResponse(){}

    public APIResponse(List<String> images, String version){
        this.images = images;
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
