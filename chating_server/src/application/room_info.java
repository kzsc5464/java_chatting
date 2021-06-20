package application;

import java.util.Vector;

public class room_info {

	public Vector<String>message_room = new Vector<String>();
	public Vector<client>client_room = new Vector<client>();
	String name;
	
	public room_info(String name) {
		this.name = name;
	}
	
	public String setRoomName() {
		return name;
	}
	
	public void setMember(client name) {
		client_room.add(name);
	}
	
	public void setMessage(String message) {
		message_room.add(message);
	}
	
}
