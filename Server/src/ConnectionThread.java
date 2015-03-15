import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConnectionThread extends Thread
{
	private Socket mSocket = null;
	private DataInputStream mDataInputStream = null;
	private DataOutputStream mDataOutputStream = null;
	private ServerThread mServerFather = null;
	private String mUserName = null;
	private int mConnectionNumber;

	public ConnectionThread(Socket socket, int connectionNumber, ServerThread srv)
	{
		mSocket = socket;
		try
		{
			mDataInputStream = new DataInputStream(mSocket.getInputStream());
			mDataOutputStream = new DataOutputStream(mSocket.getOutputStream());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		mServerFather = srv;

		setConnectionNumber(connectionNumber);

		for (int i = 0; i < 1000; i++)
		{
			mUserName = "User" + i;
			if (!GlobalHash.contains(mUserName)) break;
		}
		GlobalHash.insert(mUserName);
	}

	public int getConnectionNumber()
	{
		return mConnectionNumber;
	}

	public void setConnectionNumber(int ConnectionNumber)
	{
		this.mConnectionNumber = ConnectionNumber;
	}

	public String getUserName()
	{
		return mUserName;
	}

	public void run()
	{
		while (!mSocket.isClosed() && mSocket.isConnected())
		{
			try
			{
				String message = mDataInputStream.readUTF();
				System.out.println(message);
				parseMessage(message);
			}
			catch (IOException e)
			{
				break;
			}
		}
		synchronized (mServerFather)
		{
			GlobalHash.remove(mUserName);
			mServerFather.removeConnectionFromList(mConnectionNumber);

		}
	}

	private void parseMessage(String message)
	{
		synchronized (mServerFather)
		{
			String[] tokens = message.split(" ");
			if (tokens[0].equals("BROADCAST"))
			{
				mServerFather.writeToAllConnections("MESSAGE " + mUserName + " : " + message.substring("BROADCAST".length() + 1, message.length()));
			}
			else if (tokens[0].equals("WHISPER"))
			{
				int userIndex = Integer.parseInt(tokens[1]);
				String str = "";
				for (int i = 2; i < tokens.length; i++)
					str += tokens[i] + " ";

				mServerFather.writeToConnection(userIndex, "MESSAGE whisper from " + mUserName + " : " + str);
			}
			else if (tokens[0].equals("NAME"))
			{
				String newUsername = message.substring("NAME".length() + 1, message.length());
				if (GlobalHash.contains(newUsername))
				{
					write("ERROR Username already exists!");
				}
				else
				{
					GlobalHash.remove(mUserName);
					mUserName = newUsername;
					GlobalHash.insert(mUserName);
					mServerFather.sendUsers();
				}
			}
		}
	}

	public void write(String data)
	{
		try
		{
			mDataOutputStream.writeUTF(data);
			mDataOutputStream.flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void closeConnection()
	{
		try
		{
			mDataInputStream.close();
			mDataOutputStream.close();
			mSocket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
