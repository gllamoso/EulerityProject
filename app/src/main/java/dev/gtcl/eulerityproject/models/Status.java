package dev.gtcl.eulerityproject.models;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

public class Status {
    private String status;

    @NotNull
    public String getStatus(){
        return status;
    }

    public void setStatus(@NonNull String status) {
        this.status = status;
    }
}
