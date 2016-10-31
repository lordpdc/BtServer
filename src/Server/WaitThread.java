package Server;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import javax.obex.HeaderSet;
import javax.obex.Operation;
import javax.obex.ResponseCodes;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.logging.Logger;

public class WaitThread implements Runnable{

	private InputStream in;
	private OutputStream out;
	
	/** Constructor */
	public WaitThread() {
	}

	@Override
	public void run() {
		waitForConnection();		
	}

	/** Waiting for connection from devices */
	private void waitForConnection() {
		// retrieve the local Bluetooth device object
		LocalDevice local = null;

		StreamConnectionNotifier notifier;
		StreamConnection connection = null;

		// setup the server to listen for connection
		try {
			local = LocalDevice.getLocalDevice();
			local.setDiscoverable(DiscoveryAgent.GIAC);

			UUID uuid = new UUID("04c6093b00001000800000805f9b34fb", false);
			System.out.println(uuid.toString());

            String url = "btspp://localhost:" + uuid.toString() + ";name=RemoteBluetooth";
            notifier = (StreamConnectionNotifier)Connector.open(url);
        } catch (BluetoothStateException e) {
        	System.out.println("Bluetooth is not turned on.");
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		int cmd;
		// waiting for connection
		while(true){
			try {
				
				System.out.println("waiting for connection...");
	            connection = notifier.acceptAndOpen();
	            System.out.println("Connected to: "+ connection);
	            in = connection.openDataInputStream();
	            out = connection.openDataOutputStream();
	            
	            while(true)
	            {
	            	cmd = in.read();
	            	if(cmd == -1) break;
	            	procesaCmd(cmd);
	            }
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}
	private void procesaCmd(int cmd){
		System.out.println("Comando "+cmd);
		Robot robot = null;
		try {
			robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		switch (cmd){
			case 1:
				robot.keyPress(KeyEvent.VK_PAGE_UP);
				break;
			case 2:
				robot.keyPress(KeyEvent.VK_PAGE_DOWN);
				break;
			case 3:
				//receivingFile();
                onPut();
				break;
			default:
				System.out.println("comando no reconocido");
				break;
		}


		}
	public void receivingFile(){
		String file="/copy.pdf";
		try{
			FileOutputStream fos = new FileOutputStream(file);

			byte[] buffer = new byte[8192];
			int bytesRead = in.read(buffer, 0, buffer.length);
			int		current = bytesRead;

			do {
				bytesRead = in.read(buffer, current,buffer.length - current);
				if (bytesRead >= 0)
					current += bytesRead;
			} while (bytesRead > -1);
			fos.write(buffer);
			fos.flush();
			fos.close();

			File path = new File (file);
			Desktop.getDesktop().open(path);

		} catch (IOException e) {
			e.printStackTrace();
		}catch(IllegalArgumentException e){
		JOptionPane.showMessageDialog(null, "No se pudo encontrar el archivo","Error",JOptionPane.ERROR_MESSAGE);
		e.printStackTrace();
	}


}
    public int onPut() {
         			try {
            				//HeaderSet hs = op.getReceivedHeaders();
                        String name = "test.pdf";//(String) hs.getHeader(HeaderSet.NAME);
           				if (name != null) {
                            System.out.println("Receiving " + name);
            				} else {
            					name = "test.pdf";
                            System.out.println("Receiving file");
            				}
            				/*Long len = (Long) hs.getHeader(HeaderSet.LENGTH);
           				if (len != null) {
                            System.out.println("file lenght:" + len);
           			    }*/
           				File f = new File(homePath(), name);
           				FileOutputStream out = new FileOutputStream(f);
           				InputStream is = in;//op.openInputStream();
                       				int received = 0;

            				while (true) {
                 					int data = is.read();
                 					if (data == -1) {
                                        System.out.println("EOS received");
                   						break;
         					}
         					out.write(data);
        					received++;
        					/*if ((len != null) && (received % 100 == 0)) {
                                System.out.println(received+" %");
        					}*/
        				}
         				//op.close();
         				out.close();
                        System.out.println("file saved:" + f.getAbsolutePath());
                        System.out.println("Received " + name);
                        File path = new File (f.getAbsolutePath());
                        Desktop.getDesktop().open(path);
         				return ResponseCodes.OBEX_HTTP_OK;
         			} catch (IOException e) {
                        System.out.println("OBEX Server onPut error");
         				return ResponseCodes.OBEX_HTTP_UNAVAILABLE;
         			} finally {
                        System.out.println("OBEX onPut ends");

 		}
 		}

    private static File homePath() {
		 		String path = "bluetooth";
		 		boolean isWindows = false;
		 		String sysName = System.getProperty("os.name");
				if (sysName != null) {
		 			sysName = sysName.toLowerCase();
		 			if (sysName.indexOf("windows") != -1) {
		 				isWindows = true;
		 				path = "My Documents";
		 			}
		 		}
		 		File dir;
		 		try {
		 			dir = new File(System.getProperty("user.home"), path);
		 			if (!dir.exists()) {
		 				if (!dir.mkdirs()) {
		 					throw new SecurityException();
		 				}
		 			}
		 		} catch (SecurityException e) {
		 			dir = new File(new File(System.getProperty("java.io.tmpdir"), System.getProperty("user.name")), path);
		 		}
		 		if (isWindows) {
		 			dir = new File(dir, "Bluetooth Exchange Folder");
		 		}
		 		if (!dir.exists()) {
		 			if (!dir.mkdirs()) {
		 				return null;
		 			}
		 		} else if (!dir.isDirectory()) {
		 			dir.delete();
		 			if (!dir.mkdirs()) {
		 				return null;
		 			}
		 		}
		 		return dir;
		}

	}
	
	

