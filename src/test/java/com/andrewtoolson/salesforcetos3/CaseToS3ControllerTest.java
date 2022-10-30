package com.andrewtoolson.salesforcetos3;

import com.andrewtoolson.model.HttpRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CaseToS3ControllerTest {

    @Test
    public void test() {
        CaseToS3Controller controller = new CaseToS3Controller();

        HttpRequest request = new HttpRequest();

        controller.handleRequest(request, null);
    }

}