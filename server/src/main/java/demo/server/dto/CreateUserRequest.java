package demo.server.dto;

import lombok.Data;

@Data
public class CreateUserRequest {
    private String username;
    private String displayName;
}
