package Persistance;

public class IPAddress {
	private byte[] Adress;
	private Integer port;

	public IPAddress(byte[] Adress, Integer port){
		this.Adress = Adress;
		this.port = port;
	}

	public void setAdress(byte[] Adress){
		this.Adress = Adress;
	}
	public byte[] getAdress(){
		return Adress;
	}
	public void setPort(Integer port){
		this.port = port;
	}
	public Integer getPort(){
		return port;
	}
}
