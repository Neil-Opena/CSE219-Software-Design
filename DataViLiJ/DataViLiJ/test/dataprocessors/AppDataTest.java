/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataprocessors;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author neil1
 */
public class AppDataTest {
	
	public AppDataTest() {
	}
	
	@BeforeClass
	public static void setUpClass() {
	}
	
	@AfterClass
	public static void tearDownClass() {
	}

	/**
	 * The requirement is to test whether the data entered in the text area is being saved to the destination .tsd file. 
	 * That is, you can store the data internally in a string, and test saving that string to the destination file.
	 */
	@Test
	public void testSaveData() {
		System.out.println("saveData");

	}
	
}
