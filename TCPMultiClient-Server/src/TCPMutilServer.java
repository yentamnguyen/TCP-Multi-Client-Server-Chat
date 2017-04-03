import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class TCPMutilServer {
	public static void main(String[] args) throws Exception {
		ServerSocket sv = new ServerSocket(6789);

		Responder r = new Responder();
		System.out.println("Starting server...");
		// server runs for infinite time and wait for client to connect
		while (true) {
			// waiting
			Socket socket = sv.accept();
			System.out.println("Client connected");
			new Thread(new ServerRun(r, socket)).start();
		}
	}
}

class ServerRun implements Runnable {
	Responder r;
	Socket socket;

	public ServerRun(Responder r, Socket s) {
		this.r = r;
		this.socket = s;
	}

	@Override
	public void run() {
		while (r.responderMethod(socket)) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException ie) {
				ie.getMessage();
			}
		}
		try {
			socket.close();
			System.out.println("Client disconnected");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class Responder {
	String Message = "";

	synchronized public boolean responderMethod(Socket socket) {
		try {
			DataInputStream inFromClient = new DataInputStream(socket.getInputStream());
			DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
			outToClient.writeUTF(Message);
			String clientSentence = inFromClient.readUTF();
			if (clientSentence.startsWith("0")) {
				return false;
			}
			clientSentence = clientSentence.substring(1);
			System.out.println(clientSentence);
			Message = clientSentence + Message;
			outToClient.writeUTF(Message);
			return true;
		} catch (EOFException eof) {
			return false;
		} catch (SocketException se) {
			// se.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
