package com.ibrahimokic.ordermanagement.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDto {
    @JsonProperty("username")
    @NotNull
    private String username;

    @JsonProperty(value = "password", access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    private String password;

    @JsonProperty("email")
    @NotNull
    private String email;

    @JsonProperty("role")
    @NotNull
    private String role;

    @JsonProperty("first_name")
    @NotNull
    private String firstName;

    @JsonProperty("last_name")
    @NotNull
    private String lastName;

    @JsonProperty("birth_date")
    @NotNull
    private LocalDate birthDate;

    @JsonProperty("street")
    @NotNull
    private String addressStreet;

    @JsonProperty("zip_code")
    @NotNull
    private String addressZip;

    @JsonProperty("city")
    @NotNull
    private String addressCity;

    @JsonProperty("country")
    @NotNull
    private String addressCountry;
}
