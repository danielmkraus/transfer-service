package org.danielmkraus.transfer.integration;


import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.jupiter.api.Tag;
import org.junit.runner.RunWith;

import static org.danielmkraus.transfer.TransferTests.INTEGRATION_TEST;

@Tag(INTEGRATION_TEST)
@RunWith(Cucumber.class)
@CucumberOptions(tags = "@transfer",
        plugin = {"pretty", "html:target/cucumber"},
        features = "classpath:/stories",
        strict = true
)
public class TransferServiceIntegrationTest {

}
