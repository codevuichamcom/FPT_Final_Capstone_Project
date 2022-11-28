package com.gasstation.managementsystem.utils;

import com.gasstation.managementsystem.entity.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenUtilTest {

    private JwtTokenUtil jwtTokenUtil;

    public JwtTokenUtilTest() {
        jwtTokenUtil = new JwtTokenUtil();
        jwtTokenUtil.setSecret("secret_key");
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUsernameFromToken() {
        String token = jwtTokenUtil.generateToken("admin", JwtTokenUtil.REFRESH_TOKEN_EXPIRED);
        assertEquals("admin", jwtTokenUtil.getUsernameFromToken(token));
    }

    @Test
    void getExpirationDateFromToken() {
        String token = jwtTokenUtil.generateToken("admin", JwtTokenUtil.REFRESH_TOKEN_EXPIRED);
        assertEquals(new Date(System.currentTimeMillis() + JwtTokenUtil.REFRESH_TOKEN_EXPIRED * 1000).toString(), jwtTokenUtil.getExpirationDateFromToken(token).toString());
    }

    @Test
    void getClaimFromToken() {
        String token = jwtTokenUtil.generateToken("admin", JwtTokenUtil.REFRESH_TOKEN_EXPIRED);
        jwtTokenUtil.getClaimFromToken(token, Claims::getSubject);
    }

    @Test
    void generateToken() {
        String token = jwtTokenUtil.generateToken("admin", JwtTokenUtil.REFRESH_TOKEN_EXPIRED);
        assertTrue(token.length() > 0);
    }

    @Test
    void validateToken() {
        String token = jwtTokenUtil.generateToken("admin", JwtTokenUtil.REFRESH_TOKEN_EXPIRED);
        User user = User.builder()
                .username("admin")
                .active(true).build();
        assertTrue(jwtTokenUtil.validateToken(token,user));
    }
}