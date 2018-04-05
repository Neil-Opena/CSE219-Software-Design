/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package communication;

/**
 *
 * @author neil1
 */
public abstract class OperatedData {
	private int x;
	private int y;
	private String label;

	public OperatedData(int x, int y, String label){
		this.x = x;
		this.y = y;
		this.label = label;
	}
}
