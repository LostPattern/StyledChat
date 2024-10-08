package eu.pb4.styledchat;

import eu.pb4.styledchat.mixin.PermissionAPIAccessor;
import net.minecraft.server.command.ServerCommandSource;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;

import java.util.function.Predicate;

public class Permissions {
    public static Predicate<ServerCommandSource> require(String permission, boolean isDefault) {
        return source -> isDefault || check(source,permission, 5);
    }

    public static Predicate<ServerCommandSource> require(String permission, int level) {
        return source -> check(source, permission, level);
    }

    public static boolean check(ServerCommandSource source, String perm, int lvl) {
        if (source.getPlayer() == null) {
            return source.hasPermissionLevel(lvl);
        }

        var s = perm.split("\\.", 2);
        return source.hasPermissionLevel(lvl) || PermissionAPIAccessor.getActiveHandler().getPermission(source.getPlayer(), new PermissionNode<>(s[0], s[1], PermissionTypes.BOOLEAN, (arg, uUID, permissionDynamicContexts) -> false));
    }
}
