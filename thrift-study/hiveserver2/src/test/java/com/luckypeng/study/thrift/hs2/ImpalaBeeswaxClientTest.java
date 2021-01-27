package com.luckypeng.study.thrift.hs2;

import org.junit.Test;

import java.util.Scanner;

public class ImpalaBeeswaxClientTest {

    @Test
    public void testClient() {
        Scanner sc = new Scanner(System.in);
        ImpalaBeeswaxClient.testClient(sc);
        sc.close();
    }
}