package com.coupleapp.coupleservice.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data public class JoinRequest{@NotBlank @Size(min=6,max=6) private String inviteCode;}
