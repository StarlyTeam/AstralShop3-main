package xyz.starly.astralshop.registry;

import org.bukkit.plugin.java.JavaPlugin;

import org.jetbrains.annotations.NotNull;
import xyz.starly.astralshop.api.registry.ShopRegistry;
import xyz.starly.astralshop.api.shop.Shop;
import xyz.starly.astralshop.api.shop.ShopItem;
import xyz.starly.astralshop.api.shop.ShopPage;

import xyz.starly.astralshop.database.ConnectionPoolManager;
import xyz.starly.astralshop.shop.ShopImpl;
import xyz.starly.astralshop.shop.ShopItemImpl;
import xyz.starly.astralshop.shop.ShopPageImpl;
import xyz.starly.astralshop.shop.serialization.sql.ShopItemSQLSerializer;

import java.sql.*;

import java.util.*;
import java.util.logging.Logger;

@SuppressWarnings("all")
public class SQLShopRegistry implements ShopRegistry {

    private final Logger LOGGER;
    private ConnectionPoolManager connectionPoolManager;
    private final Map<String, Shop> shopMap;

    private final String SHOP_SUFFIX = "_shop";
    private final String SHOP_ITEM_SUFFIX = "_items";
    private final String SHOP_PAGE_SUFFIX = "_page";

    private JavaPlugin plugin;

    public SQLShopRegistry(JavaPlugin plugin) {
        this.LOGGER = plugin.getLogger();
        this.shopMap = new HashMap<>();
        this.plugin = plugin;
        connection();
    }

    private void connection() {
        ConnectionPoolManager.initializingPoolManager(plugin.getConfig());
        ConnectionPoolManager pool = ConnectionPoolManager.getInternalPool();

        try {
            pool.getConnection();
            this.connectionPoolManager = pool;
            ;
        } catch (SQLException e) {
            LOGGER.info(e.getMessage());
        }

        LOGGER.info("성공적으로 MYSQL 연결하였습니다.");
    }

    @Override
    public void loadShops() {
        if (plugin.getConfig().getBoolean("mysql.use")) {
            try {
                ResultSet showResultSet = connectionPoolManager.getConnection().createStatement().executeQuery("show TABLES");
                List<ShopPage> shopPageList = new ArrayList<>();

                while (showResultSet.next()) {
                    String shopName = showResultSet.getString(1);
                    if (shopName.contains(SHOP_SUFFIX + SHOP_PAGE_SUFFIX)) {
                        String selectSQL = "SELECT * FROM " + shopName;

                        try {
                            ResultSet resultSet = connectionPoolManager.getConnection().createStatement().executeQuery(selectSQL);

                            while (resultSet.next()) {
                                int pageNum = resultSet.getInt(1);
                                String guiTitle = resultSet.getString(2);
                                int guiRows = resultSet.getInt(3);

                                Map<Integer, ShopItem> shopItems = getShopItems(shopName);

                                ShopPage shopPage = new ShopPageImpl(pageNum, guiTitle, guiRows, shopItems);
                                shopPageList.add(shopPage);

                                String npc = getNpcName(shopName);
                                Shop shop = new ShopImpl(shopName, npc, shopPageList);

                                shopMap.put(shopName.replace(SHOP_SUFFIX, "").replace(SHOP_PAGE_SUFFIX, ""), shop);
                            }
                        } catch (SQLException e) {
                            LOGGER.warning("상점 페이지를 로드하는 중 오류가 발생하였습니다. " + e.getMessage());
                        }
                    }
                }
            } catch (SQLException e) {
                LOGGER.warning("상점을 로드하는 도중 치명적인 오류가 발생하였습니다. " + e.getMessage());
            }
        }
    }

    private Map<Integer, ShopItem> getShopItems(String shopName) {
        String selectSQL = "SELECT * FROM " + shopName + SHOP_SUFFIX + SHOP_ITEM_SUFFIX;
        Map<Integer, ShopItem> shopItems = new HashMap<>();

        try {
            ResultSet resultSet = connectionPoolManager.getConnection().createStatement().executeQuery(selectSQL);

            while (resultSet.next()) {
                String encodedItemStack = resultSet.getString(1);
                int slot = resultSet.getInt(2);

                ShopItem shopItem = new ShopItemImpl(ShopItemSQLSerializer.deserialize(encodedItemStack));
                shopItems.put(slot, shopItem);
            }

        } catch (SQLException e) {
            LOGGER.warning("아이템을 불러오는 중 오류가 발생하였습니다. " + e.getMessage());
        }
        return shopItems;
    }

    private String getNpcName(String shopName) {
        String npc = "";

        String selectSQL = "SELECT * FROM " + shopName;

        try {
            ResultSet resultSet = connectionPoolManager.getConnection().createStatement().executeQuery(selectSQL);

            while (resultSet.next()) {
                npc = resultSet.getString(2);
            }

        } catch (SQLException e) {
            LOGGER.warning("상점을 불러오는 중 오류가 발생하였습니다. " + e.getMessage());
        }

        return npc;
    }


