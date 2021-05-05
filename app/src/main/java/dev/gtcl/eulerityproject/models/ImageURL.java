package dev.gtcl.eulerityproject.models;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class ImageURL implements Serializable {
    private String url;
    private String created;
    private String updated;

    @NotNull
    public String getUrl(){
        return url;
    }

    public void setUrl(@NotNull String url){
        this.url = url;
    }

    @NotNull
    public String getCreated(){
        return created;
    }

    public void setCreated(@NotNull String created){
        this.created = created;
    }

    @NotNull
    public String getUpdated() {
        return updated;
    }

    public void setUpdated(@NotNull String updated){
        this.updated = updated;
    }

    public boolean equals(ImageURL other) {
        if(other == null){
            return false;
        }
        return this.url.equals(other.getUrl()) && this.created.equals(other.getCreated()) && this.updated.equals(other.getUpdated());
    }
}
