package Util;

public class Warning {

	protected int deep = 4;

	public Warning(String msg){

		new Log("### WARNING ### " + " [MSG] : " + msg, true, 2,1+deep);

	}


	public Warning(String msg, int inDeep){

		new Log("### WARNING ### " + " [MSG] : " + msg, true, 2,1+inDeep );

	}

}