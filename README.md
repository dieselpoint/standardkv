# StandardKV


Every KV store does things a little differently, which makes it difficult to swap out stores in your app. StandardKV provides a 
standardized interface over popular stores which makes it possible.

It doesn't support every little dial and knob in each store, but it's actually not bad. The implementations
are designed to be really simple, with very few lines of code, so that it should be easy to tweak the store-specific
configurations.

To get started, include an implementation in your project:

```XML
<dependency>
    <groupId>com.dieselpoint</groupId>
    <artifactId>standardkv.rocksdb</artifactId>
    <version>${version}</version>
</dependency>
```

And use it like this:

```Java

StoreFactory.getStore("/temp/rocksdb", StoreFactory.ROCKSDB);
		
Bucket bucket = store.createBucket("mybucket");
KVTable table = bucket.createTable("footable");
		
table.put(new ByteArray("aa"), new ByteArray("a_value"));
table.put(new ByteArray("foo"), new ByteArray("bar"));
table.put(new ByteArray("foo"), new ByteArray("bar1"));
table.put(new ByteArray("foo2"), new ByteArray("bar2"));

KVCursor curs = table.newCursor();
		
curs.seek(new ByteArray("fo"));
		
while (true) {
	System.out.println("key:" + curs.getKey().readString() + " value:" + curs.getValue().readString());
	if (!curs.next()) {
		break;
	}
}
		
KVCursor c = table.newCursor();
while (c.next()) {
	System.out.println("key:" + c.getKey().readString());
}
		
store.deleteBucket("mybucket");
```


# TO DO

Add implementations for:

berkeleydb

mapdb

lmdb 

wiredtiger

sqlite?

derby?

Consider adding some benchmarking code.

