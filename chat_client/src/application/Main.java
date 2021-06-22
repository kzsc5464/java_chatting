package application;
	
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Optional;
import java.util.StringTokenizer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;



public class Main extends Application {
	
	Socket socket;
	TextArea textArea;
	ListView<String> room_list;
//	클라이어트 프로그램 동작 메소드
	public void startClient(String IP, int port,String userName) { 
		Thread thread = new Thread() {
			public void run() {
				try {
					socket = new Socket(IP,port);
					receive();
				}catch(Exception e) {
					if(!socket.isClosed()) {
						stopClient();
						System.out.println("[서버 접속 실패]");
						Platform.exit();
					}
				}
			}
		};
		thread.start();
	}
	
	public void stopClient() {
		try {
//			socket 닫혀있지 않거나 남아있을경우 socket을 닫습니다.
			if(socket != null && !socket.isClosed()) {
				socket.close();
			}
		}catch(Exception e) {
//			오류소스코드를 출력합니다.
			e.printStackTrace();
		}
	}
	
	
	public void receive() {
		while(true) {
			try {
				InputStream in = socket.getInputStream();
				
				byte[] buffer = new byte[1024];
				int length = in.read(buffer);
				if(length == -1) throw new IOException();
				String message = new String(buffer, 0, length,"UTF-8");
				
				StringTokenizer str = new StringTokenizer(message,"/");
				String type = str.nextToken();
				String name = str.nextToken();
				String Message = str.nextToken();
				String room_name = str.nextToken();
				
				
				if(type.equals("message")) {
					Platform.runLater(()->{
						textArea.appendText( name + " : " + Message +"\n");
					});
				}else if(type.equals("room_name")||type.equals("room_first")) {
					Platform.runLater(() ->{
						room_list.getItems().add(Message);
					});
				}
			}catch(Exception e) {
				Platform.runLater(()->{
					textArea.appendText("fuck");
				});
				
				stopClient();
				break;
			}
		}
	}
	
	public void send(String message) {
	Thread thread = new Thread() {
			public void run() {
				try {
					OutputStream out = socket.getOutputStream();
					byte[] buffer = message.getBytes("UTF-8");
					out.write(buffer);
					out.flush();
				}catch(Exception e) {
					stopClient();
				}
			}
		};
		thread.start();
	}
	public void user_info_send(String name) {
		Thread thread = new Thread() {
			public void run() {
				try {
					OutputStream out = socket.getOutputStream();
					byte[] buffer = name.getBytes("UTF-8");
					out.write(buffer);
					out.flush();
				}catch(Exception e) {
					stopClient();
				}
			}
		};
		thread.start();
	}
	

	@Override
	public void start(Stage primaryStage) {
		
		BorderPane room = new BorderPane();
		room.setPrefSize(200,200);
		room.setPadding(new Insets(10));
		
		BorderPane root = new BorderPane();
		root.setPrefSize(600,200);
		root.setPadding(new Insets(10));
		
		HBox hbox = new HBox();
		hbox.setSpacing(10);
		
		HBox big_pane = new HBox();
		big_pane.getChildren().addAll(root,room);
		

//root_Top GUI(Left)
		TextField userName = new TextField();
		userName.setPrefWidth(200);
		userName.setPromptText("닉네임을 입력하세요.");
		HBox.setHgrow(userName, Priority.ALWAYS);
				
		TextField IPText = new TextField("localhost");
		TextField portText = new TextField("16000");
		portText.setPrefWidth(80);
				
		hbox.getChildren().addAll(userName,IPText,portText);		
		
		
//		room bottom
		Label choice_room = new Label();
		choice_room.setText("공용");
		
		
//		room_list
		room_list = new ListView();
		room_list.setPadding(new Insets(10));
		
		room_list.setOnMouseClicked(e -> {
			if(e.getClickCount()>1) {
				Platform.runLater(()->{
					textArea.clear();
					choice_room.setText(room_list.getSelectionModel().getSelectedItem());
					send("start_room"+"/"+userName.getText()+"/"+room_list.getSelectionModel().getSelectedItem()+"/"+"default");
				});		
			}
		});
		
//		room_채팅방만들기		
		Button room_make = new Button("채팅방만들기");
		room_make.setDisable(true);
		room_make.setOnAction(event -> {
			TextInputDialog room_text = new TextInputDialog();
			room_text.setTitle("채팅방 이름");
			room_text.setHeaderText(null);
			Optional<String>result = room_text.showAndWait();
			send("room_name"+"/"+userName.getText()+"/"+result.get()+"/"+"default");
			
		});

		
		
		HBox room_bottom = new HBox();
		room_bottom.setSpacing(10);
		room_bottom.getChildren().addAll(room_make,choice_room);

		
		

		
//		root 채팅이 출력되는 textArea입니다.
		textArea = new TextArea();
		textArea.setEditable(false);

		
//	 	채팅입력 TextField
		TextField input = new TextField();
		input.setPrefWidth(Double.MAX_VALUE);
		input.setDisable(true);
			
		input.setOnAction(event -> {
			
			if(!choice_room.getText().equals("공용")) {
				send("message"+"/"+userName.getText() + "/" + input.getText() + "/"+choice_room.getText());
			}else {
				send("message"+"/"+userName.getText() + "/" + input.getText() + "/"+"공용");
			}
			
			input.setText("");
			input.requestFocus();
			
		});
		
//	 	보내기Button기능		
		Button sendButton = new Button("보내기");
		sendButton.setDisable(true);
		sendButton.setOnAction(event -> {
			send("message"+"/"+userName.getText() + "/" + input.getText() + "/"+"공용");
			input.setText("");
			input.requestFocus();
		});
		
		
		Button connectionButton = new Button("접속하기");
		connectionButton.setOnAction(event ->{
			if(connectionButton.getText().equals("접속하기")) {
				int port = 0;
				try{
					port = Integer.parseInt(portText.getText());
				}catch(Exception e) {
					e.printStackTrace();
				}
				startClient(IPText.getText(), port,userName.getText());
				Platform.runLater(()->{
					textArea.appendText("[채팅방 접속] \n");
				});
				connectionButton.setText("종료하기");
				room_make.setDisable(false);
				input.setDisable(false);
				sendButton.setDisable(false);
				input.requestFocus();
			}else {
				stopClient();
				Platform.runLater(()->{
					textArea.appendText("[채팅방 퇴장]\n ");
				});
				connectionButton.setText("접속하기");
				input.setDisable(true);
				sendButton.setDisable(true);
			}
		});
		
//		root/Bottom
		BorderPane pane = new BorderPane();
		pane.setLeft(connectionButton);
		pane.setCenter(input);
		pane.setRight(sendButton);
		
		
//		root/main		
		root.setCenter(textArea);
		root.setTop(hbox);
		root.setBottom(pane);

//		room/main
		room.setBottom(room_bottom);
		room.setCenter(room_list);

		Scene scene = new Scene(big_pane, 800, 400);
		primaryStage.setTitle("[ 채팅 클라이언트 ] ");
		primaryStage.setScene(scene);
		primaryStage.setOnCloseRequest(event -> stopClient());
		primaryStage.show();
		
		connectionButton.requestFocus();
	}
	
	public static void main(String[] args) {
//		javaFX만의 표준실행방식입니다.
		launch(args);
	}
}
