package me.m4nst3in.m4Utils.database;

import me.m4nst3in.m4Utils.Main;
import me.m4nst3in.m4Utils.home.Home;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.sql.*;
import java.util.*;

public class SQLiteManager {
    private final Main plugin;
    private Connection connection;

    public SQLiteManager(Main plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        File dbFile = new File(dataFolder, "homes.db");

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());

            try (Statement stmt = connection.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS homes (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "player_uuid VARCHAR(36) NOT NULL," +
                        "name VARCHAR(16) NOT NULL," +
                        "world VARCHAR(50) NOT NULL," +
                        "x DOUBLE NOT NULL," +
                        "y DOUBLE NOT NULL," +
                        "z DOUBLE NOT NULL," +
                        "yaw FLOAT NOT NULL," +
                        "pitch FLOAT NOT NULL," +
                        "creation_time BIGINT NOT NULL," +
                        "UNIQUE(player_uuid, name)" +
                        ")");
            }

            plugin.getLogger().info("Conexão com banco de dados SQLite estabelecida com sucesso!");
        } catch (ClassNotFoundException e) {
            plugin.getLogger().severe("Driver JDBC SQLite não encontrado: " + e.getMessage());
        } catch (SQLException e) {
            plugin.getLogger().severe("Erro ao conectar ao banco de dados SQLite: " + e.getMessage());
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                plugin.getLogger().info("Conexão com banco de dados SQLite fechada com sucesso!");
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Erro ao fechar a conexão com o banco de dados SQLite: " + e.getMessage());
        }
    }

    public boolean saveHome(Home home) {
        String sql = "INSERT OR REPLACE INTO homes (player_uuid, name, world, x, y, z, yaw, pitch, creation_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, home.getOwner().toString());
            stmt.setString(2, home.getName());
            stmt.setString(3, home.getLocation().getWorld().getName());
            stmt.setDouble(4, home.getLocation().getX());
            stmt.setDouble(5, home.getLocation().getY());
            stmt.setDouble(6, home.getLocation().getZ());
            stmt.setFloat(7, home.getLocation().getYaw());
            stmt.setFloat(8, home.getLocation().getPitch());
            stmt.setLong(9, home.getCreationTime());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("Erro ao salvar home no banco de dados: " + e.getMessage());
            return false;
        }
    }
    public boolean deleteHome(UUID playerUUID, String homeName) {
        String sql = "DELETE FROM homes WHERE player_uuid = ? AND name = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerUUID.toString());
            stmt.setString(2, homeName);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("Erro ao excluir home do banco de dados: " + e.getMessage());
            return false;
        }
    }

    public boolean renameHome(UUID playerUUID, String oldName, String newName) {
        // Verifica se o novo nome já existe
        if (getHome(playerUUID, newName) != null) {
            return false;
        }

        String sql = "UPDATE homes SET name = ? WHERE player_uuid = ? AND name = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newName);
            stmt.setString(2, playerUUID.toString());
            stmt.setString(3, oldName);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("Erro ao renomear home no banco de dados: " + e.getMessage());
            return false;
        }
    }
    public Home getHome(UUID playerUUID, String homeName) {
        String sql = "SELECT * FROM homes WHERE player_uuid = ? AND LOWER(name) = LOWER(?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerUUID.toString());
            stmt.setString(2, homeName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractHomeFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Erro ao buscar home no banco de dados: " + e.getMessage());
        }

        return null;
    }

    public List<Home> getPlayerHomes(UUID playerUUID) {
        List<Home> homes = new ArrayList<>();
        String sql = "SELECT * FROM homes WHERE player_uuid = ? ORDER BY name ASC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerUUID.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Home home = extractHomeFromResultSet(rs);
                    if (home != null) {
                        homes.add(home);
                    }
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Erro ao buscar homes do jogador no banco de dados: " + e.getMessage());
        }

        return homes;
    }

    public int getHomeCount(UUID playerUUID) {
        String sql = "SELECT COUNT(*) AS count FROM homes WHERE player_uuid = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerUUID.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Erro ao contar homes do jogador no banco de dados: " + e.getMessage());
        }

        return 0;
    }

    public boolean updateHomeLocation(Home home) {
        String sql = "UPDATE homes SET world = ?, x = ?, y = ?, z = ?, yaw = ?, pitch = ? WHERE player_uuid = ? AND name = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, home.getLocation().getWorld().getName());
            stmt.setDouble(2, home.getLocation().getX());
            stmt.setDouble(3, home.getLocation().getY());
            stmt.setDouble(4, home.getLocation().getZ());
            stmt.setFloat(5, home.getLocation().getYaw());
            stmt.setFloat(6, home.getLocation().getPitch());
            stmt.setString(7, home.getOwner().toString());
            stmt.setString(8, home.getName());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("Erro ao atualizar localização da home: " + e.getMessage());
            return false;
        }
    }

    private Home extractHomeFromResultSet(ResultSet rs) throws SQLException {
        String worldName = rs.getString("world");
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            plugin.getLogger().warning("Mundo não encontrado para home: " + rs.getString("name"));
            return null;
        }

        double x = rs.getDouble("x");
        double y = rs.getDouble("y");
        double z = rs.getDouble("z");
        float yaw = rs.getFloat("yaw");
        float pitch = rs.getFloat("pitch");

        Location location = new Location(world, x, y, z, yaw, pitch);
        UUID playerUUID = UUID.fromString(rs.getString("player_uuid"));
        String name = rs.getString("name");
        long creationTime = rs.getLong("creation_time");

        Home home = new Home(name, location, playerUUID);
        home.setCreationTime(creationTime);

        return home;
    }
}