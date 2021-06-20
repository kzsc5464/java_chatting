package application;
	
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;


public class Main extends Application {
	
	public static ExecutorService threadPool;
	public static Vector<client> clients = new Vector<client>();
	public static Vector<room_info>room_name = new Vector<>();
	public static Vector<Vector<String>>room = new Vector<>();
	ServerSocket serverSocket;
	
//	서버시작
	public void startServer(String IP, int port) {
		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress(IP,port));
		}catch(Exception e) {
			e.printStackTrace();
			if(!serverSocket.isClosed()) {
				stopServer();
			}
			return;
		}
//		클라이언트가 접속할때까지 계속 기다리는 쓰레드입니다.
		Runnable thread = new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						Socket socket = serverSocket.accept();
						clients.add(new client(socket));
						System.out.println("[클라이언트 접속]");
					}catch(Exception e) {
						if(!serverSocket.isClosed()) {
							e.printStackTrace();
						}
						break;
					}
				}
			}
		};
//		쓰레드를 안정적이게 관리하기위해서 쓰레드 풀에 넣어둔다
//		submit(thread) threadPool 작업 큐에 thread을 입력합니다.
		threadPool = Executors.newCachedThreadPool();
		threadPool.submit(thread);
	}
//	서버정지
	public void stopServer() {
		try {
			//현재 작동 중인 모든 소켓 닫기
			Iterator<client> iterator = clients.iterator();	
			while(iterator.hasNext()) {
				client client = iterator.next();
				client.socket.close();
				iterator.remove();
			}
			if(serverSocket != null && !serverSocket.isClosed()) {
				serverSocket.close();
			}
//			threadPool은 프로그램이 작동이종료되어도 종료되지 않기 떄문에 shutdown()으로 종료시켜줍니다.
			if(threadPool != null && !threadPool.isShutdown()) {
				threadPool.shutdown();
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void start(Stage primaryStage) {
		BorderPane root = new BorderPane();
		root.setPadding(new Insets(5));
		
		TextArea textArea =new TextArea();
		textArea.setEditable(false);
		root.setCenter(textArea);
		
		Button toggleButton = new Button("시작하기");
		toggleButton.setMaxWidth(Double.MAX_VALUE);
		BorderPane.setMargin(toggleButton,new Insets(1, 0, 0, 0));
		root.setBottom(toggleButton);
		
		String IP = "localhost";
		int port = 16000;
		
		toggleButton.setOnAction(event -> {
			if(toggleButton.getText().contentEquals("시작하기")) {
				startServer(IP,port);
				Platform.runLater(()->{
					String message = String.format("[서버시작]\n", IP,port);
					textArea.appendText(message);
					toggleButton.setText("종료하기");
				});
			}else {
				stopServer();
				Platform.runLater(() -> {
					String message = String.format("[서버종료]\n",IP,port);
					textArea.appendText(message);
					toggleButton.setText("시작하기");
				});
			}
		});
		
		Scene scene = new Scene(root,400,400);
		primaryStage.setTitle("[채팅 서버]" );
		primaryStage.setOnCloseRequest(event -> stopServer());
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
