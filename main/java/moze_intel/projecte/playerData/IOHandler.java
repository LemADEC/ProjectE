package moze_intel.projecte.playerData;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import moze_intel.projecte.MozeCore;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

public abstract class IOHandler
{
	private static File knowledgeFile;
	private static File bagDataFile;
	
	public static void init(File knowledge, File bagData)
	{
		if (!knowledge.exists())
    	{
    		try 
    		{
    			knowledge.createNewFile();
			}
    		catch (IOException e) 
    		{
    			PELogger.logFatal("Couldn't create transmutation knowledge file!");
				e.printStackTrace();
			}
    	}
		
		if (!bagData.exists())
    	{
    		try 
    		{
    			bagData.createNewFile();
			}
    		catch (IOException e) 
    		{
    			PELogger.logFatal("Couldn't create alchemical bag data file!");
				e.printStackTrace();
			}
    	}
		
		knowledgeFile = knowledge;
		bagDataFile = bagData;
		
		readData();
	}
	
	private static void readData()
	{
		NBTTagCompound knowledge = null;
		
		try
		{
			knowledge = CompressedStreamTools.read(knowledgeFile);
		}
		catch (IOException e) 
		{
			PELogger.logFatal("Caught exception in file I/O (if this is the first time you load the world, this is normal)");
			e.printStackTrace();
		}
		
		if (knowledge != null)
		{
			NBTTagList tomeKnowledge = knowledge.getTagList("Tome Knowledge", NBT.TAG_COMPOUND);
			
			for (int i = 0; i < tomeKnowledge.tagCount(); i++)
			{
				NBTTagCompound tag = tomeKnowledge.getCompoundTagAt(i);
				
				String username = tag.getString("player");
				
				if (!username.isEmpty())
				{
					TransmutationKnowledge.setAllKnowledge(username);
				}
			}
			
			NBTTagList list = knowledge.getTagList("knowledge", NBT.TAG_COMPOUND);
			
			for (int i = 0; i < list.tagCount(); i++)
			{
				NBTTagCompound subTag = list.getCompoundTagAt(i);
				
				LinkedList<ItemStack> stackList = new LinkedList();
				
				NBTTagList subList = subTag.getTagList("data", NBT.TAG_COMPOUND);
				
				for (int j = 0; j < subList.tagCount(); j++)
				{
					ItemStack stack = ItemStack.loadItemStackFromNBT(subList.getCompoundTagAt(j));
					
					if (stack != null)
					{
						stackList.add(stack);
					}
				}
				
				TransmutationKnowledge.setKnowledge(subTag.getString("player"), stackList);
			}
		}
		
		NBTTagCompound bagData = null;
		
		try
		{
			bagData = CompressedStreamTools.read(bagDataFile);
		}
		catch (Exception e)
		{
			PELogger.logFatal("Caught exception in file I/O (if this is the first time you load the world, this is normal)");
			e.printStackTrace();
		}
		
		if (bagData != null)
		{
			NBTTagList list = bagData.getTagList("bagdata", NBT.TAG_COMPOUND);
			
			for (int i = 0; i < list.tagCount(); i++)
			{
				NBTTagCompound nbt = list.getCompoundTagAt(i);
				
				NBTTagList subList = nbt.getTagList("data", NBT.TAG_COMPOUND);
				
				for (int j = 0; j < subList.tagCount(); j++)
				{
					NBTTagCompound subNbt = subList.getCompoundTagAt(j);
					
					ItemStack[] inv = new ItemStack[104];
					
					NBTTagList subList2 = subNbt.getTagList("inv", NBT.TAG_COMPOUND);
					
					for (int k = 0; k < subList2.tagCount(); k++)
					{
						NBTTagCompound subNbt2 = subList2.getCompoundTagAt(k);
						
						inv[subNbt2.getByte("index")] = ItemStack.loadItemStackFromNBT(subNbt2);
					}
					
					AlchemicalBagData.set(nbt.getString("player"), subNbt.getByte("color"), inv);
				}
			}
		}
	}
	
	public static void saveData()
	{
		try
		{
			CompressedStreamTools.write(TransmutationKnowledge.getAsNBT(), knowledgeFile);
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		try
		{
			CompressedStreamTools.write(AlchemicalBagData.getAsNBT(), bagDataFile);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}