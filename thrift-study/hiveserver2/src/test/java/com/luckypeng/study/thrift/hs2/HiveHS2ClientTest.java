package com.luckypeng.study.thrift.hs2;

import org.junit.Test;

import static org.junit.Assert.*;

public class HiveHS2ClientTest {
    @Test
    public void testClient() {
        HiveHS2Client hiveHS2ClientTest =
                new HiveHS2Client("kuber02", 10000, 6000,"thrift-test", "123");
        hiveHS2ClientTest.testClient();
    }
}