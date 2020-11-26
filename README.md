The DCSA API Validator
======================

Given a target publisher of DCSA's OpenAPI, the validator tests that all end points are behaving as expected.

In its current early state of development, the target is defined in src/test/java/org/dcsa/api\_validator/conf/Configuration.java

To test an implementation running v2 of the API, run
 ```
export API_ROOT_URI=http://localhost:9090/v2
export CALLBACK_URI=http://localhost:4567
mvn -Dtest.suite=TnTV2.xml  test
```
