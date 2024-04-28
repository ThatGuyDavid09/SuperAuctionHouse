package com.highmarsorbit.superauctionhouse.util;

import com.highmarsorbit.superauctionhouse.SuperAuctionHouse;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.*;

/**
 * Wrapper around ItemStack so it can be serialized in a database.
 */
public class SerializableItemStack implements Serializable {
    private ItemStack item;
    public SerializableItemStack(ItemStack item) {
        this.item = item;
    }

    public SerializableItemStack() { super(); }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    @Serial
    private void writeObject(ObjectOutputStream outputStream) throws IllegalStateException {
        try (ByteArrayOutputStream bOutputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(bOutputStream);) {

            dataOutput.writeObject(item);

            String base64 = Base64Coder.encodeLines(bOutputStream.toByteArray());
            outputStream.writeUTF(base64);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    @Serial
    private void readObject(ObjectInputStream inputStream) throws ClassNotFoundException, IOException
    {
        String base64 = inputStream.readUTF();
        byte[] itemStackData = Base64Coder.decodeLines(base64);

        try (ByteArrayInputStream bInputStream = new ByteArrayInputStream(itemStackData);
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(bInputStream);) {

            item = (ItemStack) dataInput.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }
}
