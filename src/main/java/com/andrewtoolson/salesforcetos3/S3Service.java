package com.andrewtoolson.salesforcetos3;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * This class interfaces with the actual S3 service. Any calls to S3 should route through this class.
 */
public class S3Service {

    S3Client s3 = S3Client.builder().region(Region.US_WEST_1).build();

    public void storeInS3(String content, String filePath) {

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket("coinbase-salesforce-to-s3")
                .contentType("application/json")
                .key(filePath)
                .build();
        RequestBody body = RequestBody.fromString(content);
        s3.putObject(putRequest, body);
    }
}
