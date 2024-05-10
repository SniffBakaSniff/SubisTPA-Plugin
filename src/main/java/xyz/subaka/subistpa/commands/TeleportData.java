package xyz.subaka.subistpa.commands;

import java.util.UUID;

public class TeleportData {
    private final UUID senderUUID;
    private final UUID receiverUUID;
    private final TeleportType type;

    public TeleportData(UUID senderUUID, UUID receiverUUID, TeleportType type) {
        this.senderUUID = senderUUID;
        this.receiverUUID = receiverUUID;
        this.type = type;
    }

    @Override
    public String toString() {
        return "TeleportData{senderUUID=" + senderUUID + ", receiverUUID=" + receiverUUID + ", teleportType=" + type + "}";
    }

    public UUID getSenderUUID() {
        return senderUUID;
    }

    public UUID getReceiverUUID() {
        return receiverUUID;
    }

    public TeleportType getType() {
        return type;
    }
}
