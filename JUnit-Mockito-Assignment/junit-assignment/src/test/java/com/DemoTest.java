package com;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;


class ITest {

    @Test
    void testInterfaceMethod() {

        I mockObj = mock(I.class);  // mocking interface

        mockObj.abc();  // calling method

        verify(mockObj).abc();  // verifying method call
    }

    @Test
    void testVoidMethodCallCount() {

        I mockObj = mock(I.class);

        doNothing().when(mockObj).abc();

        mockObj.abc();
        mockObj.abc();

        verify(mockObj, times(2)).abc();
    }
}