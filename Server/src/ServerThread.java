import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerThread extends Thread
{
	private ArrayList<ConnectionThread> mConnections = new ArrayList<ConnectionThread>();

	public void run()
	{
		ServerSocket serverSocket = null;

		int port = 5001;
		try
		{
			serverSocket = new ServerSocket(port);
		}
		catch (IOException e)
		{
			System.out.println("Failed to bind port " + port);
			e.printStackTrace();
		}

		while (true)
		{
			try
			{
				Socket sock = serverSocket.accept();
				synchronized (mConnections)
				{
					ConnectionThread newConnection = new ConnectionThread(sock, mConnections.size(), this);
					newConnection.start();
					mConnections.add(newConnection);
					sendUsers();
				}
			}
			catch (Exception er)
			{
				System.out.println("Failed to accept connection! ");
				return;
			}
			System.out.println("Someone connected!");
		}

	}

	public void stopRunning()
	{
		try
		{
			for (int i = 0; i < mConnections.size(); i++)
			{
				mConnections.get(i).closeConnection();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void writeToAllConnections(String data)
	{
		synchronized (mConnections)
		{
			try
			{
				for (int i = 0; i < mConnections.size(); i++)
				{
					mConnections.get(i).write(data);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public void writeToConnection(int index , String data)
	{
		synchronized (mConnections)
		{
			try
			{
				mConnections.get(index).write(data);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public void sendUsers()
	{
		String message = "USERS ";
		for (int i = 0; i < mConnections.size() - 1; i++)
		{
			message += mConnections.get(i).getUserName() + ";";
		}
		if (mConnections.size() >= 1)
		{
			message += mConnections.get(mConnections.size() - 1).getUserName();
		}
		System.out.println(message);
		writeToAllConnections(message);
	}

	public void removeConnectionFromList(int index)
	{
		synchronized (mConnections)
		{
			ConnectionThread aux = mConnections.get(index);
			aux.closeConnection();
			mConnections.set(index, mConnections.get(mConnections.size() - 1));
			mConnections.get(index).setConnectionNumber(aux.getConnectionNumber());
			mConnections.remove(mConnections.size() - 1);
			System.out.println("Number of connections are : " + mConnections.size());
			sendUsers();
		}
	}
}
