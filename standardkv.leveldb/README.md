An implementation of the StandardKV API for LevelDB which uses the Fusesource JNI interface. See https://github.com/fusesource/leveldbjni

The classname to use is "com.dieselpoint.standardkv.impl.leveldb.LevelDbStore"

```Java
Store store = StoreFactory.getStore("/path/to/database", "com.dieselpoint.standardkv.impl.leveldb.LevelDbStore");
```
