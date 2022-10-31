package com.andrewtoolson.salesforcetos3;

import com.andrewtoolson.model.HttpRequest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class CaseToS3ControllerTest {

    @Test
    @Disabled
    public void test() {
        CaseToS3Controller controller = new CaseToS3Controller();

        HttpRequest request = new HttpRequest();
        request.getQueryStringParameters().put("caseId", "123");

        controller.handleRequest(request, new TestContext());
    }

}