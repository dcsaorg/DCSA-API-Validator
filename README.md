The DCSA API Validator
======================

The DCSA API Validator can be used to test a given implementation of
DCSA's OpenAPI against the reference test suite to ensure that all
endpoints are behaving as expected.


Testing an API
==============

To test an API, the implementor will need the following:

 * A running instance of the API that will be tested.
 * The API should be loaded with the reference test data.
 * The API validator should be configured and run.

**CAVEAT**: The API validator SHOULD only be used on test systems as it
will create and mutate data in the API under test. A concrete example
being that it will attempt to create a shipping instruction as a part
of the EBL test suite.

Data prerequisites
------------------

The API validator assumes that some particular data is present - e.g. such as
shipments or carrier booking references.   DCSA maintains that test data in
along with the [DCSA Information model] (see `references/testdata.d` directory)
since version 3 of the DCSA Information model.  For older releases of TNT (e.g. 1.2
and 2.0), the test data is directly in the source code for the TNT reference
implementation.

Note that the API validator will invoke mutating endpoints, and the data *should*
be reset between each test run for the most reliable results.

[DCSA Information model]: https://github.com/dcsaorg/DCSA-Information-Model

Executing the API validator
---------------------------

To test an implementation running v2 of the API, run
```shell
# This is where the API validator can find the implementation of the API
export API_ROOT_URI=http://localhost:9090/v2

# Which point the API validator should listen on for callbacks
# (for APIs where this is relevant)
export CALLBACK_LISTEN_PORT=4567

# For APIs with callbacks, this is the base URL the API validator will use
# to reference itself.  Most people will want the port in this
# URI to be aligned the port with CALLBACK_LISTEN_PORT.
export CALLBACK_URI=http://localhost:4567

# Run the relevant test suite (here TnT version 2)
mvn -Dtest.suite=TnTV2.xml test
```
