package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;





public class client {
	
	Socket socket;
	String name;
	
	
	
	public client(Socket socket) {
		this.socket = socket;
		receive();
		start_send();
	}
	
	public void receive() {

		Runnable thread = new Runnable() {

			@Override
			public void run() {
				try {
					while(true) {
						InputStream in = socket.getInputStream();
						byte[] buffer = new byte[1024];
						int length = in.read(buffer);
						while(length == -1)throw new IOException();
						System.out.println("[메시지 수신 성공]");
					
						String message = new String(buffer, 0 , length, "UTF-8");
						
						StringTokenizer str = new StringTokenizer(message,"/");
						String type = str.nextToken();
						String name = str.nextToken();
						String Message = str.nextToken();
						String room_name = str.nextToken();
						
						System.out.println(message);
						System.out.println(type);
						System.out.println(name);
						System.out.println(Message);
						System.out.println(room_name);
						
						if(room_name.equals("공용")) {
							System.out.println("true");
						}else {
							System.out.println(room_name + " : " + "공용");
						}
						
						if(type.equals("room_name")) {
							Main.room_name.add(new room_info(Message));
							for(client client : Main.clients) {
								client.send(message);
							}
						}
						
						
						
						else if(type.equals("start_room")) {
							for(room_info room : Main.room_name) {
								if(room.setRoomName().equals(Message)) {
									room.setMember(client.this);
								}
							}
						}
						
						else {
							if(room_name.equals("공용")) {
								System.out.println("공용 : 메세지 전달");
								for(client client : Main.clients) {
									client.send(message);
								}
							}else {
								System.out.println("ㅅㅂ 설마");
								for(room_info room : Main.room_name) {
									if(room.name.equals(room_name)) {
										for(client client:room.client_room) {
											client.send(message);
										}
									}
								}
							}
							
						}
						
						
					}
				}catch(Exception e) {
					try {
						System.out.println("[메시지 수신 오류]");
					}catch(Exception e2) {
						e2.printStackTrace();
					}
				}
				
			}
			
		};

		Main.threadPool.submit(thread);
	}
	
	public void send(String message) {

		
		Runnable thread = new Runnable() {

			@Override
			public void run() {
				try {
					OutputStream out = socket.getOutputStream();
					byte[] buffer = message.getBytes("UTF-8");
					out.write(buffer);
					out.flush();
					
				}catch(Exception e) {
					try {
						System.out.println("[메시지 송신 오류]"
								+socket.getRemoteSocketAddress()
								+ " : " + Thread.currentThread().getName());
						Main.clients.remove(client.this);
						socket.close();
					}catch(Exception e2) {
						e.printStackTrace();
					}
				}
			}
			
		};
//		threadPool은 사용자의 증가로 thread가 증가할시에 CPU의 메모리사용량을 억제하기 위해
//		thread의 제한을 두는 것입니다.
		Main.threadPool.submit(thread);
	}
	
	public void start_send() {
		Runnable thread = new Runnable() {

			@Override
			public void run() {
				try {
					for(room_info room : Main.room_name) {
						OutputStream out = socket.getOutputStream();
						String message = "room_name"+"/"+"default"+"/"+room.setRoomName()+"/"+"default";
						byte[] buffer = message.getBytes("UTF-8");
						out.write(buffer);
						out.flush();
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
				
			}
			
			
		};
		Main.threadPool.submit(thread);
	}
	

	
}
