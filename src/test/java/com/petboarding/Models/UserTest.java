package com.petboarding.Models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {
    @Test
    void testSetters() {
        User user = new User();
        user.setRole("USER");

        assertEquals("USER", user.getRole());
    }
}
