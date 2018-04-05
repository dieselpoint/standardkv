package com.dieselpoint.buffers;

import java.io.IOException;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import com.dieselpoint.buffers.ByteArray;



public class TestByteArray {

	@Test
	public void test() throws IOException {
		
		Random rand = new Random();
		ByteArray arr = new ByteArray();

		int i0 = rand.nextInt();
		arr.appendInt(i0);
		int i1 = arr.readInt();
		Assert.assertEquals(i0, i1); 
		
		
		long l0 = rand.nextLong();
		arr.appendLong(l0);
		long l1 = arr.readLong();
		Assert.assertEquals(l0, l1);


		String str = "sk4n6&n5n6l\u23F0\u1f47e"; 
		arr.clear();
		arr.appendString(str); 
		String str2 = arr.readString();
		Assert.assertEquals(str, str2);
		
		arr.clear();
		arr.appendvInt(10);
		arr.appendvInt(83);
		arr.appendvInt(127);
		arr.appendvInt(128);
		arr.appendvInt(96774355);
		Assert.assertEquals(10, arr.readvInt());
		Assert.assertEquals(83, arr.readvInt());
		Assert.assertEquals(127, arr.readvInt());
		Assert.assertEquals(128, arr.readvInt());
		Assert.assertEquals(96774355, arr.readvInt());

		arr.clear();
		arr.appendvLong(10);
		arr.appendvLong(83);
		arr.appendvLong(127);
		arr.appendvLong(128);
		arr.appendvLong(96774355);
		Assert.assertEquals(10, arr.readvLong());
		Assert.assertEquals(83, arr.readvLong());
		Assert.assertEquals(127, arr.readvLong());
		Assert.assertEquals(128, arr.readvLong());
		Assert.assertEquals(96774355, arr.readvLong());

		
		
		
	}

}
