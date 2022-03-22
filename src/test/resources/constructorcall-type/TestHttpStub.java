package com.test;

public class TestHttpStub {

  private static com.ibm.ws.webservices.engine.description.OperationDesc _getInformationOperation0 =
      null;

  private static void _getInformationOperation0() {
    com.ibm.ws.webservices.engine.description.FaultDesc[] _faults0 =
        new com.ibm.ws.webservices.engine.description[] {};
    _getInformationOperation0 =
        new com.ibm.ws.webservices.engine.description.OperationDesc("getVerificationInformation",
            new javax.xml.namespace.QName("", "getVerificationInformation"), _faults0,
            "http://com.gov//getVerificationInformation");//constructor call which needs to be transformed
  }
}
