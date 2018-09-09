import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App extends Thread implements AppCommons {
    private SenderThread senderThread;
    private String message;
    private String localAddress;
    private DatagramPacket packet;
    private MulticastSocket rec_socket = null;
    private byte[] buf = new byte[size_of_buf];
    private InetAddress group;
    private TreeSet<String> ip_table=new TreeSet<>();

    private void sendMessage() throws IOException{
        message=localAddress;
        buf = message.getBytes();
        packet = new DatagramPacket(buf, buf.length, group, default_port);
        rec_socket.send(packet);
    }

    public void run() {
        try {
            localAddress=InetAddress.getLocalHost().getHostAddress();
            senderThread=new SenderThread();
            rec_socket = new MulticastSocket(default_port);
            group = InetAddress.getByName("230.0.0.0");
            rec_socket.joinGroup(group);

            sendMessage();
            senderThread.start();
            Timer myTimer=new Timer();
            myTimer.schedule(new MyTimerTask(ip_table),1000,time_of_update_ip);
            DatagramPacket rec_packet = new DatagramPacket(buf, buf.length);

            while (true) {
                rec_socket.receive(rec_packet);
                String received = new String(rec_packet.getData(), 0, rec_packet.getLength());
                if(!received.equals(localAddress) && isIpAddress(received)) {
                    if(!ip_table.contains(received)){
                        ip_table.add(received);
                    }
                }
                if ("end".equals(received)) {
                    break;
                }
            }
            rec_socket.leaveGroup(group);
            rec_socket.close();
        }
        catch (IOException ex){
            System.out.printf(""+ex.getMessage());
        }
    }

    private boolean isIpAddress(String ip){
        Pattern p1=Pattern.compile("^([01]?\\\\d\\\\d?|2[0-4]\\\\d|25[0-5])\\\\.([01]?\\\\d\\\\d?|2[0-4]\\\\d|25[0-5])\\\\.\n" +
                        "([01]?\\\\d\\\\d?|2[0-4]\\\\d|25[0-5])\\\\.([01]?\\\\d\\\\d?|2[0-4]\\\\d|25[0-5])$");
        Matcher m1=p1.matcher(ip);
        return m1.matches();
    }

    public static void main(String[] args) {
        App app=new App();
        app.start();
    }
}

class MyTimerTask extends TimerTask{
    private TreeSet<String> old_ip_table=new TreeSet<>();
    private TreeSet<String> ip_table;
    public MyTimerTask(TreeSet<String> ip_table){
        this.ip_table=ip_table;
    }
    @Override
    public void run() {
        if(!old_ip_table.equals(ip_table)) {
            old_ip_table = (TreeSet) ip_table.clone();
            System.out.printf("Number of copies is "+old_ip_table.size()+"\n");
            for (String elements:old_ip_table) {
                System.out.printf(elements+"\n");
            }
            System.out.printf("***************************\n");
        }
        else
            System.out.printf("NOTHING CHANGE\n");
        ip_table.clear();
    }
}
//TODO equals for TreeSet
