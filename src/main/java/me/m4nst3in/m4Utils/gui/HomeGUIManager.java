package me.m4nst3in.m4Utils.gui;

import me.m4nst3in.m4Utils.Main;
import me.m4nst3in.m4Utils.home.Home;
import me.m4nst3in.m4Utils.home.HomeManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.*;

public class HomeGUIManager implements Listener {
    private final Main plugin;
    private final HomeManager homeManager;

    // Títulos dos menus
    private final String MAIN_MENU_TITLE = "§8Sistema de Homes";
    private final String HOME_LIST_TITLE = "§8Lista de Homes";
    private final String HOME_MANAGE_TITLE = "§8Gerenciar Home: ";

    // Armazena homes que estão sendo renomeadas
    private final Map<UUID, Home> renamingHomes = new HashMap<>();

    public HomeGUIManager(Main plugin, HomeManager homeManager) {
        this.plugin = plugin;
        this.homeManager = homeManager;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void openMainMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, MAIN_MENU_TITLE);

        // Item para criar uma nova home
        ItemStack createItem = createGuiItem(Material.LIME_BED,
                "§a§lCriar Home",
                "§7Defina uma nova home na",
                "§7sua localização atual.",
                "",
                "§fHomes: §e" + homeManager.getHomeCount(player.getUniqueId()) + "/" + homeManager.getMaxHomes(player),
                "",
                "§eClique para criar.");

        // Item para listar homes
        ItemStack listItem = createGuiItem(Material.BOOK,
                "§e§lLista de Homes",
                "§7Visualize e teleporte-se para",
                "§7suas homes existentes.",
                "",
                "§eClique para abrir.");

        // Item para gerenciar homes
        ItemStack manageItem = createGuiItem(Material.CRAFTING_TABLE,
                "§6§lGerenciar Homes",
                "§7Renomeie ou remova suas homes.",
                "",
                "§eClique para abrir.");

        // Item informativo
        ItemStack infoItem = createGuiItem(Material.PAPER,
                "§f§lInformações",
                "§7Você possui §e" + homeManager.getHomeCount(player.getUniqueId()) + "/" + homeManager.getMaxHomes(player) + " §7homes",
                "",
                "§7Você pode criar mais homes",
                "§7adquirindo VIP no servidor.");

        // Posicionamento dos itens
        gui.setItem(11, createItem);
        gui.setItem(13, listItem);
        gui.setItem(15, manageItem);
        gui.setItem(22, infoItem);

        // Preencher espaços vazios com vidro preto
        fillEmptySlots(gui, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

        player.openInventory(gui);
    }

