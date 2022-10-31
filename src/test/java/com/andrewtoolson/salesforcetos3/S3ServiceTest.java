package com.andrewtoolson.salesforcetos3;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class S3ServiceTest {

    @Test
    @Disabled
    public void test() {
        S3Service s3 = new S3Service();
        s3.storeInS3("hello world", "test.json");
    }

}
