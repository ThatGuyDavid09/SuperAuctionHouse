package com.highmarsorbit.superauctionhouse.storage;

import com.highmarsorbit.superauctionhouse.SuperAuctionHouse;
import com.highmarsorbit.superauctionhouse.util.AuctionItem;
import org.apache.commons.lang.SerializationException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A non-persistent storage for testing.
 */
public class DummyStorage implements Storage {
    private ArrayList<AuctionItem> auctions;
    @Override
    public boolean storeAuction(AuctionItem item) {
        auctions.add(item);
        return true;
    }

    @Override
    public boolean storeAuctions(AuctionItem[] items) {
        this.auctions.addAll(List.of(items));
        return true;
    }

    @Override
    public List<AuctionItem> getAllAuctions() {
        return auctions;
    }
    @Override
    public List<AuctionItem> getCurrentAuctions() {
        return auctions.stream()
                .filter(i -> !i.isExpired())
                .collect(Collectors.toList());
    }

    @Override
    public int getMaxId() {
        return auctions.stream()
                .mapToInt(AuctionItem::getId)
                .max().orElse(0);
    }

    @Override
    public boolean updateAuction(AuctionItem item) {
        int id = item.getId();
        auctions.removeIf(i -> i.getId() == id);
        auctions.add(item);
        return true;
    }

    @Override
    public boolean deleteAuction(int auctionId) {
        auctions.removeIf(i -> i.getId() == auctionId);
        return true;
    }

    @Override
    public boolean selfTest() {
        return true;
    }


    @Override
    public boolean open() {
        File databaseFile = new File(SuperAuctionHouse.getInstance().getDataFolder().getAbsolutePath(), "auctions");
        try (FileInputStream fis = new FileInputStream(databaseFile);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            auctions = (ArrayList<AuctionItem>) ois.readObject();
            if (auctions == null) {
                throw new SerializationException("Deserialized into null object");
            }
        } catch (FileNotFoundException e) {
            auctions = new ArrayList<>();
            return true;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        } catch (ClassNotFoundException|SerializationException e) {
            e.printStackTrace();
            SuperAuctionHouse.getLogging().warning("Database deserialization failed! Using empty array");
            auctions = new ArrayList<>();
            return true;
        }
        return true;
    }

    @Override
    public boolean close() {
        File databaseFile = new File(SuperAuctionHouse.getInstance().getDataFolder().getAbsolutePath(), "auctions");
        // Delete old database
        if (databaseFile.exists()) {
            databaseFile.delete();
        }

        try (FileOutputStream fos = new FileOutputStream(databaseFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(auctions);
        } catch (IOException e) {
            // TODO consider removing
            e.printStackTrace();
            SuperAuctionHouse.getLogging().severe("Error while saving database to file!");
            return false;
        }
        return true;
    }

    @Override
    public boolean clear() {
        auctions.clear();
        return true;
    }
}
