package com.devsuperior.dscommerce.factory;

import com.devsuperior.dscommerce.dto.UserDTO;
import com.devsuperior.dscommerce.entities.Role;
import com.devsuperior.dscommerce.entities.User;

import java.time.LocalDate;

public class UserFactory {

    public static UserDTO createClientUserDTO() {
        UserDTO userDTO = new UserDTO(createClientUser());
        return userDTO;
    }

    public static User createClientUser() {
        User user = new User(1L, "Maria Brow", "maria@gmail.com",
                "988888888", LocalDate.parse("1998-05-02"), "$2a$10$N7SkKCa3r17ga.i.dF9iy.BFUBL2n3b6Z1CWSZWi/qy7ABq/E6VpO");
        user.addRole(new Role(1L, "ROLE_CLIENT"));
        return user;
    }

    public static User createAdminUser() {
        User user = new User(2L, "Alex Green", "alex@gmail.com",
                "988888888", LocalDate.parse("1998-05-02"), "$2a$10$N7SkKCa3r17ga.i.dF9iy.BFUBL2n3b6Z1CWSZWi/qy7ABq/E6VpO");
        user.addRole(new Role(2L, "ROLE_ADMIN"));
        return user;
    }

    public static User createCustomClientUser(Long id, String username) {
        User user = new User(id, "Maria Brow", username,
                "988888888", LocalDate.parse("1998-05-02"), "$2a$10$N7SkKCa3r17ga.i.dF9iy.BFUBL2n3b6Z1CWSZWi/qy7ABq/E6VpO");
        user.addRole(new Role(1L, "ROLE_CLIENT"));
        return user;
    }

    public static User createCustomAdminUser(Long id, String username) {
        User user = new User(id, "Alex Green", username,
                "988888888", LocalDate.parse("1998-05-02"), "$2a$10$N7SkKCa3r17ga.i.dF9iy.BFUBL2n3b6Z1CWSZWi/qy7ABq/E6VpO");
        user.addRole(new Role(2L, "ROLE_ADMIN"));
        return user;
    }
}


