package Persistance;

public class IPAddress {
	private byte[] address = {0,0,0,0};
	private Integer port = 0;

	public IPAddress(byte[] Adress, Integer port){
		this.address = Adress;
		this.port = port;
	}

	public void setAddress(byte[] Adress){
		this.address = Adress;
	}
	public byte[] getAddress(){
		return address;
	}
	public void setPort(Integer port){
		this.port = port;
	}
	public Integer getPort(){
		return port;
	}
	public String toIPAddress(){
		StringBuilder addressString = new StringBuilder();
		for(byte i: address){
			addressString.append(Integer.toString(Byte.toUnsignedInt(i)));
			addressString.append('.');
		}
		addressString.deleteCharAt(addressString.length()-1);
		addressString.append(':');
		addressString.append(port);

		return addressString.toString();
	}
}
