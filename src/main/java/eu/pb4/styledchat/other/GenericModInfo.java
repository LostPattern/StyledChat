package eu.pb4.styledchat.other;

import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.neoforged.fml.ModContainer;

import javax.imageio.ImageIO;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GenericModInfo {
    private static Text[] icon = new Text[0];
    private static Text[] about = new Text[0];
    private static Text[] consoleAbout = new Text[0];

    public static void build(ModContainer container) {
        boolean useIcon = true;

        {
            final String chr = "█";
            var icon = new ArrayList<MutableText>();
            try {
                var source = ImageIO.read(Objects.requireNonNull(GenericModInfo.class.getResourceAsStream("/assets/icon_small.png")));

                for (int y = 0; y < source.getHeight(); y++) {
                    var base = Text.literal("");
                    int line = 0;
                    int color = source.getRGB(0, y) & 0xFFFFFF;
                    for (int x = 0; x < source.getWidth(); x++) {
                        int colorPixel = source.getRGB(x, y) & 0xFFFFFF;

                        if (color == colorPixel) {
                            line++;
                        } else {
                            base.append(Text.literal(chr.repeat(line)).setStyle(Style.EMPTY.withColor(color)));
                            color = colorPixel;
                            line = 1;
                        }
                    }

                    base.append(Text.literal(chr.repeat(line)).setStyle(Style.EMPTY.withColor(color)));
                    icon.add(base);
                }
            } catch (Throwable e) {
                useIcon = false;
                e.printStackTrace();
                while (icon.size() < 16) {
                    icon.add(Text.literal("/!\\ [ Invalid icon file ] /!\\").setStyle(Style.EMPTY.withColor(0xFF0000).withItalic(true)));
                }
            }

            GenericModInfo.icon = icon.toArray(new Text[0]);
        }

        {
            var about = new ArrayList<Text>();
            var aboutBasic = new ArrayList<Text>();
            var output = new ArrayList<Text>();

            try {
                about.add(Text.literal(container.getModInfo().getDisplayName()).setStyle(Style.EMPTY.withColor(Formatting.YELLOW).withBold(true).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/LostPattern/StyledChat"))));
                about.add(Text.translatable("Version: ").setStyle(Style.EMPTY.withColor(0xf7e1a7))
                        .append(Text.literal(container.getModInfo().getVersion().toString()).setStyle(Style.EMPTY.withColor(Formatting.WHITE))));

                aboutBasic.addAll(about);
                aboutBasic.add(Text.empty());
                aboutBasic.add(Text.of(container.getModInfo().getDescription()));

                var contributors = new ArrayList<String>();
                contributors.add((String) container.getModInfo().getConfig().getConfigElement("authors").get());

                about.add(Text.literal("")
                        .append(Text.translatable("Contributors")
                                .setStyle(Style.EMPTY.withColor(Formatting.AQUA)
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                Text.literal(String.join(", ", contributors)
                                        ))
                                )))
                        .append("")
                        .setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY)));
                about.add(Text.empty());

                var desc = new ArrayList<>(List.of(container.getModInfo().getDescription().split(" ")));

                if (desc.size() > 0) {
                    StringBuilder descPart = new StringBuilder();
                    while (!desc.isEmpty()) {
                        (descPart.isEmpty() ? descPart : descPart.append(" ")).append(desc.remove(0));

                        if (descPart.length() > 16) {
                            about.add(Text.literal(descPart.toString()).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
                            descPart = new StringBuilder();
                        }
                    }

                    if (descPart.length() > 0) {
                        about.add(Text.literal(descPart.toString()).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
                    }
                }

                if (icon.length > about.size() + 2 && useIcon) {
                    int a = 0;
                    for (int i = 0; i < icon.length; i++) {
                        if (i == (icon.length - about.size() - 1) / 2 + a && a < about.size()) {
                            output.add(icon[i].copy().append(Text.literal("  ").setStyle(Style.EMPTY.withItalic(false)).append(about.get(a++))));
                        } else {
                            output.add(icon[i]);
                        }
                    }
                } else {
                    Collections.addAll(output, icon);
                    output.addAll(about);
                }
            } catch (Exception e) {
                e.printStackTrace();
                var invalid = Text.literal("/!\\ [ Invalid about mod info ] /!\\").setStyle(Style.EMPTY.withColor(0xFF0000).withItalic(true));

                output.add(invalid);
                about.add(invalid);
            }

            GenericModInfo.about = output.toArray(new Text[0]);
            GenericModInfo.consoleAbout = aboutBasic.toArray(new Text[0]);
        }
    }

    public static Text[] getIcon() {
        return icon;
    }

    public static Text[] getAboutFull() {
        return about;
    }

    public static Text[] getAboutConsole() {
        return consoleAbout;
    }
}
