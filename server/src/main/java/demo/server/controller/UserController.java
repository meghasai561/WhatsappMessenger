package demo.server.controller;

import demo.server.dto.CreateUserRequest;
import demo.server.dto.UserDto;
import demo.server.model.User;
import demo.server.repo.UserRepo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepo userRepo;
    public UserController(UserRepo userRepo){ this.userRepo = userRepo; }

    @PostMapping
    public UserDto create(@RequestBody CreateUserRequest req){
        var u = userRepo.save(User.builder().username(req.username()).displayName(req.displayName()).build());
        return new UserDto(u.getId(), u.getUsername(), u.getDisplayName());
    }

    @GetMapping
    public List<UserDto> all(){
        return userRepo.findAll().stream().map(u-> new UserDto(u.getId(), u.getUsername(), u.getDisplayName())).toList();
    }
}
