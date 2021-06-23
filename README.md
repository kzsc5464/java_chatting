# java_chatting_program
TCP/IP, JAVAFX(GUI), Thread, Socket  




## TODO 

* make_chatting_room in client_part
* make_add_delete_friend in client_part
* make_image_profile in client_part
* ignore member in Sever_part
* Desgin in all(client and Server)


## Install


### First java + eclipse install JavaFX

### Second JavaFX SDK(url)
https://github.com/kzsc5464/java_chatting

### Third javaFX SDK path setting

![javaFX_SDk](https://user-images.githubusercontent.com/60764506/123064255-12eaa200-d449-11eb-899a-63eddc2234d4.PNG)



## What's this?

* basic Socket chatting program
```
Socket socket;
```
* protocol Server program
```
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
```



## Update

* 2021-06-21 loading message When entering a chatting room / +add room_info setMessage / readMessage
* 2021-06-22 update protocol message -> message type Message
* 2021-06-23 Independent chat for each room.





IF you want talking me anything mail pleases
kzsc5464@naver.com 





