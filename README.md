#StandardKV

StandardKV provides a standardized interface over a key/value store. It also contains several implementations over common kv stores.

Every KV store does things a little differently, which makes it difficult to swap out stores in your app. StandardKV
is designed to be a standard interface over several popular stores. It's written to be as lightweight as possible.

It doesn't support every little dial and knob in each store, but it's actually not bad. And the implementations
are designed to be really simple, with very few lines of code, so that it should be easy to tweak the store-specific
configurations.

To get started, include the main kvstore dependency in your project:

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
snappydb for android


add benchmarks
add concurrency testing. test for crash and consistency separately.


