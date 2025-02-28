package com.ced.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import static com.ced.constants.ApplicationConstants.USERS;

import java.util.Date;
import java.util.Objects;

@Setter
@Getter
@Document(collection = USERS)
public class User {

    @Id
    private String id;

    @Size(min = 4, max = 100, message = "O campo 'name' deve ter entre 4 e 100 caracteres")
    @NotBlank(message = "O campo 'name' nao pode ser nulo ou vazio")
    private String name;
    @Indexed(unique = true)
    @Size(min = 4, max = 100, message = "O campo 'email' deve ter entre 4 e 100 caracteres")
    @NotBlank(message = "O campo 'email' nao pode ser nulo ou vazio")
    private String email;
    @Size(min = 4, max = 100, message = "O campo 'password' deve ter entre 4 e 100 caracteres")
    @NotBlank(message = "O campo 'password' nao pode ser nulo ou vazio")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    
    private boolean emailVerified = false;
    private String verificationToken;
    private Date verificationTokenExpiry;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
