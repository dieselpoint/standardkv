This is a in-memory implementation of the StandardKV API.

It should be used for testing only. It does not support transactions, and concurrent updates may show up in the cursor while you're iterating over it.
Other than that it should be thread-safe.

