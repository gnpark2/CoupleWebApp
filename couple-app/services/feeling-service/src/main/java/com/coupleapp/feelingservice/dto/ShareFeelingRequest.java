package com.coupleapp.feelingservice.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data public class ShareFeelingRequest{@NotBlank @Size(max=10) private String moodEmoji;@NotBlank @Size(max=50) private String moodLabel;@Size(max=200) private String comment;}
