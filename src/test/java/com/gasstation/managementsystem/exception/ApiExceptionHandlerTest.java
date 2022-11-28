package com.gasstation.managementsystem.exception;

import com.gasstation.managementsystem.exception.custom.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class ApiExceptionHandlerTest {

    @InjectMocks
    ApiExceptionHandler apiExceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void duplicateFieldException() {
        apiExceptionHandler.duplicateFieldException(Mockito.mock(CustomDuplicateFieldException.class));
    }

    @Test
    void notFoundIdException() {
        apiExceptionHandler.notFoundIdException(Mockito.mock(CustomBadRequestException.class));
    }

    @Test
    void testNotFoundIdException() {
        apiExceptionHandler.notFoundIdException(Mockito.mock(CustomNotFoundException.class));
    }

    @Test
    void forBiddenException() {
        apiExceptionHandler.forBiddenException(Mockito.mock(CustomForbiddenException.class));
    }

    @Test
    void unAuthorizedException() {
        apiExceptionHandler.unAuthorizedException(Mockito.mock(CustomUnauthorizedException.class));
    }

    @Test
    void internalServer() {
        apiExceptionHandler.internalServer(Mockito.mock(CustomInternalServerException.class));
    }

    @Test
    void notExistException() {
        apiExceptionHandler.notExistException(Mockito.mock(Exception.class));
    }

    @Test
    void duplicateException() {
        apiExceptionHandler.duplicateException(Mockito.mock(Exception.class));
    }
}