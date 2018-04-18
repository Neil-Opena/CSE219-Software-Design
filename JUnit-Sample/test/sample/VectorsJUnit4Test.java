/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sample;

import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author neil1
 */
public class VectorsJUnit4Test {
	
	public VectorsJUnit4Test() {
	}
	
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
	}

	/**
	 * Test of equal method, of class Vectors.
	 */
	@Test
	public void equalsCheck() {
		System.out.println("* VectorsJUnit4Test: equalsCheck()");
		assertTrue(Vectors.equal(new int[] {}, new int[] {}));
    		assertTrue(Vectors.equal(new int[] {0}, new int[] {0}));
    		assertTrue(Vectors.equal(new int[] {0, 0}, new int[] {0, 0}));
    		assertTrue(Vectors.equal(new int[] {0, 0, 0}, new int[] {0, 0, 0}));
    		assertTrue(Vectors.equal(new int[] {5, 6, 7}, new int[] {5, 6, 7}));

    		assertFalse(Vectors.equal(new int[] {}, new int[] {0}));
    		assertFalse(Vectors.equal(new int[] {0}, new int[] {0, 0}));
    		assertFalse(Vectors.equal(new int[] {0, 0}, new int[] {0, 0, 0}));
    		assertFalse(Vectors.equal(new int[] {0, 0, 0}, new int[] {0, 0}));
    		assertFalse(Vectors.equal(new int[] {0, 0}, new int[] {0}));
    		assertFalse(Vectors.equal(new int[] {0}, new int[] {}));

    		assertFalse(Vectors.equal(new int[] {0, 0, 0}, new int[] {0, 0, 1}));
    		assertFalse(Vectors.equal(new int[] {0, 0, 0}, new int[] {0, 1, 0}));
    		assertFalse(Vectors.equal(new int[] {0, 0, 0}, new int[] {1, 0, 0}));
    		assertFalse(Vectors.equal(new int[] {0, 0, 1}, new int[] {0, 0, 3}));
	}

	/**
	 * Test of scalarMultiplication method, of class Vectors.
	 */
	@Test
	public void ScalarMultiplicationCheck() {
		System.out.println("* VectorsJUnit4Test: ScalarMultiplicationCheck()");
		assertEquals(  0, Vectors.scalarMultiplication(new int[] { 0, 0}, new int[] { 0, 0}));
    		assertEquals( 39, Vectors.scalarMultiplication(new int[] { 3, 4}, new int[] { 5, 6}));
    		assertEquals(-39, Vectors.scalarMultiplication(new int[] {-3, 4}, new int[] { 5,-6}));
    		assertEquals(  0, Vectors.scalarMultiplication(new int[] { 5, 9}, new int[] {-9, 5}));
    		assertEquals(100, Vectors.scalarMultiplication(new int[] { 6, 8}, new int[] { 6, 8}));
	}
	
}
