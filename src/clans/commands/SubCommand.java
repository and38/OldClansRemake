package clans.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import clans.chat.Chat;
import clans.clan.ClanRole;
import clans.player.ClansPlayer;
import clans.player.Players;

public abstract class SubCommand {
	
	
	private static final HashMap<String, List<SubCommand>> commands = new HashMap<String, List<SubCommand>>();
	
	
	static {
		List<SubCommand> list = new ArrayList<SubCommand>();
		list.add(new ClanHome());
		list.add(new ClanCreate());
		list.add(new ClanHelp());
		list.add(new ClanJoin());
		list.add(new ClanInvite());
		list.add(new ClanClaim());
		list.add(new ClanDisband());
		list.add(new ClanAlly());
		list.add(new ClanNeutral());
		list.add(new ClanLeave());
		list.add(new ClanUnclaim());
		commands.put("clan", list);
		List<SubCommand> goldList = new ArrayList<SubCommand>();
		goldList.add(new GoldGive());
		goldList.add(new GoldRemove());
		goldList.add(new GoldSet());
		commands.put("gold", goldList);
		
		//SOme messages:
		//Invite
	}
	
	public static boolean handleCommands(Player player, String cmd, String[] args) {
		for (SubCommand subCmd : SubCommand.registeredSubCommands(cmd)) {
			if (subCmd.getName().equalsIgnoreCase(args[0])) {
				subCmd.onCommand(player, args);
				return true;
			} 
		}
		return false;
	}
	
	public static List<SubCommand> registeredSubCommands(String command) {
		List<SubCommand> ret = new ArrayList<SubCommand>();
		for (String str : commands.keySet()) {
			if (str.equalsIgnoreCase(command)) {
				ret.addAll(commands.get(str));
			}
		}
		return ret;
	}
	
	//Add clan permissions and normal perms.
	private String subcommand;
	protected ClanRole roleReq;
	private String[] reqArgs;
	private String[] opArgs;

	public SubCommand(String subcommand, String[] reqArgs, String[] opArgs, ClanRole roleReq) {
		this.subcommand = subcommand;
		this.reqArgs = reqArgs;
		this.opArgs = opArgs;
		this.roleReq = roleReq;
	}
	
	public abstract void onCommand(Player player, String[] args);
	
	public String getName() {
		return subcommand;
	}

	public String[] getReqArgs() {
		return reqArgs;
	}

	public String[] getOpArgs() {
		return opArgs;
	}

	public static void unload() {
		commands.clear();
	}
	
	public ChatColor getColor() {
		switch(roleReq) {
		case LEADER:
			return ChatColor.DARK_RED;
		case ADMIN:
			return ChatColor.GOLD;
		case MEMBER:
			return ChatColor.WHITE;
		default:
			return ChatColor.WHITE;
		}
	}
	
	public String getArgsString() {
		StringBuilder strBuilder = new StringBuilder("");
		for (int i = 0; i < reqArgs.length; i++) {
			strBuilder.append("<" + reqArgs[i] + "> ");
		}
		for (int i = 0; i < opArgs.length; i++) {
			strBuilder.append("(" + opArgs[i] + ") ");
		}
		
		return strBuilder.toString();
		
	}
	
	protected boolean checkRole(Player player) {
		ClansPlayer cPlayer = Players.getInstance().getCPlayerFrom(player);
		if (roleReq.ordinal() > cPlayer.getRole().ordinal()) {
			return false;
		}
		return true;
	}

	public ClanRole getRole() {
		return roleReq;
	}

	public String getDescription() {
		return this.subcommand + " " + getArgsString();
	}
	
}
