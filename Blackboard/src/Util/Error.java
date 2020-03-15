package Util;

public class Error {

	protected int deep = 6;

	public Error(String msg){

		new Log("### ERROR ### " + " [MSG] : " + msg, true,2,1+deep);

	}


	public Error(String msg, int inDeep){

		new Log("### ERROR ### " + " [MSG] : " + msg, true,2, 1+inDeep );

	}


	public Error(Throwable e){

		new Log("### ERROR ### " + " [MSG] : " + e.toString() , true,2, 1+deep);

	}


	public Error(Throwable e, int inDeep){

		new Log("### ERROR ### " + " [MSG] : " + e.toString(), true, 2, 1+inDeep);

	}

}