    @Override
    public void saveShops() {
        System.out.println("Mysql 저장중...");

        shopMap.forEach((name, shop) -> {
            String npc = shop.getNpc();
            String guiTitle = shop.getGuiTitle();
            List<ShopPage> shopPages = shop.getShopPages();

            shopPages.forEach(shopPage -> {
                Map<Integer, ShopItem> items = shopPage.getItems();
                items.forEach((slot, shopItem) -> {
                    String shopItemTable = name + SHOP_SUFFIX + SHOP_ITEM_SUFFIX;

                    String encoded = ShopItemSQLSerializer.serialize(shopItem);

                    double sellPrice = shopItem.getSellPrice();
                    double buyPrice = shopItem.getBuyPrice();

                    int maxStock = shopItem.getStock();
                    int remainStock = shopItem.getRemainStock();

                    List<String> commands = shopItem.getCommands();

                    try {
                        String actionSQL = "INSERT INTO `" + shopItemTable + "` (`itemstack`, `slot`, `sell_price`, `buy_price`, `max_stock`, `remain_stock`, `popularity`, `commands`) " +
                                "VALUES ('" + encoded + "', '" + slot + "', '" + sellPrice + "', '" + buyPrice + "', '" + maxStock + "', '" + remainStock + "', '" + -1 + "', '" + commands + "') " +
                                "ON DUPLICATE KEY UPDATE itemstack = VALUES(itemstack), slot = VALUES(slot), sell_price = VALUES(sell_price), buy_price = VALUES(buy_price), " +
                                "max_stock = VALUES(max_stock), remain_stock = VALUES(remain_stock), popularity = VALUES(popularity), commands = VALUES(commands)";

                        connectionPoolManager.getConnection().createStatement().executeUpdate(actionSQL);

                    } catch (SQLException e) {
                        LOGGER.warning("상점을 저장하는 도중 치명적인 오류가 발생하였습니다. " + e.getMessage());
                    }
                });
            });
        });
    }

    @Override
    public boolean createShop(String name) {
        String selectSQL = "SHOW TABLES FROM " + connectionPoolManager.getDatabase() + " LIKE '" + name + SHOP_SUFFIX + "'";
        String createShopTableSQL = "CREATE TABLE IF NOT EXISTS `" + name + SHOP_SUFFIX + "` (`shop_name` VARCHAR(255), `permission` VARCHAR(255), `npc` VARCHAR(255));";
        String createShopPageTableSQL = "CREATE TABLE IF NOT EXISTS `" + name + SHOP_SUFFIX + SHOP_PAGE_SUFFIX + "` (`pageNum` INTEGER,`gui_title` VARCHAR(255), `gui_rows` INTEGER);";
        String createShopItemTableSQL = "CREATE TABLE IF NOT EXISTS `" + name + SHOP_SUFFIX + SHOP_ITEM_SUFFIX + "` (" +
                "`itemstack` VARCHAR(5000), " +
                "`slot` INTEGER(2), " +
                "`sell_price` DOUBLE, " +
                "`buy_price` DOUBLE, " +
                "`max_stock` INTEGER, " +
                "`remain_stock` INTEGER, " +
                "`popularity` INTEGER," +
                "`commands` VARCHAR(1000)," +
                "PRIMARY KEY(slot)" +
                ");";

        try {
            Statement createShopStatement = connectionPoolManager.getConnection().createStatement();
            ResultSet createShopResultSet = createShopStatement.executeQuery(selectSQL);

            if (createShopResultSet.next()) {
                return false;
            }

            connectionPoolManager.getConnection().createStatement().executeUpdate(createShopTableSQL);

            String insertSQL = "INSERT INTO " + name + SHOP_SUFFIX + " (shop_name, permission, npc) VALUES (?,?,?)";
            PreparedStatement preparedStatement = connectionPoolManager.getConnection().prepareStatement(insertSQL);

            preparedStatement.setString(1, "");
            preparedStatement.setString(2, "");
            preparedStatement.setString(3, "");

            preparedStatement.executeUpdate();

            connectionPoolManager.getConnection().createStatement().executeUpdate(createShopPageTableSQL);
            connectionPoolManager.getConnection().createStatement().executeUpdate(createShopItemTableSQL);

            String insertPageSQL = "INSERT INTO " + name + SHOP_SUFFIX + SHOP_PAGE_SUFFIX + " (pageNum, gui_title, gui_rows) VALUES (?,?,?)";
            preparedStatement = connectionPoolManager.getConnection().prepareStatement(insertPageSQL);

            preparedStatement.setInt(1, 1);
            preparedStatement.setString(2, name);
            preparedStatement.setInt(3, 5);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            LOGGER.warning("상점을 생성하는 도중 치명적인 오류가 발생하였습니다. " + e.getMessage());
        }

        return true;
    }

    @Override
    public boolean deleteShop(String name) {
        String selectSQL = "SHOW TABLES FROM " + connectionPoolManager.getDatabase() + " LIKE '" + name + SHOP_SUFFIX + "'";

        try {
            ResultSet resultSet = connectionPoolManager.getConnection().createStatement().executeQuery(selectSQL);
            if (resultSet.next()) {
                String deleteShopSQL = "DROP TABLE " + name + SHOP_SUFFIX;
                String deleteShopItemsSQL = "DROP TABLE " + name + SHOP_SUFFIX + SHOP_ITEM_SUFFIX;
                String deleteShopPageSQL = "DROP TABLE " + name + SHOP_SUFFIX + SHOP_PAGE_SUFFIX;

                connectionPoolManager.getConnection().createStatement().execute(deleteShopSQL);
                connectionPoolManager.getConnection().createStatement().execute(deleteShopItemsSQL);
                connectionPoolManager.getConnection().createStatement().execute(deleteShopPageSQL);
                return true;
            }
        } catch (SQLException e) {
            LOGGER.warning("상점을 제거하는 도중 치명적인 오류가 발생하였습니다. " + e.getMessage());
        }

        return false;
    }

    @Override
    public Shop getShop(String name) {
        return shopMap.get(name);
    }

    @Override
    public @NotNull List<Shop> getShops() {
        return new ArrayList<>(shopMap.values());
    }

    @Override
    public List<String> getShopNames() {
        return new ArrayList<>(shopMap.keySet());
    }
}