package com.highmarsorbit.superauctionhouse.storage;

import com.highmarsorbit.superauctionhouse.SuperAuctionHouse;
import com.highmarsorbit.superauctionhouse.util.AuctionItem;
import com.highmarsorbit.superauctionhouse.util.AuctionType;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.io.*;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SQLiteStorage implements Storage {
    private final LinkedList<AuctionAction> updateQueue = new LinkedList<>();
    private volatile boolean isRunningSynchronousCommand = false;
    private BukkitTask updateTask;

    @Override
    public boolean storeAuction(AuctionItem item) {
        return updateQueue.offer(new AuctionAction(AuctionActionType.CREATE, item));
    }

    @Override
    public boolean storeAuctions(AuctionItem[] items) {
        for (AuctionItem item : items) {
            boolean success = this.storeAuction(item);
            if (!success) return false;
        }
        return true;
    }

    @Override
    public List<AuctionItem> getAllAuctions() {
        isRunningSynchronousCommand = true;
        String getAllSql = "SELECT auctionData FROM auctions;";
        List<AuctionItem> auctions = new ArrayList<>();
        try (Connection conn = this.connect()) {
            if (conn == null)
            {
                isRunningSynchronousCommand = false;
                return auctions;
            }

            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery(getAllSql);
            if (results == null) {
                isRunningSynchronousCommand = false;
                return auctions;
            }

            while (results.next()) {
                String auctionString = results.getString("auctionData");
                ByteArrayInputStream bInputStream = new ByteArrayInputStream(auctionString.getBytes());
                ObjectInputStream in = new ObjectInputStream(bInputStream);
                auctions.add((AuctionItem) in.readObject());
            }
        } catch (SQLException | IOException e) {
            isRunningSynchronousCommand = false;
            return auctions;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        isRunningSynchronousCommand = false;
        return auctions;
    }

    @Override
    public List<AuctionItem> getCurrentAuctions() {
        isRunningSynchronousCommand = true;
        String getCurrentSql = "SELECT auctionData FROM auctions WHERE endTime < ?;";
        List<AuctionItem> auctions = new ArrayList<>();
        try (Connection conn = this.connect()) {
            if (conn == null) {
                isRunningSynchronousCommand = false;
                return auctions;
            }

            PreparedStatement pstmt = conn.prepareStatement(getCurrentSql);
            pstmt.setTimestamp(1, Timestamp.from(Instant.ofEpochSecond(System.currentTimeMillis())));
            ResultSet results = pstmt.executeQuery();
            if (results == null) {
                isRunningSynchronousCommand = false;
                return auctions;
            }

            while (results.next()) {
                String auctionString = results.getString("auctionData");
                ByteArrayInputStream bInputStream = new ByteArrayInputStream(auctionString.getBytes());
                ObjectInputStream in = new ObjectInputStream(bInputStream);
                auctions.add((AuctionItem) in.readObject());
            }
        } catch (SQLException | IOException e) {
            isRunningSynchronousCommand = false;
            return auctions;
        } catch (ClassNotFoundException e) {
            isRunningSynchronousCommand = false;
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        isRunningSynchronousCommand = false;
        return auctions;
    }

    @Override
    public int getMaxId() {
        isRunningSynchronousCommand = true;
        String getMaxIdSql = "SELECT MAX(id) FROM auctions;";
        try (Connection conn = this.connect()) {
            if (conn == null) {
                isRunningSynchronousCommand = false;
                // For safety just something super big
                return 99999999;
            }

            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery(getMaxIdSql);
            if (results == null) {
                isRunningSynchronousCommand = false;
                return 0;
            }

            while (results.next()) {
                return results.getInt(1);
            }
        } catch (SQLException e) {
            return 99999999;
        }
        isRunningSynchronousCommand = false;
        return 0;
    }

    @Override
    public boolean updateAuction(AuctionItem item) {
        return updateQueue.offer(new AuctionAction(AuctionActionType.UPDATE, item));
    }

    @Override
    public boolean deleteAuction(int auctionId) {
        return updateQueue.offer(new AuctionAction(AuctionActionType.DELETE, auctionId));
    }

    @Override
    public boolean open() {
        try(Connection conn = this.connect()) {
            if (conn == null) return false;
            Statement stmt = conn.createStatement();

            String createAuctionTableSql = """
                    CREATE TABLE IF NOT EXISTS auctions (
                        id INTEGER PRIMARY KEY,
                        sellerUUID VARCHAR(40) NOT NULL,
                        createTime DATETIME NOT NULL,
                        endTime DATETIME NOT NULL,
                        auctionType VARCHAR(3) CHECK( auctionType IN ('ACT','BIN') ),
                        bought BOOL NOT NULL,
                        currentPrice DOUBLE NOT NULL,
                        auctionData TEXT NOT NULL
                    );""";
            stmt.execute(createAuctionTableSql);

            String createTransactionTableSql = """
                    CREATE TABLE IF NOT EXISTS transactions (
                    	id INTEGER PRIMARY KEY,
                    	auctionId INTEGER NOT NULL,
                    	transactionTime DATETIME NOT NULL,
                    	playerUUID VARCHAR(40) NOT NULL,
                    	newPrice DOUBLE NOT NULL,
                    	FOREIGN KEY(auctionId) REFERENCES auctions(id)
                    );""";
            stmt.execute(createTransactionTableSql);

            this.updateTask = Bukkit.getScheduler().runTaskTimerAsynchronously(SuperAuctionHouse.getInstance(),
                    () -> {
                        if (isRunningSynchronousCommand) return;
                        AuctionAction action = updateQueue.poll();
                        if (action == null) return;

                        List<AuctionAction> batch = new ArrayList<>();
                        batch.add(action);
                        while (!updateQueue.isEmpty()) {
                            AuctionAction nextAction = updateQueue.poll();
                            if (nextAction == null) break;
                            if (nextAction.actionType != action.actionType) break;
                            if (nextAction.actionType == AuctionActionType.CLEAR_ALL) break;
                            batch.add(nextAction);
                        }
                        if (batch.size() <= 1) {
                            boolean success = this.doAction(action);
                            if (!success) {
                                updateQueue.addFirst(action);
                            }
                        } else {
                            List<AuctionAction> failedActions = this.doActionBatch(batch);
                            for (AuctionAction failedAction : failedActions) {
                                updateQueue.addFirst(failedAction);
                            }
                        }
                    }, 0, 1);

        } catch (SQLException e) {
            e.printStackTrace();
            SuperAuctionHouse.getLogging().severe("Error when connecting to SQLite3 database!");
            return false;
        }
        return true;
    }

    private boolean doAction(AuctionAction action) {
        try (Connection conn = this.connect()) {
            if (conn == null) return false;
            AuctionItem auction = action.auction;
            switch (action.actionType) {
                case CREATE -> {
                    String createAuctionSql = """
                            INSERT INTO auctions
                            (id,sellerUUID,createTime,endTime,auctionType,bought,currentPrice,auctionData)
                            VALUES (?,?,?,?,?,?,?,?);""";

                    PreparedStatement pstmt = conn.prepareStatement(createAuctionSql);
                    pstmt.setInt(1, auction.getId());
                    pstmt.setString(2, auction.getSeller().getUniqueId().toString());
                    pstmt.setTimestamp(3, Timestamp.from(auction.getCreateTime()));
                    pstmt.setTimestamp(4, Timestamp.from(auction.getEndTime()));
                    pstmt.setString(5, auction.getAuctionType() == AuctionType.AUCTION ? "ACT" : "BIN");
                    pstmt.setBoolean(6, auction.getBought());
                    pstmt.setDouble(7, auction.getPrice());

                    ByteArrayOutputStream bOutputStream = new ByteArrayOutputStream();
                    ObjectOutputStream out = new ObjectOutputStream(bOutputStream);
                    out.writeObject(auction);
                    out.close();
                    String auctionString = bOutputStream.toString();
                    pstmt.setString(8, auctionString);
                    pstmt.executeUpdate();
                }
                case UPDATE -> {
                    String updateAuctionSql = """
                            UPDATE auctions
                            SET bought = ?,
                                currentPrice = ?,
                                auctionData = ?
                            WHERE id = ?""";

                    PreparedStatement pstmt = conn.prepareStatement(updateAuctionSql);
                    pstmt.setInt(1, auction.getId());
                    pstmt.setBoolean(2, auction.getBought());
                    pstmt.setDouble(3, auction.getPrice());

                    ByteArrayOutputStream bOutputStream = new ByteArrayOutputStream();
                    ObjectOutputStream out = new ObjectOutputStream(bOutputStream);
                    out.writeObject(auction);
                    out.close();
                    String auctionString = bOutputStream.toString();
                    pstmt.setString(4, auctionString);
                    pstmt.executeUpdate();
                }
                case DELETE -> {
                    String deleteAuctionSql = """
                            DELETE FROM auctions
                            WHERE id = ?""";

                    PreparedStatement pstmt = conn.prepareStatement(deleteAuctionSql);
                    pstmt.setInt(1, auction.getId());
                    pstmt.executeUpdate();
                }
                case CLEAR_ALL -> {
                    updateQueue.clear();
                    String dropAuctions = "DROP TABLE auctions";
                    String dropTransactions = "DROP TABLE transactions";

                    Statement stmt = conn.createStatement();
                    stmt.execute(dropAuctions);
                    stmt.execute(dropTransactions);
                    this.close();
                    this.open();
                }
            }
        } catch (SQLException | IOException e) {
            // TODO change stack trace prints into simple error prints (like e.toString())
            e.printStackTrace();
            SuperAuctionHouse.getLogging().warning("Error while performing auction update!");
            return false;
        }
        return true;
    }


    private List<AuctionAction> doActionBatch(List<AuctionAction> batch) {
        // I hate this code
        Connection conn = this.connect();
        if (conn == null) return new ArrayList<>();
        List<AuctionAction> failed = new ArrayList<>();

        AuctionActionType type = batch.get(0).actionType;
        String sql = "";
        switch (type) {
            case CREATE -> sql = """
                            INSERT INTO auctions
                            (id,sellerUUID,createTime,endTime,auctionType,bought,currentPrice,auctionData)
                            VALUES (?,?,?,?,?,?,?,?);""";
            case UPDATE -> sql = """
                            UPDATE auctions
                            WHERE id = ?
                            SET bought = ?,
                                currentPrice = ?,
                                auctionData = ?""";
            case DELETE -> sql = """
                            DELETE FROM auctions
                            WHERE id = ?""";
        }
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
        } catch (SQLException e) {
            return batch;
        }

        for (AuctionAction action : batch) {
            AuctionItem auction = action.auction;
            try {
                switch (action.actionType) {
                    case CREATE -> {
                        pstmt.setInt(1, auction.getId());
                        pstmt.setString(2, auction.getSeller().getUniqueId().toString());
                        pstmt.setTimestamp(3, Timestamp.from(auction.getCreateTime()));
                        pstmt.setTimestamp(4, Timestamp.from(auction.getEndTime()));
                        pstmt.setString(5, auction.getAuctionType() == AuctionType.AUCTION ? "ACT" : "BIN");
                        pstmt.setBoolean(6, auction.getBought());
                        pstmt.setDouble(7, auction.getPrice());


                        ByteArrayOutputStream bOutputStream = new ByteArrayOutputStream();
                        ObjectOutputStream out = new ObjectOutputStream(bOutputStream);
                        out.writeObject(auction);
                        out.close();
                        String auctionString = bOutputStream.toString();
                        pstmt.setString(8, auctionString);

                        pstmt.addBatch();
                    }
                    case UPDATE -> {
                        pstmt.setInt(1, auction.getId());
                        pstmt.setBoolean(2, auction.getBought());
                        pstmt.setDouble(3, auction.getPrice());

                        ByteArrayOutputStream bOutputStream = new ByteArrayOutputStream();
                        ObjectOutputStream out = new ObjectOutputStream(bOutputStream);
                        out.writeObject(auction);
                        out.close();
                        String auctionString = bOutputStream.toString();
                        pstmt.setString(4, auctionString);

                        pstmt.addBatch();
                    }
                    case DELETE -> {
                        pstmt.setInt(1, auction.getId());
                        pstmt.addBatch();
                    }
                }
            } catch (SQLException | IOException e) {
                e.printStackTrace();
                SuperAuctionHouse.getLogging().warning("Error while performing auction update!");
                failed.add(action);
            }
        }

        try {
            pstmt.executeBatch();
            conn.close();
        } catch (SQLException e) {
            return batch;
        }
        return failed;
    }


    private Connection connect() {
        try {
            String url = "jdbc:sqlite:" + SuperAuctionHouse.getInstance().getDataFolder().getAbsolutePath() + "/superauctionhouse-sqlite.db";
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            SuperAuctionHouse.getLogging().severe("Error while connecting to SQLite database!");
        }
        return null;
    }

    @Override
    public boolean close() {
        updateTask.cancel();
        while (!updateQueue.isEmpty()) {
            boolean success = doAction(updateQueue.poll());
            if (!success) return false;
        }
        return true;
    }

    @Override
    public boolean selfTest() {
        isRunningSynchronousCommand = true;
        isRunningSynchronousCommand = false;
        return true;
    }

    @Override
    public boolean clear() {
        isRunningSynchronousCommand = true;
        doAction(new AuctionAction(AuctionActionType.CLEAR_ALL));
        isRunningSynchronousCommand = false;
        return true;
    }

    public static class AuctionAction {
        public final AuctionActionType actionType;
        public final AuctionItem auction;
        public final Integer auctionId;

        public AuctionAction(AuctionActionType type) {
            if (type != AuctionActionType.CLEAR_ALL) {
                throw new IllegalStateException("Must specify auction item or auction id for this operation");
            }

            this.actionType = type;
            this.auctionId = null;
            this.auction = null;
        }

        public AuctionAction(AuctionActionType type, int itemId) {
            if (type != AuctionActionType.DELETE) {
                throw new IllegalStateException("Must specify auction item for this operation");
            }

            this.actionType = type;
            this.auctionId = itemId;
            this.auction = null;
        }

        public AuctionAction(AuctionActionType type, AuctionItem item) {
            this.actionType = type;
            this.auction = item;
            this.auctionId = item.getId();
        }
    }

    public enum AuctionActionType {
        CREATE,
        UPDATE,
        DELETE,
        CLEAR_ALL
    }
}
