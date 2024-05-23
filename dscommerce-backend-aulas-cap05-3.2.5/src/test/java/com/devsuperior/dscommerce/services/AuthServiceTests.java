package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.factory.UserFactory;
import com.devsuperior.dscommerce.services.exceptions.ForbiddenException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class AuthServiceTests {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserService userService;

    private User adminClient;
    private User selfClient;
    private User otherClient;

    @BeforeEach
    void Setup() throws Exception {
        adminClient = UserFactory.createAdminUser();
        selfClient = UserFactory.createCustomClientUser(1L, "Bob Blue");
        otherClient = UserFactory.createCustomClientUser(2L, "Ana Yellow");
    }

    @Test
    public void validateSelfOrAdminShouldDoNothingWhenAdminLogged () {

        when(userService.authenticated()).thenReturn(adminClient);

        long UserId = adminClient.getId();

        Assertions.assertDoesNotThrow(() -> {
            authService.validateSelfOrAdmin(UserId);
        });
    }

    @Test
    public void validateSelfOrAdminShouldDoNothingWhenSelfLogged () {
        when(userService.authenticated()).thenReturn(selfClient);

        long UserId = selfClient.getId();

        Assertions.assertDoesNotThrow(() -> {
            authService.validateSelfOrAdmin(UserId);
        });
    }

    @Test
    public void validateSelfOrAdminShouldThrowForbiddenExceptionWhenOtherClientLogged() {
        when(userService.authenticated()).thenReturn(selfClient);

        long UserId = otherClient.getId();

       Assertions.assertThrows(ForbiddenException.class, () -> {
           authService.validateSelfOrAdmin(UserId);
       });
    }
}
