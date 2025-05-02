package dev.bhargav.security.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class UserDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -3131695631261244993L;

    private String username;
    private String password;
}
