package client.GUI;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import client.core.Client;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;

public class Front
{

	private JFrame mFrame;
	private JPanel mOutputPanel;
	private JPanel mStatsPanel;
	private JPanel mInputPanel;
	private JButton mSendButton;
	private JTextArea mChatTextArea;
	private Client mClient;
	private JTextField mTextField;
	private JTextField mNameTextField;
	private JLabel mUserNameLabel;
	private DefaultListModel<String> mListModel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					Front window = new Front();
					window.mFrame.setVisible(true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	public Front()
	{
		initialize();
		mClient = new Client("127.0.0.1", 5001, this);
		mClient.start();
	}

	public void appendToChatText(String message)
	{
		mChatTextArea.append(message);
		mChatTextArea.setCaretPosition(mChatTextArea.getDocument().getLength());
	}
	
	public JFrame getFrame()
	{
		return mFrame;		
	}
	
	public JTextField getNameTextField()
	{
		return mNameTextField;		
	}
	
	public JTextArea getChatTextArea()
	{
		return mChatTextArea;		
	}
	
	public void addUser(String name)
	{
		mListModel.addElement(name);
	}
	
	public void clearUsers()
	{
		mListModel.clear();
	}
	
	public void addUserNameToTitle(String userName)
	{
		mFrame.setTitle("Client - " + userName); 
	}

	private ActionListener sendListener = new ActionListener()
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (mOptionsBox.getSelectedIndex() == 0)
			{
				if (mTextField.getText().isEmpty()) return;
				mClient.write("BROADCAST " + mTextField.getText());
				mTextField.setText("");
			}
			else
			{
				if (mUsersList.getSelectedIndex() == -1)
				{
					JOptionPane.showMessageDialog(mFrame, "Please select a user to whisper to him!", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (mTextField.getText().isEmpty()) return;
				mClient.write("WHISPER " + mUsersList.getSelectedIndex() + " " + mTextField.getText());
				appendToChatText("to " + mListModel.get(mUsersList.getSelectedIndex()) + " : " + mTextField.getText()+"\n");
				mTextField.setText("");
			}
		}

	};
	
	AbstractAction mSend = new AbstractAction() 
	{
	    public void actionPerformed(ActionEvent e) 
	    {
	        sendListener.actionPerformed(e);
	    }
	};

	private ActionListener setNameListener = new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (!mNameTextField.getText().equals(""))
			{
				if (!mNameTextField.getText().contains(";"))
				{
					mClient.write("NAME " + mNameTextField.getText());
				}
				else
				{
					JOptionPane.showMessageDialog(mFrame, "Character ';' is not permitted in name!\r\nPlease use another name!", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			else
			{
				JOptionPane.showMessageDialog(mFrame, "Name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	};

	private JScrollPane mTextScrollPane;
	private JButton mSetButton;
	private JScrollPane mUsersScrollPane;
	private JList<String> mUsersList;
	private JLabel mConnectedUsersLel;
	private JPanel mSendOptionsPanel;
	private JComboBox<String> mOptionsBox;
	private JLabel mOptionsLabel;

	private void initialize()
	{
		mFrame = new JFrame();
		mFrame.setTitle("Client");
		mFrame.setResizable(false);
		mFrame.setBounds(100, 100, 1024, 600);
		mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mFrame.getContentPane().setLayout(null);

		mOutputPanel = new JPanel();
		mOutputPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		mOutputPanel.setBounds(12, 12, 769, 460);
		mFrame.getContentPane().add(mOutputPanel);
		mOutputPanel.setLayout(null);

		mTextScrollPane = new JScrollPane();
		mTextScrollPane.setBounds(0, 0, 769, 460);
		mTextScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		mOutputPanel.add(mTextScrollPane);

		mChatTextArea = new JTextArea();
		mTextScrollPane.setViewportView(mChatTextArea);

		mStatsPanel = new JPanel();
		mStatsPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		mStatsPanel.setBounds(12, 483, 213, 78);
		mFrame.getContentPane().add(mStatsPanel);
		mStatsPanel.setLayout(null);

		mUserNameLabel = new JLabel("Your name, you can change it");
		mUserNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		mUserNameLabel.setFont(new Font("Times New Roman", Font.PLAIN, 11));
		mUserNameLabel.setBounds(10, 11, 193, 14);
		mStatsPanel.add(mUserNameLabel);

		mNameTextField = new JTextField();
		mNameTextField.setBounds(10, 36, 92, 31);
		mStatsPanel.add(mNameTextField);
		mNameTextField.setColumns(10);

		mSetButton = new JButton("Set");
		mSetButton.addActionListener(setNameListener);
		mSetButton.setFont(new Font("Dialog", Font.PLAIN, 18));
		mSetButton.setToolTipText("Set your name with this button");
		mSetButton.setBounds(114, 37, 89, 29);
		mStatsPanel.add(mSetButton);

		mInputPanel = new JPanel();
		mInputPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		mInputPanel.setBounds(237, 483, 544, 78);
		mFrame.getContentPane().add(mInputPanel);
		mInputPanel.setLayout(null);

		mSendButton = new JButton("SEND");
		mSendButton.addActionListener(sendListener);
		mSendButton.setFont(new Font("Tahoma", Font.PLAIN, 17));
		mSendButton.setBounds(446, 18, 89, 39);
		mInputPanel.add(mSendButton);

		mTextField = new JTextField();
		mTextField.setToolTipText("Type a message here...");
		mTextField.setBounds(10, 21, 424, 36);
		mTextField.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "Send");
		mTextField.getActionMap().put("Send",mSend);
		mInputPanel.add(mTextField);
		mTextField.setColumns(10);
		
		JPanel mUsersPanel = new JPanel();
		mUsersPanel.setBorder(null);
		mUsersPanel.setBounds(791, 12, 215, 460);
		mFrame.getContentPane().add(mUsersPanel);
		mUsersPanel.setLayout(null);
		
		mUsersScrollPane = new JScrollPane();
		mUsersScrollPane.setBounds(0, 39, 215, 421);
		mUsersScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		mUsersPanel.add(mUsersScrollPane);
		
		mListModel = new DefaultListModel<String>();
		mUsersList = new JList<String>(mListModel);
		mUsersScrollPane.setViewportView(mUsersList);
		
		
		mConnectedUsersLel = new JLabel("Connected Users");
		mConnectedUsersLel.setFont(new Font("Dialog", Font.BOLD, 18));
		mConnectedUsersLel.setHorizontalAlignment(SwingConstants.CENTER);
		mConnectedUsersLel.setBounds(12, 12, 191, 15);
		mUsersPanel.add(mConnectedUsersLel);
		
		mSendOptionsPanel = new JPanel();
		mSendOptionsPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		mSendOptionsPanel.setBounds(791, 483, 215, 78);
		mFrame.getContentPane().add(mSendOptionsPanel);
		mSendOptionsPanel.setLayout(null);
		
		DefaultComboBoxModel<String> boxModel = new DefaultComboBoxModel<String>(); 
		boxModel.addElement("Send to all users");
		boxModel.addElement("Whisper");
		mOptionsBox = new JComboBox<String>(boxModel);
		mOptionsBox.setSelectedIndex(0);
		mOptionsBox.setBounds(12, 42, 191, 24);
		mSendOptionsPanel.add(mOptionsBox);
		
		mOptionsLabel = new JLabel("Choose sending mode");
		mOptionsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		mOptionsLabel.setBounds(12, 12, 191, 15);
		mSendOptionsPanel.add(mOptionsLabel);
	}
}
