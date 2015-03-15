package client.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import client.GUI.Front;

public class Client extends Thread
{
	private String mAdress;
	private int mPort;
	private Socket mSocket = null;
	private DataInputStream mDataInputStream = null;
	private DataOutputStream mDataOutputStream = null;
	private Front mFrontUI = null;

	public Client(String ip_adress, int port, Front ui)
	{
		mAdress = ip_adress;
		mPort = port;
		mFrontUI = ui;
	}

	public void run()
	{
		int i = 0;
		mFrontUI.appendToChatText("Connecting to server...\n");

		for (i = 0; i < 10; i++)
		{
			try
			{
				mSocket = new Socket(mAdress, mPort);
				mDataInputStream = new DataInputStream(mSocket.getInputStream());
				mDataOutputStream = new DataOutputStream(mSocket.getOutputStream());
				break;
			}
			catch (Exception er)
			{
				mFrontUI.appendToChatText("Connection failed.. retrying\n");
			}
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		if (i == 10)
		{
			mFrontUI.appendToChatText("Server offline or no internet connection!\n");
			return;
		}

		mFrontUI.appendToChatText("Connected to " + mAdress + " on port : " + mPort + "\n");

		while (!mSocket.isClosed() && mSocket.isConnected())
		{
			try
			{
				parseMessage(mDataInputStream.readUTF());
				Thread.sleep(10);
			}
			catch (Exception er)
			{
				er.printStackTrace();
			}
		}

	}

	private void parseMessage(final String message)
	{
		String[] tokens = message.split(" ");
		String str = "";
		for (int i = 1; i < tokens.length; i++)
			str += tokens[i] + " ";
		final String string = str;

		if (tokens[0].equals("ERROR"))
		{
			JOptionPane.showMessageDialog(mFrontUI.getFrame(), string, "Error", JOptionPane.ERROR_MESSAGE);
		}
		else if (tokens[0].equals("MESSAGE"))
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					JTextArea txt = mFrontUI.getChatTextArea();
				    txt.append(string + "\n");
				}
			});
			
		}
		else if (tokens[0].equals("USERS"))
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					String users_message = message.substring(message.indexOf(' ') + 1);
					mFrontUI.clearUsers();
					String[] users = users_message.split(";");
					for (int i = 0; i < users.length; i++)
						mFrontUI.addUser(users[i]);
				}
			});
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
}
