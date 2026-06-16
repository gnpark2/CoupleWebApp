package com.coupleapp.userservice.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data public class UpdateProfileRequest{@Size(min=2,max=20) private String nickname;@Size(max=500) private String bio;private String timezone;private String city;private String profileImageUrl;}
