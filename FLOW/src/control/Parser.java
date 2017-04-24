package control;

import java.io.File;

import model.Network;

public class Parser {

	@SuppressWarnings("unused")
	private File file;
	private Network network;
	
	public Parser(File file){
		this.file = file;
	}
	
	public void parse() {
		//Zeugs und so
	}
	
	public Network getData() {
		return this.network;
	}
}
