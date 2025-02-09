package vttp.ws.ws26.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ReviewForm {

    @NotBlank(message = "Field cannot be left empty.")
    private String user;

    @NotBlank(message = "Field cannot be left empty.")
    private String comment;

    // @NotNull(message = "Field cannot be left empty.")
    private Integer gid;

    // @NotNull(message = "Field cannot be left empty.")
    @Min(value = 0, message = "Rating cannot be lower than 0.")
    @Max(value = 10, message = "Rating cannot be more than 10.")
    private Integer rating;
    
    public Integer getRating() {
        return rating;
    }
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public Integer getGid() {
        return gid;
    }
    public void setGid(Integer gid) {
        this.gid = gid;
    }
    @Override
    public String toString() {
        return "ReviewForm [user=" + user + ", comment=" + comment + ", gid=" + gid + ", rating=" + rating + "]";
    }
    

    
}
