package demo.server.controller;

import demo.server.dto.*;
import demo.server.model.User;
import demo.server.repo.UserRepository;
import demo.server.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final ChatService chatService;
    private final UserRepository userRepo;

    @PostMapping
    public UserDto createUser(@RequestBody CreateUserRequest req) {
        User u = chatService.createUser(req.getUsername(), req.getDisplayName());
        return Mappers.toDto(u);
    }

    @GetMapping
    public List<UserDto> allUsers() {
        return userRepo.findAll().stream().map(Mappers::toDto).toList();
    }
}
