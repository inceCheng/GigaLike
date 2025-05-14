package com.ince.gigalike.model.dto;

import lombok.Data;

@Data
public class DoThumbRequest {  
    private Long blogId;  
    
    // Explicit getter and setter for blogId field
    public Long getBlogId() {
        return blogId;
    }
    
    public void setBlogId(Long blogId) {
        this.blogId = blogId;
    }
}
