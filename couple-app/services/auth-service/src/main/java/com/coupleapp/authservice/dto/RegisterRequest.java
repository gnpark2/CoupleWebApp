package com.coupleapp.authservice.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data public class RegisterRequest { @Email @NotBlank private String email; @NotBlank @Size(min=8) private String password; @NotBlank @Size(min=2,max=20) private String nickname; }
