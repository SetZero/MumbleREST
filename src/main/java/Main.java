import mumble.tcp.TCPCommunicator;
import rest.http.HTTPServer;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

public class Main {
    public static void main(String[] args) throws CertificateException, SSLException, InterruptedException {
        System.out.println("Hello World");
        //HTTPServer server = new HTTPServer(8080, true);
        //server.start();
        TCPCommunicator communicator = new TCPCommunicator("nooblounge.net", 64738);
        Thread t = new Thread(communicator);
        t.start();
        t.join();
    }
}
