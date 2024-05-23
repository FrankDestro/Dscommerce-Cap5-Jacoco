package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.dto.UserDTO;
import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.factory.UserDetailsFactory;
import com.devsuperior.dscommerce.factory.UserFactory;
import com.devsuperior.dscommerce.projections.UserDetailsProjection;
import com.devsuperior.dscommerce.repositories.UserRepository;
import com.devsuperior.dscommerce.util.CustomUserUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class UserServiceTests {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomUserUtil customUserUtil;

    private String existingUserName;
    private String nonExistingUserName;
    private User user;
    private List<UserDetailsProjection> userDetails;
    private UserService userServiceSpy;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() throws Exception {

        existingUserName = "maria@gmail.com";
        nonExistingUserName = "user@gmail.com";
        user = UserFactory.createCustomClientUser(1L, existingUserName);
        userDetails = UserDetailsFactory.createCustomAdminUser(existingUserName);
        userServiceSpy = spy(userService);
        userDTO = UserFactory.createClientUserDTO();

        when(userRepository.searchUserAndRolesByEmail(existingUserName)).thenReturn(userDetails);
        when(userRepository.searchUserAndRolesByEmail(nonExistingUserName)).thenReturn(new ArrayList<>());

        when(userRepository.findByEmail(existingUserName)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(nonExistingUserName)).thenReturn(Optional.empty());

        when(customUserUtil.getLoggedUserName()).thenReturn(existingUserName);

        when(userServiceSpy.authenticated()).thenReturn(user);
    }

    @Test
    public void loadUserByUsernameShouldReturnUserDetailsWhenUserNameIsExists() {
        UserDetails result = userService.loadUserByUsername(existingUserName);
        assertNotNull(result);
        verify(userRepository, times(1)).searchUserAndRolesByEmail(existingUserName);
    }

    @Test
    public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserNameDoesNotExists() {
        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(nonExistingUserName);
        });
    }

    @Test
    public void authenticatedShouldReturnUserWhenExistingUserNameExists() {
        User result = userService.authenticated();
        assertNotNull(result);
    }

    @Test
    public void authenticatedShouldReturnThrowUsernameNotFoundExceptionWhenNonExistingUserNameDoesNotExists() {
        when(customUserUtil.getLoggedUserName()).thenThrow(UsernameNotFoundException.class);
        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            userService.authenticated();
        });
    }

    @Test
    public void getMeShouldReturnUserDTWhenUserAuthenticated() {
        userDTO = userServiceSpy.getMe();
        assertNotNull(userDTO);
        assertEquals(userDTO.getEmail(), existingUserName);
    }

    @Test
    public void getMeShouldThrowUsernameNotFoundExceptionWhenUserNotAuthenticated() {
        doThrow(UsernameNotFoundException.class).when(userServiceSpy).authenticated();
        assertThrows(UsernameNotFoundException.class, () -> {
            userServiceSpy.getMe();
        });
    }
}
