import java.util.Hashtable;


public abstract class GlobalHash
{
	static private Hashtable<String,Integer> mHash = new Hashtable<String,Integer>();
	static public void insert(String value)
	{
		mHash.put(value, mHash.size());
	}
	static public boolean contains(String value)
	{
		return mHash.containsKey(value);
	}
	static public void remove(String value)
	{
		mHash.remove(value);
	}
	static public int size()
	{
		return mHash.size();
	}
	
}