    public void openHomeListMenu(Player player) {
        List<Home> homes = homeManager.getPlayerHomes(player.getUniqueId());

        // Calcula o tamanho necessário para o inventário (múltiplo de 9)
        int size = Math.max(27, (int) (Math.ceil(homes.size() / 9.0) * 9) + 9);
        size = Math.min(size, 54); // Limita a 54 slots (6 linhas)

        Inventory gui = Bukkit.createInventory(null, size, HOME_LIST_TITLE);

        // Adiciona as homes ao inventário
        int slot = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        for (Home home : homes) {
            String worldName = home.getLocation().getWorld().getName();
            String formattedWorld;

            // Formata o nome do mundo para exibição
            switch (worldName.toLowerCase()) {
                case "world":
                    formattedWorld = "§aMundo Principal";
                    break;
                case "world_nether":
                    formattedWorld = "§cNether";
                    break;
                case "world_the_end":
                    formattedWorld = "§5End";
                    break;
                default:
                    formattedWorld = "§7" + worldName;
            }

            // Criar item para a home
            Material material;
            // Escolhe material baseado no mundo
            if (worldName.equalsIgnoreCase("world_nether")) {
                material = Material.NETHERRACK;
            } else if (worldName.equalsIgnoreCase("world_the_end")) {
                material = Material.END_STONE;
            } else {
                material = Material.GRASS_BLOCK;
            }

            ItemStack homeItem = createGuiItem(material,
                    "§e§l" + home.getName(),
                    "§7Coordenadas: §fX: " + (int)home.getLocation().getX() + ", Y: " + (int)home.getLocation().getY() + ", Z: " + (int)home.getLocation().getZ(),
                    "§7Mundo: " + formattedWorld,
                    "§7Criada em: §f" + dateFormat.format(new Date(home.getCreationTime())),
                    "",
                    "§aClique para teleportar",
                    "§eClique com botão direito para gerenciar");

            gui.setItem(slot++, homeItem);
        }

        // Botão para voltar ao menu principal
        ItemStack backItem = createGuiItem(Material.ARROW, "§c§lVoltar", "§7Voltar ao menu principal");
        gui.setItem(size - 5, backItem);

        // Preencher espaços vazios com vidro preto
        fillEmptySlots(gui, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

        player.openInventory(gui);
    }

    public void openHomeManageMenu(Player player, Home home) {
        Inventory gui = Bukkit.createInventory(null, 27, HOME_MANAGE_TITLE + home.getName());

        // Item informativo sobre a home
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        String worldName = home.getLocation().getWorld().getName();
        String formattedWorld;

        // Formata o nome do mundo para exibição
        switch (worldName.toLowerCase()) {
            case "world":
                formattedWorld = "§aMundo Principal";
                break;
            case "world_nether":
                formattedWorld = "§cNether";
                break;
            case "world_the_end":
                formattedWorld = "§5End";
                break;
            default:
                formattedWorld = "§7" + worldName;
        }

        ItemStack infoItem = createGuiItem(Material.BOOK,
                "§e§l" + home.getName(),
                "§7Coordenadas: §fX: " + (int)home.getLocation().getX() + ", Y: " + (int)home.getLocation().getY() + ", Z: " + (int)home.getLocation().getZ(),
                "§7Mundo: " + formattedWorld,
                "§7Criada em: §f" + dateFormat.format(new Date(home.getCreationTime())));

        // Item para teleportar
        ItemStack teleportItem = createGuiItem(Material.ENDER_PEARL,
                "§a§lTeleportar",
                "§7Teleporta para esta home.",
                "",
                "§aClique para teleportar");

        // Item para renomear
        ItemStack renameItem = createGuiItem(Material.NAME_TAG,
                "§6§lRenomear",
                "§7Altera o nome desta home.",
                "",
                "§eClique para renomear");

        // Item para excluir
        ItemStack deleteItem = createGuiItem(Material.BARRIER,
                "§c§lExcluir",
                "§7Remove permanentemente esta home.",
                "",
                "§cClique para excluir");

        // Item para atualizar a localização da home
        ItemStack updateItem = createGuiItem(Material.COMPASS,
                "§b§lAtualizar Localização",
                "§7Atualiza a localização desta home",
                "§7para sua posição atual.",
                "",
                "§bClique para atualizar");

        // Botão para voltar
        ItemStack backItem = createGuiItem(Material.ARROW, "§c§lVoltar", "§7Voltar à lista de homes");

        // Posicionamento dos itens
        gui.setItem(4, infoItem);
        gui.setItem(10, teleportItem);
        gui.setItem(12, renameItem);
        gui.setItem(14, updateItem);
        gui.setItem(16, deleteItem);
        gui.setItem(22, backItem);

        // Preencher espaços vazios com vidro preto
        fillEmptySlots(gui, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        // Verifica se é um menu nosso e se o item é válido
        if (!title.startsWith("§8") || clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        event.setCancelled(true);

        // Menu principal
        if (title.equals(MAIN_MENU_TITLE)) {
            handleMainMenuClick(player, clickedItem);
        }
        // Lista de homes
        else if (title.equals(HOME_LIST_TITLE)) {
            handleHomeListClick(player, clickedItem, event.getClick());
        }
        // Gerenciar home
        else if (title.startsWith(HOME_MANAGE_TITLE)) {
            handleHomeManageClick(player, clickedItem, title.substring(HOME_MANAGE_TITLE.length()));
        }
    }

    private void handleMainMenuClick(Player player, ItemStack clickedItem) {
        if (!clickedItem.hasItemMeta() || !clickedItem.getItemMeta().hasDisplayName()) {
            return;
        }

        String displayName = clickedItem.getItemMeta().getDisplayName();

        if (displayName.equals("§a§lCriar Home")) {
            player.closeInventory();

            // Verificar se jogador atingiu limite de homes
            int currentCount = homeManager.getHomeCount(player.getUniqueId());
            int maxCount = homeManager.getMaxHomes(player);

            if (currentCount >= maxCount) {
                player.sendMessage(Main.colorize("&cVocê atingiu o limite de " + maxCount + " homes!"));
                return;
            }

            // Mensagem para o jogador sobre como criar uma home
            player.sendMessage(Main.colorize("&aPara criar uma home, digite o nome da home no chat:"));
            player.sendMessage(Main.colorize("&7(Digite 'cancelar' para cancelar)"));

            // Registrar o jogador no modo de criação de home
            renamingHomes.put(player.getUniqueId(), null); // null significa criar nova home
        }
        else if (displayName.equals("§e§lLista de Homes")) {
            openHomeListMenu(player);
        }
        else if (displayName.equals("§6§lGerenciar Homes")) {
            // Verificar se o jogador tem homes
            if (homeManager.getHomeCount(player.getUniqueId()) == 0) {
                player.sendMessage(Main.colorize("&cVocê não possui nenhuma home!"));
                player.closeInventory();
                return;
            }

            openHomeListMenu(player);
        }
    }

    private void handleHomeListClick(Player player, ItemStack clickedItem, org.bukkit.event.inventory.ClickType clickType) {
        if (!clickedItem.hasItemMeta() || !clickedItem.getItemMeta().hasDisplayName()) {
            return;
        }

        String displayName = clickedItem.getItemMeta().getDisplayName();

        // Botão de voltar
        if (displayName.equals("§c§lVoltar")) {
            openMainMenu(player);
            return;
        }

        // Se não for o botão de voltar, é um item de home
        if (displayName.startsWith("§e§l")) {
            String homeName = displayName.substring(4); // Remove o prefixo "§e§l"
            Home home = homeManager.getHome(player.getUniqueId(), homeName);

            if (home != null) {
                // Verificar o tipo de clique
                if (clickType.isRightClick()) {
                    // Clique direito - abrir menu de gerenciamento
                    openHomeManageMenu(player, home);
                } else {
                    // Clique esquerdo - teleportar
                    player.closeInventory();
                    player.performCommand("home tp " + homeName);
                }
            }
        }
    }

    private void handleHomeManageClick(Player player, ItemStack clickedItem, String homeName) {
        if (!clickedItem.hasItemMeta() || !clickedItem.getItemMeta().hasDisplayName()) {
            return;
        }

        String displayName = clickedItem.getItemMeta().getDisplayName();
        Home home = homeManager.getHome(player.getUniqueId(), homeName);

        if (home == null) {
            player.closeInventory();
            player.sendMessage(Main.colorize("&cEsta home não existe mais!"));
            return;
        }

        if (displayName.equals("§a§lTeleportar")) {
            player.closeInventory();
            player.performCommand("home tp " + homeName);
        }
        else if (displayName.equals("§6§lRenomear")) {
            player.closeInventory();
            player.sendMessage(Main.colorize("&aDigite o novo nome para a home no chat:"));
            player.sendMessage(Main.colorize("&7(Digite 'cancelar' para cancelar)"));

            // Armazenar a home que está sendo renomeada
            renamingHomes.put(player.getUniqueId(), home);
        }
        else if (displayName.equals("§c§lExcluir")) {
            player.closeInventory();

            // Confirma a exclusão
            player.sendMessage(Main.colorize("&cVocê tem certeza que deseja excluir a home &e" + homeName + "&c?"));
            player.sendMessage(Main.colorize("&cDigite &econfirmar &cpara confirmar ou &ecancelar &cpara cancelar."));

            // Configurar listener temporário para a confirmação
            Bukkit.getPluginManager().registerEvents(new Listener() {
                @EventHandler
                public void onChat(org.bukkit.event.player.AsyncPlayerChatEvent event) {
                    if (event.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                        event.setCancelled(true);

                        String message = event.getMessage().toLowerCase();
                        if (message.equals("confirmar")) {
                            if (homeManager.deleteHome(player.getUniqueId(), homeName)) {
                                player.sendMessage(Main.colorize("&aHome &e" + homeName + " &aexcluída com sucesso!"));
                            } else {
                                player.sendMessage(Main.colorize("&cHome não encontrada ou já excluída!"));
                            }
                        } else if (message.equals("cancelar")) {
                            player.sendMessage(Main.colorize("&aOperação cancelada."));
                        } else {
                            player.sendMessage(Main.colorize("&cDigite &econfirmar &cou &ecancelar&c."));
                            return;
                        }

                        // Desregistra o listener após o uso
                        HandlerList.unregisterAll(this);
                    }
                }
            }, plugin);
        }
        else if (displayName.equals("§b§lAtualizar Localização")) {
            player.closeInventory();

            // Cria uma nova home com o mesmo nome, sobrescrevendo a anterior
            if (homeManager.createHome(player, homeName)) {
                player.sendMessage(Main.colorize("&aLocalização da home &e" + homeName + " &aatualizada com sucesso!"));
            } else {
                player.sendMessage(Main.colorize("&cNão foi possível atualizar a localização da home!"));
            }
        }
        else if (displayName.equals("§c§lVoltar")) {
            openHomeListMenu(player);
        }
    }

    @EventHandler
    public void onPlayerChat(org.bukkit.event.player.AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        // Verifica se o jogador está no processo de renomear uma home
        if (renamingHomes.containsKey(player.getUniqueId())) {
            event.setCancelled(true);
            String input = event.getMessage();

            // Se o jogador cancelou a operação
            if (input.equalsIgnoreCase("cancelar")) {
                renamingHomes.remove(player.getUniqueId());
                player.sendMessage(Main.colorize("&aOperação cancelada."));
                return;
            }

            // Verifica se o nome contém caracteres válidos
            if (!input.matches("[a-zA-Z0-9_\\-]{1,16}")) {
                player.sendMessage(Main.colorize("&cO nome da home deve conter apenas letras, números, _ e -, e ter no máximo 16 caracteres!"));
                player.sendMessage(Main.colorize("&7Tente novamente ou digite 'cancelar' para cancelar."));
                return;
            }

            // Verificar se é uma nova home ou uma renomeação
            Home home = renamingHomes.get(player.getUniqueId());

            if (home == null) {
                // Criação de nova home
                if (homeManager.createHome(player, input)) {
                    player.sendMessage(Main.colorize("&aHome &e" + input + " &acriada com sucesso!"));
                } else {
                    player.sendMessage(Main.colorize("&cJá existe uma home com este nome ou você atingiu o limite de homes!"));
                }
            } else {
                // Renomeação de home
                String oldName = home.getName();
                if (homeManager.renameHome(player.getUniqueId(), oldName, input)) {
                    player.sendMessage(Main.colorize("&aHome renomeada de &e" + oldName + " &apara &e" + input + "&a!"));
                } else {
                    player.sendMessage(Main.colorize("&cNão foi possível renomear a home! Verifique se já existe uma home com este nome."));
                }
            }

            // Remover o jogador do modo de renomeação
            renamingHomes.remove(player.getUniqueId());
        }
    }

    private ItemStack createGuiItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);

        if (lore.length > 0) {
            meta.setLore(Arrays.asList(lore));
        }

        item.setItemMeta(meta);
        return item;
    }

    private void fillEmptySlots(Inventory inventory, ItemStack item) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR) {
                inventory.setItem(i, item);
            }
        }
    }
}