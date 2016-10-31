package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by raoman on 29/10/2016.
 */
public interface RemoteServices extends Remote {

    public void transmitionFile() throws RemoteException;
    public void openFile() throws RemoteException;
    public void nextPage() throws RemoteException;
    public void previusPage()throws RemoteException;

}
