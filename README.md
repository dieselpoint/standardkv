#StandardKV

StandardKV provides a standardized interface over a key/value store. It also contains several implementations

Every KV store does things a little differently; some ...

This project wraps several KV stores in an interface which is simple and general-purpose.

The kvstore module contains the interfaces. It's very lightweight. The other modules contain specific implementations.

To get started, include the kvstore dependency in your project:

<> (is this necessary?)

and at least one implementation dependency:

<>

In your code, create a store like this:


```Java
Store store = StoreFactory.getStore(stuff here);
```

Usage goes here.


to add:
berkeleydb
mapdb
wiredtiger
sqlite?
derby?

add benchmarks
add concurrency testing. test for crash and consistency separately.


