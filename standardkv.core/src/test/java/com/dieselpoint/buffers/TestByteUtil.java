package com.dieselpoint.buffers;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

public class TestByteUtil {

	@Test
	public void test() throws IOException {
		
		Random rand = new Random(4565);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		List<Long> list = new ArrayList<>();
		
		for (int i = 0; i < 100; i++) {
			
			long lng = rand.nextLong();
			ByteUtil.writevLong(lng, out);
			list.add(new Long(lng));
	
			lng = rand.nextInt(127);
			ByteUtil.writevLong(lng, out);
			list.add(new Long(lng));

			
			lng = rand.nextInt(16000);
			ByteUtil.writevLong(lng, out);
			list.add(new Long(lng));
			
			lng = rand.nextInt(2000000);
			ByteUtil.writevLong(lng, out);
			list.add(new Long(lng));
			
			lng = rand.nextInt();
			ByteUtil.writevLong(lng, out);
			list.add(new Long(lng));
		}

		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		
		for (Long lng: list) {
			long written = ByteUtil.readvLong(in);
			if (written != lng.longValue()) {
				fail(written + "!=" + lng.longValue());
			}
		}
	}

	/*
	@Test
	public void test2() throws IOException {
		ByteArray arr = new ByteArray();
		arr.appendvLong(130);
		System.out.println(arr.readvLong());
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteUtil.writevLong(130, out);
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		System.out.println(ByteUtil.readvLong(in));
		
	}
	*/
	
	
	
	
}
