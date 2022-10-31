package com.andrewtoolson.salesforcetos3;

import com.andrewtoolson.model.SalesforceCaseDetails;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class SalesforceCaseDetailsServiceTest {

    @Test
    @Disabled
    public void getCaseDetails() throws IOException {
        SalesforceCaseDetailsService service = new SalesforceCaseDetailsService();
        SalesforceCaseDetails caseDetails = service.getCaseDetails("500Dn000002JPo2IAG", "00DDn000002EhVi!AQIAQG9zCi.azsHnmoQUNAw8qlRwGaVKPau.SyRGURSbMCGlVcS4fAn1F4PJnb7av0Z3GC5L0P.DV2pr.dotNjJu6S3BXw0C");
        System.out.println("details: " + caseDetails);
    }
}