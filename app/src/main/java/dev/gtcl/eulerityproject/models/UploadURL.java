package dev.gtcl.eulerityproject.models;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

public class UploadURL {
    private String url;

    @NotNull
    public String getUrl(){
        return url;
    }

    public void setUrl(@NonNull String url) {
        this.url = url;
    }
}
