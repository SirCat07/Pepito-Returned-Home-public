package game.items;

import java.awt.image.BufferedImage;
import java.util.*;

public class Item {
    private final BufferedImage icon;
    private String name = "mc chicken";
    private String description = "yummy";
    private String keybind = ""; // ENTIRELY VISUAL
    private short amount = -1;
    private short defaultAmount = -1;
    private boolean enabled = false;
    private boolean selected = false;
    private String id = "mcchicken";
    private final Set<ItemTag> tags = new LinkedHashSet<>();
    private final Set<Item> conflicts = new HashSet<>();

    public Item(BufferedImage icon, String name, String description, int amount, String id, String keybind) {
        this.icon = icon;
        this.name = name;
        this.description = description;
        this.amount = (short) amount;
        this.defaultAmount = (short) amount;
        this.id = id;
        this.keybind = keybind;
    }

    public BufferedImage getIcon() {
        return icon;
    }

    public void select() {
        selected = true;
    }
    public void deselect() {
        selected = false;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public void enable() {
        enabled = true;
    }
    public void disable() {
        enabled = false;
    }
    public boolean isEnabled() {
        return enabled;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getKeybind() {
        return keybind;
    }

    public Item addTags(List<ItemTag> tags) {
        this.tags.addAll(tags);
        return this;
    }
    public void addConflicts(List<Item> conflicts) {
        tags.add(ItemTag.CONFLICTS);
        this.conflicts.addAll(conflicts);
    }
    public void addConflicts(Set<Item> conflicts) {
        tags.add(ItemTag.CONFLICTS);
        this.conflicts.addAll(conflicts);
    }
    public Item setItemLimitAdd(byte add) {
        this.itemLimitAdd = add;
        return this;
    }
    byte itemLimitAdd = 0;
    public Set<ItemTag> getTags() {
        return tags;
    }
    public Set<Item> getConflicts() {
        return conflicts;
    }

    public int getAmount() {
        return amount;
    }
    public int getDefaultAmount() {
        return defaultAmount;
    }
    public void setAmount(int amount) {
        this.amount = (short) amount;
    }
    public void add(int amount) {
        this.amount += (short) amount;
    }
    public void safeAdd(int amount) {
        if(this.amount < 0)
            return;
        this.amount += (short) amount;
    }
    public void remove(int amount) {
        this.amount -= (short) amount;
    }

    public String getId() {
        return id;
    }

    public String getStringAmount() {
        String stringAmount = String.valueOf(amount);
        if (amount < 0) {
            stringAmount = "∞";
        }

        return stringAmount;
    }

    public byte getItemLimitAdd() {
        return itemLimitAdd;
    }

    public boolean isInfinite() {
        return amount < 0;
    }

    boolean markedConflicting = false;
    public void setMarkedConflicting(boolean marked) {
        this.markedConflicting = marked;
    }
    public boolean isMarkedConflicting() {
        return markedConflicting;
    }

    byte shakeIntensity = 0;
    public byte getShakeIntensity() {
        return shakeIntensity;
    }
    public void setShakeIntensity(byte shakeIntensity) {
        this.shakeIntensity = shakeIntensity;
    }

    public void assignText(String name, String desc) {
        this.name = name;
        this.description = desc;
    }
}