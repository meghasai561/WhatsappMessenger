package demo.server.dto;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String displayName;
}
