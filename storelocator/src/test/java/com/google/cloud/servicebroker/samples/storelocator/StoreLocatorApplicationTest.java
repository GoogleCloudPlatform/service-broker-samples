package com.google.cloud.servicebroker.samples.storelocator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StoreLocatorApplicationTest {

  @MockBean
  DatabaseInitializer databaseInitializer;

  @Test
  public void contextLoads() {
  }

}
