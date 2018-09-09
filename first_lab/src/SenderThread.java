import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SenderThread extends Thread implements AppCommons {
    private byte [] buf=new byte[size_of_buf];
    public void run(){
        while (true){
            try{
                DatagramSocket send_socket=new DatagramSocket();
                String message=InetAddress.getLocalHost().getHostAddress();
                buf = message.getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName("230.0.0.0"), default_port);
                send_socket.send(packet);
                Thread.sleep(time_of_send);
            }
            catch (IOException | InterruptedException e){
                System.out.printf(""+e.getMessage());
            }
        }
    }
}
