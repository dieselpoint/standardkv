package com.dieselpoint.standardkv;

import java.io.IOException;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;



public class TestByteArray {

	@Test
	public void test() throws IOException {
		
		Random rand = new Random();
		ByteArray arr = new ByteArray();

		int i0 = rand.nextInt();
		arr.appendInt(i0);
		int i1 = arr.getInt();
		Assert.assertEquals(i0, i1); 
		
		
		long l0 = rand.nextLong();
		arr.appendLong(l0);
		long l1 = arr.getLong();
		Assert.assertEquals(l0, l1);


		String str = "sk4n6&n5n6l"; // should add some unicode chars
		arr.clear();
		arr.append(str); 
		String str2 = arr.getString();
		Assert.assertEquals(str, str2);
		
		arr.clear();
		arr.appendvInt(10);
		arr.appendvInt(83);
		arr.appendvInt(96774355);
		Assert.assertEquals(10, arr.getvInt());
		Assert.assertEquals(83, arr.getvInt());
		Assert.assertEquals(96774355, arr.getvInt());
		
		
	}

}
