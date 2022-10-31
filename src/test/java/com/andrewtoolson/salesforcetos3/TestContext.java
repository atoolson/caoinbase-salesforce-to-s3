package com.andrewtoolson.salesforcetos3;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import lombok.experimental.Delegate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TestContext implements Context {

    @Delegate
    private final Context context = mock(Context.class);
    LambdaLogger logger = mock(LambdaLogger.class);

    public TestContext() {
        doAnswer(invocation -> {
            System.out.println(invocation.getArguments()[0]);
            return null;
        }).when(logger).log(anyString());

        when(context.getLogger()).thenReturn(logger);
    }

}
